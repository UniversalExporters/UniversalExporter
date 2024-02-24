package org.universal.exporter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.utils.AdvancementHelper;
import org.universal.exporter.utils.CommandHelper;
import org.universal.exporter.utils.ItemAndBlockHelper;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable.blockAndItemSerializable;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FoodType.foodType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType.nbtType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ToolType.toolType;
import static org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType.factorCalculationDataType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType.statusEffectInstanceType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType.statusEffectType;
import static org.universal.exporter.command.argument.ExporterArgumentType.getExporter;
import static org.universal.exporter.command.argument.ModidArgumentType.getModidType;
import static org.universal.exporter.utils.Base64Helper.icon;
import static org.universal.exporter.utils.ItemAndBlockUtils.defaultItemProperties;
import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;

/**
 * uex exporter command
 * @author baka4n
 * @author QWERTY770
 */
public class ExporterCommand extends CommandHelper implements Serializable {
    @Serial
    private static final long serialVersionUID = -3255258476049849876L;

    public static final ExecutorService executorService = Executors.newWorkStealingPool();//线程池子
    //命令头
    public static final LiteralArgumentBuilder<ServerCommandSource> uex = literal("uex");
    //带选择的命令
    public static final  RequiredArgumentBuilder<ServerCommandSource, ExporterType> select = argument("select", ExporterArgumentType.exporter());
    public static final RequiredArgumentBuilder<ServerCommandSource, Boolean> advanceParameters = argument("advance-parameters", BoolArgumentType.bool());
    public static final RequiredArgumentBuilder<ServerCommandSource, ModidType> modid = argument("modid", ModidArgumentType.modids());

    private final ExporterType this$select;
    private final Boolean this$advanceParameters;
    private static final Path exporter = UniExporterExpectPlatform.getGameFolder().resolve("exporter");

    private ExporterCommand(CommandContext<ServerCommandSource> context, ExporterType select, ModidType modid, boolean advanceParameters) {
        super(modid, context);
        this$select = select;
        this$advanceParameters = advanceParameters;
    }

    /**
     * Register Server Command
     * @param dispatcher adapter
     * @param registryAccess access
     * @param env environment
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(
                uex
                        .executes(context ->getInstance(context, false, false, false))
                        .then(
                                modid
                                        .executes(context -> getInstance(context, false, true, false))
                                        .then(advanceParameters.executes(context -> getInstance(context, false, true, true)))
                        )
                        .then(advanceParameters.executes(context -> getInstance(context, false, false, true)))
                        .then(
                                select
                                        .executes(context -> getInstance(context, true, false, false))
                                        .then(advanceParameters.executes(context -> getInstance(context, true, false, true)))
                                        .then(
                                                modid
                                                        .executes(context -> getInstance(context, true, true, false))
                                                        .then(advanceParameters.executes(context -> getInstance(context, true, true, true)))
                                        )
                        )
        );

    }

    // uex √
    // uex advance-parameters √
    // uex select √
    // uex select modid √
    // uex modid √
    // uex modid advance-parameters √
    // uex select advance-parameters √
    // uex select modid advance-parameters √

    public static int getInstance(CommandContext<ServerCommandSource> context, boolean select, boolean modid, boolean advanceParameters) {
        return new ExporterCommand(context, select ? getExporter(context, "select") : null, modid ? getModidType(context, "modid") : null, advanceParameters && BoolArgumentType.getBool(context, "advance-parameters")).all();
    }




    public static int defaultCommandSources() {
        return 1;
    }

    public static <T extends ArgumentBuilder<ServerCommandSource, T>, R extends ArgumentBuilder<ServerCommandSource, T>> T executes(R r, Command<ServerCommandSource> sourceCommand) {
        return r.executes(sourceCommand);
    }

    public int all() {
        if (this$select == null) {
            itemAndBlockExporterAll();
            advancementsAll();
        } else {
            if (this$select.equals(ExporterType.itemandblock)) {
                itemAndBlockExporterAll();
            } else if (this.this$select.equals(ExporterType.advancements)) {
                advancementsAll();
            }
        }

        return defaultCommandSources();
    }


    public void advancementModId(String modid) {
        Path advancementsJson = exporter.resolve(modid).resolve("advancements.json");
        List<Advancement> advancements = context.getSource().getServer().getAdvancementLoader().getAdvancements().stream().toList();

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            List<Advancement> parents = advancements.stream().filter(advancement -> advancement.getParent() == null).toList();

            Advancements modidAdvancements = new Advancements();
            advancements.stream().filter(advancement -> advancement.getParent() == null).forEachOrdered(parent -> {
                new AdvancementHelper(parent.getId().toString(), this$advanceParameters).advancementSet(parent, modidAdvancements);
            });

            subAdvancementSet(modidAdvancements, advancements);
            modidAdvancements.save(advancementsJson);
        });
        try {
            Void unused = CompletableFuture.allOf(voidCompletableFuture).get();
        } catch (InterruptedException | ExecutionException ignored) {

        }
    }

    private void subAdvancementSet(Advancements modidAdvancements, List<Advancement> advancements) {
        modidAdvancements.advancements.forEach((registerName, serializable) -> {
            advancements.stream()
                    .filter(advancement -> advancement.getParent() != null && advancement.getParent().getId().toString().equals(registerName))
                    .forEachOrdered(subAdvancement -> {
                        serializable.children(advancements1 -> {
                            new AdvancementHelper(subAdvancement.getId().toString(), this$advanceParameters).advancementSet(subAdvancement, modidAdvancements);
                        });
                        subAdvancementSet(serializable.children, advancements);
                    });
        });
    }

    /**
     * Exporting all advancement
     */
    public void advancementsAll() {
        if (this$modid != null) {
            advancementModId(this$modid.asString());
        } else {
            Arrays.stream(ModidType.values()).map(ModidType::name).forEach(this::advancementModId);
        }
    }

