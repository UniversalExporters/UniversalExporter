package org.universal.exporter.command;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
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
import org.uniexporter.exporter.adapter.serializable.type.BlockType;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.*;
import org.universal.exporter.UniExporter;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.utils.Base64Helper;
import org.universal.exporter.utils.ItemAndBlockHelper;

import java.io.IOException;
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
public class ExporterCommand {

    public static final ExecutorService executorService = Executors.newWorkStealingPool();//线程池子

    /**
     * Register Server Command
     * @param dispatcher adapter
     * @param registryAccess access
     * @param env environment
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(literal("uex")
                .then(argument("select", ExporterArgumentType.exporter())
                        .executes(ExporterCommand::select)));
    }

    /**
     * select command argument
     * @param context execute context
     * @return success
     */
    private static int select(CommandContext<ServerCommandSource> context) {
        ExporterType select = ExporterArgumentType.getExporter(context, "select");
        if (select.equals(ExporterType.itemandblock)) {
            itemAndBlockExporterAll(context);
        } else if (select.equals(ExporterType.advancements)) {
            advancementsAll(context);
        }
        return 1;
    }

    /**
     * Exporting all advancement
     * @param context execute context
     */
    public static void advancementsAll(CommandContext<ServerCommandSource> context) {
        var modids = UniExporterExpectPlatform.getModids();
        MinecraftServer server = context.getSource().getServer();

        List<Advancement> advancements = server.getAdvancementLoader().getAdvancements().stream().toList();

        for (int i = 0; i < modids.size(); i++) {
            String modid = modids.get(i);
            Path advancementsJson = UniExporter.exporter.resolve(modid).resolve("advancements.json");

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
                        displayType
                                .background(display.getBackground().toString())
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
    }

    /**
     * Exporting item and block
     * @param context execute context
     */
    public static void itemAndBlockExporterAll(CommandContext<ServerCommandSource> context) {

        BlockAndItems blockAndItems = new BlockAndItems();

        var modids = UniExporterExpectPlatform.getModids();


        CompletableFuture<?>[] futures = new CompletableFuture[modids.size()];

        for (int i = 0; i < modids.size(); i++) {
            final String modid = modids.get(i);
            futures[i] = CompletableFuture.runAsync(() -> {
                BlockAndItemSerializable serializable = new BlockAndItemSerializable();
                ItemAndBlockHelper itemAndBlockHelper = new ItemAndBlockHelper(serializable);

                Path itemAndBlocksJson = UniExporter.exporter.resolve(modid).resolve("item-and-block.json");
                var registry = Registries.ITEM;
                List<Identifier> registryIds = registry.getIds().stream().filter(identifier -> identifier.getNamespace().equals(modid)).toList();
                for (Identifier registryId : registryIds) {
                    Item item = registry.get(registryId);
                    ItemType type = new ItemType()
                            .maxStackSize(item.getMaxCount())
                            .maxDurability(item.getMaxDamage());
                    TagKey.codec(RegistryKeys.ITEM).map(itemTagKey -> {
                        if (item.getDefaultStack().itemMatches(itemRegistryEntry -> itemRegistryEntry.isIn(itemTagKey))) {
                            type.OredictList(itemTagKey.id().toString());
                        }
                        return itemTagKey;
                    });
                    AtomicBoolean b = new AtomicBoolean(true);
                    itemAndBlockHelper
                            .init(item)
                            .setup(t -> {
                                if (item instanceof BlockItem blockItem) {
                                    Block block = blockItem.getBlock();

                                    BlockType blockType = new BlockType()
                                            .hardness(block.getHardness())
                                            .luminance(block.getDefaultState().getLuminance())
                                            .resistance(block.getBlastResistance());
                                    if (block instanceof FluidBlock fluid) {
                                        FluidState liquid = fluid.getFluidState(fluid.getDefaultState());
                                        blockType.asFluid(UniExporterExpectPlatform
                                                .fluidType(new FluidType()
                                                        .source(liquid.isStill()), (FlowableFluid) liquid.getFluid())
                                                .asBucket(registry.getId(liquid.getFluid().getBucketItem()).toString()));
                                    }
                                    type.asBlock(blockType);
                                    blockAndItems.block(registryId.toString(), serializable.type(type.type("block-item")));
                                    b.set(false);
                                }
                                return itemAndBlockHelper;
                            })
                            .setup(t -> {
                                if (item instanceof ArmorItem tool) {
                                    blockAndItems.tool(registryId.toString(), serializable.type(t.type("tools")));
                                    b.set(false);
                                }
                                return itemAndBlockHelper;
                            })
                            .setup(t -> {
                                if (item instanceof BucketItem bucket) {

                                    blockAndItems.item(registryId.toString(), serializable.type(t.asFluid(Registries.FLUID.getId(bucket.fluid).toString()).type("item")));
                                    b.set(false);
                                }
                                return itemAndBlockHelper;
                            })
                            .setup(t -> {
                                if (item instanceof ArmorItem armor) {
                                    blockAndItems.armor(registryId.toString(), serializable.type(t.type("buck-item")));
                                    b.set(false);
                                }

                                return itemAndBlockHelper;
                            })
                            .setup(t -> {
                                if (b.get()) {
                                    blockAndItems.item(registryId.toString(), serializable.type(t.type("item")));
                                }
                                return itemAndBlockHelper;
                            });

                }
                blockAndItems.save(itemAndBlocksJson);
            }, executorService);
        }
    }


}
