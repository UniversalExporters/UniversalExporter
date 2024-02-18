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
import net.minecraft.block.FluidBlock;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.*;
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
import org.uniexporter.exporter.adapter.serializable.AdvancementSerializable;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.*;
import org.universal.exporter.UniExporter;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.utils.Base64Helper;
import org.universal.exporter.utils.ItemAndBlockHelper;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
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
    public static final LiteralArgumentBuilder<ServerCommandSource> advanceParameters = literal("-advance--parameters");
    private final CommandContext<ServerCommandSource> context;
    public ExporterCommand(CommandContext<ServerCommandSource> context) {
        this.context = context;

    }

    public static ExporterCommand of(CommandContext<ServerCommandSource> context) {
        return new ExporterCommand(context);
    }

    /**
     * Register Server Command
     * @param dispatcher adapter
     * @param registryAccess access
     * @param env environment
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {

        dispatcher.register(uex
                .then(select
                        .executes(context -> of(context).select())
                        .then(advanceParameters.then(
                                argument("ap", BoolArgumentType.bool())
                                        .executes(context -> of(context).select())
                        ))));

    }

    public static int defaultCommandSources(CommandContext<ServerCommandSource> context, boolean advancementParameters) {
        return 1;
    }

    public static <T extends ArgumentBuilder<ServerCommandSource, T>, R extends ArgumentBuilder<ServerCommandSource, T>> T executes(R r, Command<ServerCommandSource> sourceCommand) {
        return r.executes(sourceCommand);
    }

    /**
     * select command argument
     * @return success
     */
    private int select() {
        ExporterType select = ExporterArgumentType.getExporter(context, "select");
        boolean advancementParameters = false;
        try {
            advancementParameters = BoolArgumentType.getBool(context, "ap");
        } catch (Exception ignored) {}
        if (select.equals(ExporterType.itemandblock)) {
            itemAndBlockExporterAll();
        } else if (select.equals(ExporterType.advancements)) {
            advancementsAll(context, advancementParameters);
        }
        return defaultCommandSources(context, advancementParameters);
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
     * @param context execute context
     */
    public static void advancementsAll(CommandContext<ServerCommandSource> context, boolean advancementParameters) {
        var modids = UniExporterExpectPlatform.getModids();
        MinecraftServer server = context.getSource().getServer();



        for (int i = 0; i < modids.size(); i++) {
            String modid = modids.get(i);

        }
    }

    public void itemAndBlockExporterModid(String modid) {
        BlockAndItems blockAndItems = new BlockAndItems();
        Path itemAndBlocksJson = UniExporter.exporter.resolve(modid).resolve("item-and-block.json");
        CompletableFuture.runAsync(() -> {
            final DefaultedRegistry<Item> registry = Registries.ITEM;
            List<Identifier> modItemIds = registry.getIds().stream().filter(identifier -> identifier.getNamespace().equals(modid)).toList();
            for (Identifier modItemId : modItemIds) {
                Item item = registry.get(modItemId);
                BlockAndItemSerializable serializable = BlockAndItemSerializable.of(blockAndItem -> {
                    blockAndItem.type = ItemType.of(type -> {
                        type.maxStackSize = item.getMaxCount();
                        type.maxDurability = item.getMaxDamage();
                        TagKey.codec(RegistryKeys.ITEM).map(itemTagKey -> {
                            if (type.OredictList == null) type.OredictList = new ArrayList<>();
                            type.OredictList.add(itemTagKey.id().toString());
                            return itemTagKey;
                        });
                        var base64Helper= new Base64Helper(blockAndItem.type);
                        base64Helper.itemToBase(item);
                        String translationKey = item.getTranslationKey();
                        if (en_us().hasTranslation(translationKey))
                            blockAndItem.englishName = en_us().get(translationKey);

                        if (zh_cn().hasTranslation(translationKey))
                            blockAndItem.name = zh_cn().get(item.getTranslationKey());
                        if (item.isFood()) {
                            FoodComponent foodComponent = item.getFoodComponent();
                        }
                    });
                });

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
     *
     */
    public void itemAndBlockExporterAll() {
        var modids = UniExporterExpectPlatform.getModids();
        for (final String modid : modids) {
            itemAndBlockExporterModid(modid);
        }
    }


}