    @NotNull
    public static TagKey<Item> findAllOredicts(ItemType type, TagKey<Item> itemTagKey) {
        if (type.OredictList == null) type.OredictList = new ArrayList<>();
        type.OredictList.add(itemTagKey.id().toString());
        return itemTagKey;
    }

    public static void defaultSetItemSettings(ItemStack stack, ItemType type) {
        type.maxStackSize = stack.getItem().getMaxCount();
        type.maxDurability = stack.getItem().getMaxDamage();
        TagKey.codec(RegistryKeys.ITEM).map(itemTagKey -> {
            if (type.OredictList == null) type.OredictList = new ArrayList<>();
            type.OredictList.add(itemTagKey.id().toString());
            return itemTagKey;
        });
        type.icon(icon().itemStackToBase(stack));

    }

    public static void language(NameType blockAndItem, ItemStack item) {
        en_us().get(blockAndItem, item.getTranslationKey());
        zh_cn().get(blockAndItem, item.getTranslationKey());

    }

    public static void language(NameType blockAndItem, String translationKey) {
        en_us().get(blockAndItem, translationKey);
        zh_cn().get(blockAndItem, translationKey);
    }

    public static void checkAndSaveInFood(ItemStack stack, ItemType type) {
        if (stack.getItem().isFood()) {
            foodTypeSet(stack, type, stack.getItem());
        }
    }

    public static void foodTypeSet(ItemStack stack, ItemType type, Item item) {
        var foodComponent = item.getFoodComponent();
        assert foodComponent != null;
        type.asFood = foodType(food -> {
            food.hunger = foodComponent.getHunger();
            food.meat = foodComponent.isMeat();
            food.alwaysEdible = foodComponent.isAlwaysEdible();
            food.snack = foodComponent.isSnack();
            food.saturationModifier = foodComponent.getSaturationModifier();
            type.maxUseTime = item.getMaxUseTime(stack);
            foodComponent.statusEffects.forEach(pair -> {
                if (food.statusEffects == null) food.statusEffects = new ConcurrentHashMap<>();
                food.statusEffects(statusEffectInstanceType(statusEffectInstance -> {
                    statusEffect(statusEffectInstance, pair.getFirst());
                }), pair.getSecond());
            });
        });
    }

