package org.universal.exporter.utils.abstracts;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.faces.RepairIngredient;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.*;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType;
import org.universal.exporter.command.type.AdvancementParamType;
import org.universal.exporter.serializable.Util;
import org.universal.exporter.utils.SimpleLanguage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType.armorType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ToolType.toolType;
import static org.universal.exporter.utils.ItemAndBlockUtils.*;

public class ItemAndBlockHelper extends BasicHelper {
    private final Path itemAndBlockExporter;
    private final ArrayList<ItemGroup> groups = new ArrayList<>();

    public ItemAndBlockHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx, AdvancementParamType... types) {
        super(modid, advancement, ctx, types);
        itemAndBlockExporter = modidDir.resolve("item-and-block.json");

    }

    public void saveItemExporter() {
//        items.forEach(this::setItems);
//        noShowItems.forEach(this::setItems);//导出除tab之外的注册表内的物品
//        recipeItems.forEach(this::setItems);
        blockAndItems.save(itemAndBlockExporter);
    }

    public void registryItemExporter() {
        for (Identifier id : Registries.ITEM.getIds()) {
            checkPut(id, id.asStack(), null);
        }
    }

    public void fluidRegistryBlockExporter() {
        for (Identifier id : Registries.FLUID.getIds()) {
           checkPutFluid(id);
        }
    }

    public void checkPutFluid(Identifier id) {
        if (!id.getNamespace().equals(modid)) return;
        Fluid fluid = Registries.FLUID.get(id);
        FluidState defaultState = fluid.getDefaultState();

        BlockAndItemSerializable serializable = new BlockAndItemSerializable();
        blockAndItems.fluid(id.toString(), serializable);
        serializable.type = new ItemType();
        initBlock(defaultState.getBlockState().getBlock(), serializable);
        serializable.type.asBlock.asFluid = new FluidType();
        serializable.type.asBlock.asFluid.asBucket = Registries.ITEM.getId(fluid.getBucketItem()).toString();
        serializable.type.asBlock.asFluid.isSource = defaultState.isStill();
        serializable.englishName = SimpleLanguage.en_us.get(defaultState.getBlockState().getBlock().getTranslationKey());
        serializable.name = Language.getInstance().get(defaultState.getBlockState().getBlock().getTranslationKey());
        if (fluid instanceof FlowableFluid flowableFluid) {
            Util.fluidType(serializable.type.asBlock.asFluid, flowableFluid);
        }
    }

    //优先级 流体>方块>桶>盔甲>工具>刷怪蛋>食物>燃料>普通物品
    public void checkPut(Identifier id, ItemStack stack, ItemGroup group) {
        if (!id.getNamespace().equals(modid)) return;
        Item item = stack.getItem();
        BlockAndItemSerializable serializable = new BlockAndItemSerializable();
        setItemName(stack, serializable);
        initItem(stack, serializable, group);
        initFoodSettings(serializable, item);
        initBlock(item instanceof BlockItem blockItem ? blockItem.getBlock() : null, serializable);
        initBucketItem(item instanceof BucketItem bucketItem ? bucketItem : null, serializable);
        initArmor(item instanceof ArmorItem armorItem ? armorItem : null, serializable);
        initToolItem(item instanceof ToolItem toolItem ? toolItem : null, serializable);
        serializable.type.fuelTime = FurnaceBlockEntity.createFuelTimeMap().get(item);
        initSpawnEggItem(item instanceof SpawnEggItem spawnEggItem ?spawnEggItem : null, serializable);
        String registryName = id.toString();
        if (item instanceof BlockItem) {
            blockAndItems.block(registryName, serializable);
        } else if (item instanceof BucketItem) {
            blockAndItems.bucket(registryName, serializable);
        } else if (item instanceof ArmorItem) {
            blockAndItems.armor(registryName, serializable);
        } else if (item instanceof ToolItem) {
            blockAndItems.tool(registryName, serializable);
        } else if (item instanceof SpawnEggItem) {
            blockAndItems.spawnEgg(registryName, serializable);
        } else if (item.isFood()) {
            blockAndItems.food(registryName, serializable);
        } else if (serializable.type.fuelTime > 0) {
            blockAndItems.fuel(registryName, serializable);
        } else {
            blockAndItems.item(registryName, serializable);
        }
    }

    private void initBucketItem(BucketItem bucketItem, BlockAndItemSerializable serializable) {
        if (bucketItem == null) return;
        serializable.type.asFluid(Registries.FLUID.getId(bucketItem.fluid).toString());
    }

    private void initFoodSettings(BlockAndItemSerializable serializable, Item item) {
        if (!item.isFood()) return;
        serializable.type.asFood = new FoodType();
        FoodComponent foodComponent = item.getFoodComponent();
        assert foodComponent != null;
        serializable.type.asFood.hunger = foodComponent.getHunger();
        serializable.type.asFood.saturationModifier = foodComponent.getSaturationModifier();
        serializable.type.asFood.meat = foodComponent.isMeat();
        serializable.type.asFood.alwaysEdible = foodComponent.isAlwaysEdible();
        serializable.type.asFood.snack = foodComponent.isSnack();
        runAdvanceParams(AdvancementParamType.status_effects_food, () -> {
            for (Pair<StatusEffectInstance, Float> statusEffect : foodComponent.statusEffects) {
                StatusEffectInstance first = statusEffect.getFirst();
                StatusEffectInstanceType type = statusEffectInstanceInit(first);
                serializable.type.asFood.statusEffects(type, statusEffect.getSecond());
            }
        });



    }

    @NotNull
    private StatusEffectInstanceType statusEffectInstanceInit(StatusEffectInstance first) {
        StatusEffectInstanceType type = new StatusEffectInstanceType();
        type.type = new StatusEffectType();
        type.type.color = first.type.getColor();

        first.type.getFactorCalculationDataSupplier().ifPresent(data -> {
            type.type.factorCalculationDataSupplier = new FactorCalculationDataType();
            type.type.factorCalculationDataSupplier.paddingDuration = data.paddingDuration;
            type.type.factorCalculationDataSupplier.factorStart = data.factorStart;
            type.type.factorCalculationDataSupplier.factorTarget = data.factorTarget;
            type.type.factorCalculationDataSupplier.factorCurrent = data.factorCurrent;
            type.type.factorCalculationDataSupplier.effectChangedTimestamp = data.effectChangedTimestamp;
            type.type.factorCalculationDataSupplier.factorPreviousFrame = data.factorPreviousFrame;
            type.type.factorCalculationDataSupplier.hadEffectLastTick = data.hadEffectLastTick;
        });

        type.duration = first.duration;
        type.amplifier = first.amplifier;
        type.ambient = first.ambient;
        type.showParticles = first.showParticles;
        type.showIcon = first.showIcon;
        if (first.hiddenEffect != null) {
            type.hiddenEffect = statusEffectInstanceInit(first.hiddenEffect);
        }
        return type;
    }

    private void initSpawnEggItem(SpawnEggItem spawnEggItem, BlockAndItemSerializable serializable) {
        if (spawnEggItem == null) return;
        serializable.type.asEgg = new SpawnType();
        serializable.type.asEgg.primaryColor = spawnEggItem.getColor(0);
        serializable.type.asEgg.secondaryColor = spawnEggItem.getColor(1);
        serializable.type.asEgg.type = Registries.ENTITY_TYPE.getId(spawnEggItem.type).toString();
    }

    private void initToolItem(ToolItem toolItem, BlockAndItemSerializable serializable) {
        if (toolItem == null) return;
        serializable.type.tool = toolType(toolType -> {
            toolType.miningLevel = toolItem.getMaterial().getMiningLevel();
            toolType.miningSpeed = toolItem.getMaterial().getMiningSpeedMultiplier();
            toolType.enchantability = toolItem.getEnchantability();
            for (ItemStack matchingStack : toolItem.getMaterial().getRepairIngredient().getMatchingStacks()) {
                NbtCompound nbt = matchingStack.getNbt();
                toolType.repairIngredient(matchingStack.getId().toString(), nbt == null ? null : NbtType.nbtType(nbtType -> {
                    nbt(nbt, nbtType);
                }));
            }
            if (toolItem instanceof MiningToolItem miningToolItem) {
                toolType.tagId = miningToolItem.effectiveBlocks.id().toString();
                toolType.attackDamage = miningToolItem.getAttackDamage();
            } else {
                toolType.attackDamage = toolItem.getMaterial().getAttackDamage();
            }
            setRepairIngredient(toolItem.getMaterial().getRepairIngredient(), toolType);
        });
    }

    private void initArmor(ArmorItem armorItem, BlockAndItemSerializable serializable) {
        if (armorItem == null) return;
        serializable.type.armor = armorType(armorType -> {
            armorType.type = armorItem.getType().getName();
            armorType.equipmentSlot = armorItem.getType().getEquipmentSlot().getName();
            armorType.enchantability = armorItem.getEnchantability();
            armorType.protection = armorItem.getProtection();
            armorType.toughness = armorItem.getToughness();
            armorType.knockbackResistance = armorItem.getMaterial().getKnockbackResistance();
            setRepairIngredient(armorItem.getMaterial().getRepairIngredient(), armorType);
        });
    }

    private void setRepairIngredient(Ingredient ingredient, RepairIngredient<?> armorType) {
        for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
            String string = Registries.ITEM.getId(matchingStack.getItem()).toString();
            NbtType nbt = new NbtType();
            if (matchingStack.getNbt() != null) {
                nbt(matchingStack.getNbt(), nbt);
            }
            armorType.repairIngredient(string, nbt);
        }
    }

    private void initBlock(Block block, BlockAndItemSerializable serializable) {
        if (block == null) return;
        BlockState defaultState = block.getDefaultState();
        serializable.type.asBlock = blockType(blockType -> {
            blockType.luminance = defaultState.getLuminance();
            blockType.isFull = Block.isShapeFullCube(defaultState.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));

            blockType.hardness = defaultState.getHardness(null, null);
            blockType.toolRequired = defaultState.isToolRequired();
            blockType.resistance = block.getBlastResistance();

            blockType.randomTicks = defaultState.hasRandomTicks();
            blockType.lootTableId = block.getLootTableId().toString();
            runAdvanceParams(AdvancementParamType.block_level_1, () -> {
                //空气 透明度 可被其他方块替换(草) 可燃 光滑度
                blockType.isAir = defaultState.isAir();
                blockType.opaque = defaultState.isOpaque();
                blockType.replaceable = defaultState.isReplaceable();
                blockType.burnable = defaultState.isBurnable();
                blockType.slipperiness = block.getSlipperiness();
            });
            runAdvanceParams(AdvancementParamType.block_level_2, () -> {
                //起跳性质，速度性质
                blockType.velocityMultiplier = block.getVelocityMultiplier();
                blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();
            });
            runAdvanceParams(AdvancementParamType.block_level_3, () -> {
                blockType.collidable = block.collidable;
                blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
            });
            runAdvanceParams(AdvancementParamType.block_level_4, () -> {
                //第四序列,弃用方法的高级导出
                blockType.liquid = defaultState.isLiquid();
                blockType.solid = defaultState.isSolid();
            });

            blockType.blockBreakParticles = defaultState.hasBlockBreakParticles();





        });
    }

    public void itemExporter() {
        itemGroupItemExporter();
        registryItemExporter();
        recipesItemExporter();
        fluidRegistryBlockExporter();
        saveItemExporter();
    }

    public void recipesItemExporter() {
        for (Recipe<?> recipe : ctx.getSource().getServer().getRecipeManager().values()) {
            ItemStack output = recipe.getOutput(ctx.getSource().getRegistryManager());
            Identifier id = Registries.ITEM.getId(output.getItem());
            checkPut(id, output, null);
//            put(id, new ItemStackHelper(output, null, advancement), recipeItems);
        }
    }

    public void itemGroupItemExporter() {
        MinecraftClient instance = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
        if (networkHandler == null || instance.world == null) return;
        DynamicRegistryManager registryManager = instance.world.getRegistryManager();
        ItemGroups.updateDisplayContext(networkHandler.getEnabledFeatures(), true, registryManager);
        ArrayList<ItemGroup> groups = getGroups();
        for (ItemGroup group : ItemGroups.getGroups()) {
            if (!groups.contains(group)) {
                Collection<ItemStack> displayStacks = group.getDisplayStacks();
                for (ItemStack displayStack : displayStacks) {
                    Identifier id = Registries.ITEM.getId(displayStack.getItem());
                    checkPut(id, displayStack, group);
//                    put(id, new ItemStackHelper(displayStack, group, advancement), items);
                }
            }
        }

    }

    public void initItem(ItemStack stack, BlockAndItemSerializable serializable, ItemGroup group) {
        defaultItemProperties(stack, serializable, group, ctx);
//        ItemAndBlockUtils.tabPut(group, serializable);

    }

    public void defaultItemProperties(ItemStack stack,
                                             BlockAndItemSerializable blockAndItem,
                                             ItemGroup group, CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        blockAndItem.type = itemType(itemType -> {
            tooltipSet(stack, itemType, player);
            itemType.maxStackSize = stack.getItem().getMaxCount();
            itemType.maxDurability = stack.getItem().getMaxDamage();
            stack.streamTags().forEach(itemTagKey -> {
                itemType.OredictList(itemTagKey.id().toString());
            });
            groupSet(group, itemType);
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) {
                itemType.nbt = new NbtType();
                nbt(nbt, itemType.nbt);
            }
        });
    }

    private void groupSet(ItemGroup group, ItemType itemType) {
        if (group != null) {
            Text displayName = group.getDisplayName();
            itemType.tab = new NameType();
            itemType.tab.englishName = get(displayName, true, itemType.tab);
            itemType.tab.name = get(displayName, false, itemType.tab);
        }
    }

    private void tooltipSet(ItemStack stack, ItemType itemType, ServerPlayerEntity player) {
        runAdvanceParams(AdvancementParamType.basic_tooltip, () -> {
            List<Text> basic = stack.getTooltip(player, TooltipContext.BASIC);

            basic.forEach(text -> {
                NameType basicTooltip = new NameType();
                basicTooltip.englishName = get(text, true, basicTooltip);
                basicTooltip.name = get(text, false, basicTooltip);
                itemType.basicTooltip(basicTooltip);
            });
        });
        runAdvanceParams(AdvancementParamType.advance_tooltip, () -> {
            List<Text> tooltip1 = stack.getTooltip(player, TooltipContext.ADVANCED);
            tooltip1.forEach(text -> {
                NameType advanceToolTip = new NameType();
                advanceToolTip.englishName = get(text, true, advanceToolTip);
                advanceToolTip.name = get(text, false, advanceToolTip);
                itemType.basicTooltip(advanceToolTip);
            });
        });
    }

    public ArrayList<ItemGroup> getGroups() {
        if (groups.isEmpty())
            groups.addAll(ImmutableList.of(ItemGroups.HOTBAR, ItemGroups.INVENTORY, ItemGroups.SEARCH).stream().map(Registries.ITEM_GROUP::get).toList());
        return groups;
    }
}
