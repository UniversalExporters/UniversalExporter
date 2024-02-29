package org.universal.exporter.utils.abstracts;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.faces.RepairIngredient;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FoodType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.SpawnType;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType;
import org.universal.exporter.utils.ItemAndBlockUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType.armorType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ToolType.toolType;
import static org.universal.exporter.utils.ItemAndBlockUtils.nbt;
import static org.universal.exporter.utils.ItemAndBlockUtils.setItemName;

public class ItemAndBlockHelper extends BasicHelper {
    private final Path itemAndBlockExporter;
    private final ArrayList<ItemGroup> groups = new ArrayList<>();

    public ItemAndBlockHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        super(modid, advancement, ctx);
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
    //优先级 流体>方块>桶>盔甲>工具>刷怪蛋>食物>普通物品
    public void checkPut(Identifier id, ItemStack stack, ItemGroup group) {
        Item item = stack.getItem();
        BlockAndItemSerializable serializable = new BlockAndItemSerializable();
        setItemName(stack, serializable);
        initItem(stack, serializable, group);
        if (item instanceof BlockItem blockItem) {
            initBlock(blockItem, serializable);
            blockAndItems.block(id.toString(), serializable);
        } else if (item instanceof BucketItem bucketItem) {
            serializable.type.asFluid = Registries.FLUID.getId(bucketItem.fluid).toString();
            blockAndItems.bucket(id.toString(), serializable);
        } else if (item instanceof ArmorItem armorItem) {
            initArmor(armorItem, serializable);
            blockAndItems.armor(id.toString(), serializable);
        } else if (item instanceof ToolItem toolItem) {
            initToolItem(toolItem, serializable);
            blockAndItems.tool(id.toString(), serializable);
        } else if (item instanceof SpawnEggItem spawnEggItem) {
            initSpawnEggItem(spawnEggItem, serializable);
            blockAndItems.spawnEgg(id.toString(), serializable);
        } else if (item.isFood()) {
            initFoodSettings(serializable, item);
            blockAndItems.food(id.toString(), serializable);
        } else {
            blockAndItems.item(id.toString(), serializable);
        }
    }

    private void initFoodSettings(BlockAndItemSerializable serializable, Item item) {
        serializable.type.asFood = new FoodType();
        FoodComponent foodComponent = item.getFoodComponent();
        assert foodComponent != null;
        serializable.type.asFood.hunger = foodComponent.getHunger();
        serializable.type.asFood.saturationModifier = foodComponent.getSaturationModifier();
        serializable.type.asFood.meat = foodComponent.isMeat();
        serializable.type.asFood.alwaysEdible = foodComponent.isAlwaysEdible();
        serializable.type.asFood.snack = foodComponent.isSnack();
        if (advancement) {
            for (Pair<StatusEffectInstance, Float> statusEffect : foodComponent.statusEffects) {
                StatusEffectInstance first = statusEffect.getFirst();
                StatusEffectInstanceType type = statusEffectInstanceInit(first);
                serializable.type.asFood.statusEffects(type, statusEffect.getSecond());
            }
        }
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
        serializable.type.asEgg = new SpawnType();
        serializable.type.asEgg.primaryColor = spawnEggItem.getColor(0);
        serializable.type.asEgg.secondaryColor = spawnEggItem.getColor(1);
        serializable.type.asEgg.type = Registries.ENTITY_TYPE.getId(spawnEggItem.type).toString();
    }

    private void initToolItem(ToolItem toolItem, BlockAndItemSerializable serializable) {
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

    private void initBlock(BlockItem blockItem, BlockAndItemSerializable serializable) {
        Block block = blockItem.getBlock();
        BlockState defaultState = block.getDefaultState();
        serializable.type.asBlock = blockType(blockType -> {
            blockType.luminance = defaultState.getLuminance();
            blockType.isFull = Block.isShapeFullCube(defaultState.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));

            if (advancement) {
                blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
                blockType.isAir = defaultState.isAir();
                blockType.liquid = defaultState.isLiquid();
                blockType.solid = defaultState.isSolid();
                blockType.opaque = defaultState.isOpaque();
                blockType.blockBreakParticles = defaultState.isOpaque();
                blockType.replaceable = defaultState.isOpaque();
                blockType.burnable = defaultState.isBurnable();
                blockType.slipperiness = block.getSlipperiness();
                blockType.velocityMultiplier = block.getVelocityMultiplier();
                blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();
                blockType.collidable = block.collidable;
            }

            blockType.hardness = defaultState.getHardness(null, null);
            blockType.toolRequired = defaultState.isToolRequired();
            blockType.resistance = block.getBlastResistance();

            blockType.randomTicks = defaultState.hasRandomTicks();
            blockType.lootTableId = block.getLootTableId().toString();
        });
    }

    public void itemExporter() {
        itemGroupItemExporter();
        registryItemExporter();
        recipesItemExporter();
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
        ItemAndBlockUtils.defaultItemProperties(stack, serializable, group, ctx, advancement);
//        ItemAndBlockUtils.tabPut(group, serializable);

    }

    public ArrayList<ItemGroup> getGroups() {
        if (groups.isEmpty())
            groups.addAll(ImmutableList.of(ItemGroups.HOTBAR, ItemGroups.INVENTORY, ItemGroups.SEARCH).stream().map(Registries.ITEM_GROUP::get).toList());
        return groups;
    }
}