    public static void statusEffect(StatusEffectInstanceType type, StatusEffectInstance instance) {
        StatusEffect effect = instance.type;
        type.type = statusEffectType(statusEffectType -> {
            statusEffectType.color = effect.getColor();
            language(statusEffectType, effect.getTranslationKey());

            effect.getFactorCalculationDataSupplier().ifPresent(factorCalculationData ->
                    type.factorCalculationData = factorCalculationDataType(factorCalculationDataType ->
                            factorCalculationData(factorCalculationDataType, factorCalculationData)
                    ));
        });
        type.ambient = instance.ambient;
        type.amplifier = instance.amplifier;
        type.duration = instance.duration;
        type.showParticles = instance.showParticles;
        type.showIcon = instance.showIcon;

        if (instance.hiddenEffect != null) {
            type.hiddenEffect = statusEffectInstanceType(statusEffect -> {
                statusEffect(statusEffect, instance.hiddenEffect);
            });
        }
        instance.getFactorCalculationData().ifPresent(factorCalculationData ->
                type.factorCalculationData = factorCalculationDataType(factorCalculationDataType ->
                        factorCalculationData(factorCalculationDataType, factorCalculationData)
                ));
    }

    public static void factorCalculationData(FactorCalculationDataType type, StatusEffectInstance.FactorCalculationData data) {
        type.paddingDuration = data.paddingDuration;
        type.factorStart = data.factorStart;
        type.factorTarget = data.factorTarget;
        type.factorCurrent = data.factorCurrent;
        type.effectChangedTimestamp = data.effectChangedTimestamp;
        type.factorPreviousFrame = data.factorPreviousFrame;
        type.hadEffectLastTick = data.hadEffectLastTick;
    }

    public static void blockSettings(ItemType type, ItemStack stack, boolean this$advanceParameters) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState defaultState = block.getDefaultState();

