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
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.serializable.AdvancementSerializable;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementCriterionType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementRewardsType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.CommandFunctionType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.LazyContainerType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.utils.CommandHelper;
import org.universal.exporter.utils.ItemAndBlockHelper;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable.blockAndItemSerializable;
import static org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementDisplayType.advancementDisplayType;
import static org.universal.exporter.command.argument.ExporterArgumentType.getExporter;
import static org.universal.exporter.command.argument.ModidArgumentType.getModidType;
import static org.universal.exporter.utils.Base64Helper.icon;
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

    private ExporterCommand(CommandContext<ServerCommandSource> context, ExporterType select, ModidType modid, boolean advanceParameters) {
        super(modid, context);
        initType();
        this$select = select;
        this$advanceParameters = advanceParameters;
    }

    public void initType() {
        ExporterType.itemandblock.setRunnable(this::itemAndBlockExporterAll);
        ExporterType.advancements.setRunnable(this::advancementsAll);
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
            CompletableFuture.runAsync(this$select.runnable, executorService);
        }

        return defaultCommandSources();
    }


    @SuppressWarnings("unchecked")
    public void advancementModId(String modid) {
        Path advancementsJson = UniExporter.exporter.resolve(modid).resolve("advancements.json");
        List<Advancement> advancements = context.getSource().getServer().getAdvancementLoader().getAdvancements().stream().toList();

        CompletableFuture.runAsync(() -> {
            List<Advancement> parents = advancements.stream().filter(advancement -> advancement.getParent() == null).toList();
            Advancements modidAdvancements = new Advancements();
            for (Advancement parent : parents) {
                String registerName = parent.getId().toString();
                AdvancementSerializable advancement = new AdvancementSerializable();
                var display = parent.getDisplay();

                if (display != null) advancement.display = advancementDisplayType(advancementDisplay -> {
                    advancementDisplay.title = zh_cn().get(display.getTitle().getContent());
                    advancementDisplay.englishTitle = en_us().get(display.getTitle().getContent());
                    advancementDisplay.description = zh_cn().get(display.getDescription().getContent());
                    advancementDisplay.englishDescription = en_us().get(display.getDescription().getContent());
                    advancementDisplay.icon(icon().itemStackToBase(display.getIcon()));
                    if (display.getBackground() != null) {
                        advancementDisplay.background(display.getBackground().toString(), this$advanceParameters);
                    }
                    advancementDisplay.frame = display.getFrame().getId();
                    advancementDisplay.showToast = display.shouldShowToast();
                    advancementDisplay.announceToChat = display.shouldAnnounceToChat();
                    advancementDisplay.hidden = display.isHidden();
                });
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
                    CriterionConditions conditions = entry.getValue().getConditions();
                    if (conditions != null)
                        advancement
                            .criteria(entry.getKey(), new AdvancementCriterionType()
                            .registerName(conditions.getId().toString()));
                }
                String[][] requirements = parent.getRequirements();

                for (String[] requirement : requirements)
                    advancement.requirements(requirement);
                advancement.sendsTelemetryEvent(parent.sendsTelemetryEvent());


                modidAdvancements.advancement(registerName, advancement);
            }
            modidAdvancements.save(advancementsJson);
        });
    }

    /**
     * Exporting all advancement
     */
    public void advancementsAll() {
        generationAll(this::advancementModId);
    }

    public BlockAndItemSerializable itemAndBlockExporterStack(ItemStack stack, BlockAndItems blockAndItems) {
        String registerName = Registries.ITEM.getId(stack.getItem()).toString();
        ItemAndBlockHelper helper = new ItemAndBlockHelper(registerName, this$advanceParameters);
        return Objects.requireNonNullElseGet(blockAndItems.find(registerName), () -> blockAndItemSerializable(blockAndItem -> {
            blockAndItem.type = helper.save(stack, blockAndItems, blockAndItem);
        }));
    }

    @NotNull
    private static ItemType save(ItemStack stack, BlockAndItems blockAndItems, BlockAndItemSerializable blockAndItem, ItemAndBlockHelper helper, NbtCompound nbtCompound) {
        return ItemType.itemType(type -> {
            helper
                    .defaultSetItemSettings(stack, type)
                    .language(blockAndItem, stack) // LanguageHelper.get
                    .checkAndSaveInFood(stack, blockAndItems, blockAndItem, type) // stack.getItem().isFood()
                    .blockSettings(blockAndItems, blockAndItem, type, stack) // instanceof BlockItem block instanceof FluidBlock else other
                    .setHasNbt(type, nbtCompound); // nbtCompound != null

        });
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
        generationAll(this::itemAndBlockExporterModid);
    }


}
