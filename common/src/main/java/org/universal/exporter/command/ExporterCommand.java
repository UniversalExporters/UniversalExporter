package org.universal.exporter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.serializable.AdvancementSerializable;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.*;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.*;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.universal.exporter.UniExporter;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.utils.Base64Helper;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable.blockAndItemSerializable;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FoodType.foodType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType.nbtType;
import static org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType.factorCalculationDataType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType.statusEffectInstanceType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType.statusEffectType;
import static org.universal.exporter.command.argument.ExporterArgumentType.getExporter;
import static org.universal.exporter.command.argument.ModidArgumentType.getModidType;
import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;
/**
 * uex exporter command
 * @author baka4n
 * @author QWERTY770
 */
public class ExporterCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = -3255258476049849876L;

    public static final ExecutorService executorService = Executors.newWorkStealingPool();//线程池子
    //命令头
    public static final LiteralArgumentBuilder<ServerCommandSource> uex = literal("uex");
    //带选择的命令
    public static final  RequiredArgumentBuilder<ServerCommandSource, ExporterType> select = argument("select", ExporterArgumentType.exporter());
    public static final RequiredArgumentBuilder<ServerCommandSource, Boolean> advanceParameters = argument("advance-parameters", BoolArgumentType.bool());
    public static final RequiredArgumentBuilder<ServerCommandSource, ModidType> modid = argument("modid", ModidArgumentType.modids());
    private final CommandContext<ServerCommandSource> context;
    private final ExporterType this$select;
    private final Boolean this$advanceParameters;
    private final ModidType this$modid;

    public ExporterCommand(CommandContext<ServerCommandSource> context, ExporterType select, ModidType modid, boolean advanceParameters) {
        this.context = context;
        this$select = select;
        this$advanceParameters = advanceParameters;
        this$modid = modid;
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
            } else if (this$select.equals(ExporterType.advancements)) {
                advancementsAll();
            }
        }

        return defaultCommandSources();
    }



    public static void advancementModId(CommandContext<ServerCommandSource> context, boolean advancementParameters, String modid) {
        Path advancementsJson = UniExporter.exporter.resolve(modid).resolve("advancements.json");
        List<Advancement> advancements = context.getSource().getServer().getAdvancementLoader().getAdvancements().stream().toList();
        CompletableFuture.runAsync(() -> {
            List<Advancement> parents = advancements.stream().filter(advancement -> advancement.getParent() == null).toList();
            Advancements modidAdvancements = new Advancements();
            for (Advancement parent : parents) {
                AdvancementDisplay display = parent.getDisplay();
                AdvancementSerializable advancement = new AdvancementSerializable();
                if (display != null) {
                    var title = display.getTitle().getContent();
                    var description = display.getDescription().getContent();

                    AdvancementDisplayType displayType = new AdvancementDisplayType()
                            .title(title instanceof TranslatableTextContent translatable ? zh_cn().get(translatable.getKey()) : title.toString())
                            .englishTitle(title instanceof TranslatableTextContent translatable ? en_us().get(translatable.getKey()) : title.toString())
                            .description(description instanceof TranslatableTextContent translatable ? zh_cn().get(translatable.getKey()) : title.toString())
                            .englishDescription(description instanceof TranslatableTextContent translatable ? en_us().get(translatable.getKey()) : title.toString());
                    advancement.display(displayType);
                    try {
                        Pair<String, String> pair = Base64Helper.itemStackToBase64(display.getIcon());
                        displayType.icon(new IconType()
                                .smallIcon(pair.getLeft())
                                .largeIcon(pair.getRight()));
                    } catch (IOException e) {
                        UniExporter.LOGGER.error("don't find {}", display.getIcon().getItem().getName());
                    }
                    Identifier background = display.getBackground();
                    if (background != null) {
                        displayType.background(background.toString(), advancementParameters);
                    }
                    displayType
                            .frame(display.getFrame().getId())
                            .showToast(display.shouldShowToast())
                            .announceToChat(display.shouldAnnounceToChat())
                            .hidden(display.isHidden());
                }
                AdvancementRewards rewards = parent.getRewards();
                CommandFunction.LazyContainer function = rewards.function;

                advancement
                        .rewards(new AdvancementRewardsType()
                                .experience(rewards.experience)
                        );
                advancement.rewards.loots = Arrays.stream(rewards.loot).map(Identifier::toString).collect(Collectors.toCollection(ArrayList::new));
                advancement.rewards.recipes = Arrays.stream(rewards.getRecipes()).map(Identifier::toString).collect(Collectors.toCollection(ArrayList::new));
                Identifier id = function.getId();
                CommandFunctionType commandFunctionType = new CommandFunctionType();
                if (id != null) commandFunctionType.id(id.toString());
                advancement.rewards
                        .function(new LazyContainerType()
                                .function(commandFunctionType));

                if (parent.text.getContent() instanceof TranslatableTextContent translatable){
                    advancement.englishName(en_us().get(translatable.getKey()));
                    if (zh_cn().hasTranslation(translatable.getKey())){
                        advancement.name(zh_cn().get(translatable.getKey()));
                    }
                }
                else {
                    advancement.name(parent.text.getContent().toString());
                    advancement.englishName(parent.text.getContent().toString());
                }
                Map<String, AdvancementCriterion> criteria = parent.getCriteria();
                for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
                    advancement.criteria(entry.getKey(), new AdvancementCriterionType()
                            .registerName(entry.getValue().getConditions().getId().toString()));
                }
                String[][] requirements = parent.getRequirements();

                for (String[] requirement : requirements)
                    advancement.requirements(requirement);
                advancement.sendsTelemetryEvent(parent.sendsTelemetryEvent());

                modidAdvancements.advancement(parent.getId().toString() ,advancement);
            }
            modidAdvancements.save(advancementsJson);
        });
    }

    /**
     * Exporting all advancement
     */
    public void advancementsAll() {
        var modids = UniExporterExpectPlatform.getModids();
        MinecraftServer server = context.getSource().getServer();

        for (int i = 0; i < modids.size(); i++) {
            String modid = modids.get(i);

        }
    }

    @SuppressWarnings("deprecation")
    public BlockAndItemSerializable itemAndBlockExporterStack(ItemStack stack, BlockAndItems blockAndItems) {
        Item item = stack.getItem();
        String registerName = Registries.ITEM.getId(item).toString();
        NbtCompound nbtCompound = stack.getNbt();
        BlockAndItemSerializable blockAndItemSerializable = blockAndItems.find(registerName);
        return Objects.requireNonNullElseGet(blockAndItemSerializable, () -> blockAndItemSerializable(blockAndItem -> {
            blockAndItem.type = ItemType.itemType(type -> {
                type.maxStackSize = item.getMaxCount();
                type.maxDurability = item.getMaxDamage();
                TagKey.codec(RegistryKeys.ITEM).map(itemTagKey -> {
                    if (type.OredictList == null) type.OredictList = new ArrayList<>();
                    type.OredictList.add(itemTagKey.id().toString());
                    return itemTagKey;
                });
                var base64Helper = new Base64Helper(blockAndItem.type);
                base64Helper.itemToBase(item);
                String translationKey = item.getTranslationKey();
                if (en_us().hasTranslation(translationKey))
                    blockAndItem.englishName = en_us().get(translationKey);
                if (zh_cn().hasTranslation(translationKey))
                    blockAndItem.name = zh_cn().get(translationKey);

                if (item.isFood()) {
                    if (blockAndItems.foods == null) blockAndItems.foods = new ConcurrentHashMap<>();
                    blockAndItems.foods.put(registerName, blockAndItem);
                    FoodComponent foodComponent = item.getFoodComponent();
                    assert foodComponent != null;
                    type.asFood = foodType(food -> {
                        food.hunger = foodComponent.getHunger();
                        food.meat = foodComponent.isMeat();
                        food.alwaysEdible = foodComponent.isAlwaysEdible();
                        food.snack = foodComponent.isSnack();
                        food.saturationModifier = foodComponent.getSaturationModifier();
                        type.maxUseTime = item.getMaxUseTime(stack);
                        for (var statusEffect : foodComponent.statusEffects) {
                            StatusEffectInstance first = statusEffect.getFirst();
                            if (food.statusEffects == null) food.statusEffects = new ConcurrentHashMap<>();
                            food.statusEffects.put(statusEffectInstanceType(statusEffectInstance -> {
                                statusEffect(statusEffectInstance, first);
                            }), statusEffect.getSecond());
                        }
                    });
                }
                if (item instanceof BlockItem blockItem) {

                    Block block = blockItem.getBlock();
                    BlockState defaultState = block.getDefaultState();

                    type.asBlock = blockType(blockType -> {
                        blockType.luminance = defaultState.getLuminance();
                        blockType.collidable = block.collidable;
                        blockType.hardness = block.getHardness();
                        blockType.blockBreakParticles = defaultState.hasBlockBreakParticles();
                        blockType.burnable = defaultState.isBurnable();
                        blockType.isAir = defaultState.isAir();
                        blockType.resistance = block.getBlastResistance();
                        blockType.toolRequired = defaultState.isToolRequired();
                        blockType.opaque = defaultState.isOpaque();
                        blockType.replaceable = defaultState.isReplaceable();

                        blockType.slipperiness = block.getSlipperiness();//ice
                        blockType.velocityMultiplier = block.getVelocityMultiplier();//run speed
                        blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();//jump height
                        blockType.lootTableId = block.getLootTableId().toString();
                        if (this$advanceParameters) {
                            blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
                            blockType.liquid = defaultState.isLiquid();
                            blockType.solid = defaultState.isSolid();
                            blockType.ticksRandomly = defaultState.hasRandomTicks();
                            blockType.randomTicks = block.randomTicks;
                        }



                        if (block instanceof FluidBlock fluidBlock) {
                            if (blockAndItems.fluids == null) blockAndItems.fluids = new ConcurrentHashMap<>();
                            blockAndItems.fluids.put(registerName, blockAndItem);
                            blockType.asFluid = UniExporterExpectPlatform.fluidType(new FluidType(), (FlowableFluid) fluidBlock.getFluidState(defaultState).getFluid());
                        } else {
                            if (blockAndItems.blocks == null) blockAndItems.blocks = new ConcurrentHashMap<>();
                            blockAndItems.blocks.put(registerName, blockAndItem);
                        }
                    });
                }
                if (nbtCompound != null) {
                    type.nbt = nbtType(nbt -> {
                        for (String key : nbtCompound.getKeys()) {
                            nbt.entries.put(key, Objects.requireNonNull(nbtCompound.get(key)));
                        }
                    });
                }
            });
        }));
    }

    public void statusEffect(StatusEffectInstanceType type, StatusEffectInstance instance) {
        StatusEffect effect = instance.type;
        type.type = statusEffectType(statusEffectType -> {
            statusEffectType.color = effect.getColor();
            var translationKey = effect.getTranslationKey();
            if (en_us().hasTranslation(translationKey))
                statusEffectType.englishName = en_us().get(translationKey);
            if (zh_cn().hasTranslation(translationKey))
                statusEffectType.name = zh_cn().get(translationKey);
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

    public void factorCalculationData(FactorCalculationDataType type, StatusEffectInstance.FactorCalculationData data) {
        type.paddingDuration = data.paddingDuration;
        type.factorStart = data.factorStart;
        type.factorTarget = data.factorTarget;
        type.factorCurrent = data.factorCurrent;
        type.effectChangedTimestamp = data.effectChangedTimestamp;
        type.factorPreviousFrame = data.factorPreviousFrame;
        type.hadEffectLastTick = data.hadEffectLastTick;
    }

    public void itemAndBlockExporterModid(String modid) {
        BlockAndItems blockAndItems = new BlockAndItems();
        Path itemAndBlocksJson = UniExporter.exporter.resolve(modid).resolve("item-and-block.json");
        CompletableFuture.runAsync(() -> {
            final DefaultedRegistry<Item> registry = Registries.ITEM;
            List<Identifier> modItemIds = registry.getIds().stream().filter(identifier -> identifier.getNamespace().equals(modid)).toList();
            for (Identifier modItemId : modItemIds) {
                Item item = registry.get(modItemId);
                BlockAndItemSerializable serializable = itemAndBlockExporterStack(item.getDefaultStack(), blockAndItems);
            }

//                AtomicBoolean b = new AtomicBoolean(true);
//                itemAndBlockHelper
//                        .init(item)
//                        .setup(t -> {
//                            if (item instanceof BlockItem blockItem) {
//                                Block block = blockItem.getBlock();
//
//                                BlockType blockType = new BlockType()
//                                        .hardness(block.getHardness())
//                                        .luminance(block.getDefaultState().getLuminance())
//                                        .resistance(block.getBlastResistance());
//                                if (block instanceof FluidBlock fluid) {
//                                    FluidState liquid = fluid.getFluidState(fluid.getDefaultState());
//                                    blockType.asFluid(UniExporterExpectPlatform
//                                            .fluidType(new FluidType()
//                                                    .source(liquid.isStill()), (FlowableFluid) liquid.getFluid())
//                                            .asBucket(registry.getId(liquid.getFluid().getBucketItem()).toString()));
//                                }
//                                type.asBlock(blockType);
//                                blockAndItems.block(registryId.toString(), serializable.type(type.type("block-item")));
//                                b.set(false);
//                            }
//                            return itemAndBlockHelper;
//                        })
//                        .setup(t -> {
//                            if (item instanceof ArmorItem tool) {
//                                blockAndItems.tool(registryId.toString(), serializable.type(t.type("tools")));
//                                b.set(false);
//                            }
//                            return itemAndBlockHelper;
//                        })
//                        .setup(t -> {
//                            if (item instanceof BucketItem bucket) {
//
//                                blockAndItems.item(registryId.toString(), serializable.type(t.asFluid(Registries.FLUID.getId(bucket.fluid).toString()).type("item")));
//                                b.set(false);
//                            }
//                            return itemAndBlockHelper;
//                        })
//                        .setup(t -> {
//                            if (item instanceof ArmorItem armor) {
//                                blockAndItems.armor(registryId.toString(), serializable.type(t.type("buck-item")));
//                                b.set(false);
//                            }
//
//                            return itemAndBlockHelper;
//                        })
//                        .setup(t -> {
//                            if (b.get()) {
//                                blockAndItems.item(registryId.toString(), serializable.type(t.type("item")));
//                            }
//                            return itemAndBlockHelper;
//                        });
//
//            }
            blockAndItems.save(itemAndBlocksJson);
        }, executorService);
    }

    /**
     * Exporting item and block
     */
    public void itemAndBlockExporterAll() {
        if (this$modid != null) {
            itemAndBlockExporterModid(this$modid.name());
        } else {
            Arrays.stream(ModidType.values()).map(ModidType::name).forEach(this::itemAndBlockExporterModid);
        }
    }


}