            type.asBlock = blockType(blockType -> {
                defaultBlockType(blockType, defaultState, block);
                advanceParameterBlockType(blockType, defaultState, block, this$advanceParameters);
            });
        }

    }

    public static void advanceParameterBlockType(BlockType blockType, BlockState defaultState, Block block, boolean this$advanceParameters) {
        if (this$advanceParameters) {
            blockType.blockBreakParticles = defaultState.hasBlockBreakParticles();
            blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
            blockType.isAir = defaultState.isAir();
            blockType.liquid = defaultState.isLiquid();
            blockType.solid = defaultState.isSolid();
            blockType.randomTicks = block.randomTicks;
        }
    }

    public static void defaultBlockType(BlockType blockType, BlockState defaultState, Block block) {
        blockType.luminance = defaultState.getLuminance();
        blockType.collidable = block.collidable;
        blockType.hardness = block.getHardness();

        blockType.burnable = defaultState.isBurnable();

        blockType.resistance = block.getBlastResistance();
        blockType.toolRequired = defaultState.isToolRequired();
        blockType.opaque = defaultState.isOpaque();
        blockType.replaceable = defaultState.isReplaceable();

        blockType.slipperiness = block.getSlipperiness();//ice
        blockType.velocityMultiplier = block.getVelocityMultiplier();//run speed
        blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();//jump height
        blockType.lootTableId = block.getLootTableId().toString();
    }

    @NotNull
    public static ItemType save(ItemStack stack, BlockAndItemSerializable blockAndItem, boolean this$advanceParameters) {
        return itemType(type -> {
            defaultSetItemSettings(stack, type);
            language(blockAndItem, stack);
                    // LanguageHelper.get
            checkAndSaveInFood(stack, type);
                    // stack.getItem().isFood()
            blockSettings(type, stack, this$advanceParameters);
                    // instanceof BlockItem block instanceof FluidBlock else other
            miningTool(stack, type); // instanceof MiningToolItem miningTool
            armor(stack, type); // instanceof ArmorItem armorItem
            fuelSet(stack, type); // add fuel time if fuel time == 0 item isn't fuel
            setHasNbt(type, stack.getNbt()); // nbtCompound != null

        });
    }

    public static void setHasNbt(ItemType type, NbtCompound nbtCompound) {
        if (nbtCompound != null) {
            type.nbt = nbtType(nbt -> nbtCompound.getKeys().forEach(key -> nbt.entries.put(key, Objects.requireNonNull(nbtCompound.get(key)))));
        }
    }

    public static void fuelSet(ItemStack stack, ItemType type) {
        type.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(stack.getItem(), 0);
    }

    public static void armor(ItemStack stack, ItemType type) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            type.armor = ArmorType.armorType(armor -> {
                armor.enchantability = armorItem.getEnchantability();
                armor.equipmentSlot = armorItem.getType().getEquipmentSlot().getName();
                armor.type = armorItem.getType().getName();
                armor.protection = armorItem.getProtection();
                armor.knockbackResistance = armorItem.getMaterial().getKnockbackResistance();
                armor.toughness = armorItem.getToughness();
                for (ItemStack matchingStack : armorItem.getMaterial().getRepairIngredient().getMatchingStacks()) {
                    armor.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static void miningTool(ItemStack stack, ItemType type) {
        if (stack.getItem() instanceof MiningToolItem miningTool) {

            type.tool = toolType(tool -> {
                tool.tagId = miningTool.effectiveBlocks.id().toString();
                ToolMaterial toolMaterial = ((Supplier<ToolMaterial>) miningTool).get();
                tool.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
                tool.attackDamage = toolMaterial.getAttackDamage();
                tool.miningLevel = toolMaterial.getMiningLevel();
                tool.enchantability = toolMaterial.getEnchantability();
                for (ItemStack matchingStack : toolMaterial.getRepairIngredient().getMatchingStacks()) {
                    tool.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
                }
            });
        }
    }

    public static NbtType nbt(NbtCompound nbtCompound) {
        NbtType nbtType = null;
        if (nbtCompound != null) {
            nbtType = nbtType(nbt -> nbtCompound.getKeys().forEach(key -> nbt.entries.put(key, Objects.requireNonNull(nbtCompound.get(key)))));
        }
        return nbtType;
    }

    public void itemAndBlockExporterStack(ItemStack stack, BlockAndItems blockAndItems) {
        Item item = stack.getItem();
        String registerName = Registries.ITEM.getId(item).toString();
        BlockAndItemSerializable blockAndItemSerializable = blockAndItemSerializable(blockAndItem -> {
            defaultItemProperties(stack, blockAndItem);
        });
        if (item.isFood()) {
            blockAndItems.food(registerName, blockAndItemSerializable);
        } else if (item instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FluidBlock) {
                blockAndItems.fluid(registerName, blockAndItemSerializable);
            } else {
                blockAndItems.block(registerName, blockAndItemSerializable);
            }
        } else if (item instanceof MiningToolItem) {
            blockAndItems.tool(registerName, blockAndItemSerializable);
        } else if (item instanceof ArmorItem) {
            blockAndItems.armor(registerName, blockAndItemSerializable);
        } else {
            blockAndItems.item(registerName, blockAndItemSerializable);
        }
    }




    public void itemAndBlockExporterModid(String modid) {
        BlockAndItems blockAndItems = new BlockAndItems();
        Path itemAndBlocksJson = exporter.resolve(modid).resolve("item-and-block.json");
        Registries.ITEM.getIds().forEach(identifier -> {
            Item item = Registries.ITEM.get(identifier);
            itemAndBlockExporterStack(item.getDefaultStack(), blockAndItems);
        });

        blockAndItems.save(itemAndBlocksJson);
    }

    /**
     * Exporting item and block
     */
    public void itemAndBlockExporterAll() {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        if (this$modid != null) {
            futures.add(CompletableFuture.runAsync(() -> {
                itemAndBlockExporterModid(this$modid.asString());
            }, executorService));
        } else {
            Arrays.stream(ModidType.values()).map(ModidType::name).forEach(modid -> {
                futures.add(CompletableFuture.runAsync(() -> {
                    itemAndBlockExporterModid(modid);
                }, executorService));
            });
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }


}
