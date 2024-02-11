package org.universal.exporter.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.*;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.BlockType;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.uniexporter.exporter.adapter.serializable.type.ItemType;
import org.universal.exporter.UniExporter;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.utils.Base64Helper;
import org.universal.exporter.utils.ItemAndBlockHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Language.load;

public class ExporterCommand {



    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(literal("ue")
                .then(argument("select", ExporterArgumentType.exporter())
                        .executes(ExporterCommand::select)));
    }

    private static int select(CommandContext<ServerCommandSource> context) {
        ExporterType select = ExporterArgumentType.getExporter(context, "select");
        if (select.equals(ExporterType.item)) {
            itemAndBlockExporter(context);
        }
        return 1;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void itemAndBlockExporter(CommandContext<ServerCommandSource> context) {

        BlockAndItems blockAndItems = new BlockAndItems();


        var mods = Platform.getMods().stream().toList();

        ExecutorService executorService = Executors.newFixedThreadPool(mods.size());
        CompletableFuture<?>[] futures = new CompletableFuture[mods.size()];
        for (int i = 0; i < mods.size(); i++) {
            final Mod mod = mods.get(i);
            futures[i] = CompletableFuture.runAsync(() -> {
                BlockAndItemSerializable serializable = new BlockAndItemSerializable();
                ItemAndBlockHelper itemAndBlockHelper = new ItemAndBlockHelper(serializable);

                String modid = mod.getModId();
                Path itemAndBlocksJson = UniExporter.exporter.resolve(modid).resolve("item-and-block.json");
                var registry = Registries.ITEM;
                Path parent = itemAndBlocksJson.getParent();
                if (!Files.exists(parent)) parent.toFile().mkdirs();
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
                                    Fluid fluid = bucket.arch$getFluid();
                                    blockAndItems.item(registryId.toString(), serializable.type(t.asFluid(Registries.FLUID.getId(fluid).toString()).type("item")));
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
            }, executorService);
        }
        wait(futures);
    }

    public static void wait(CompletableFuture<?>[] futures) {
        boolean done = true;
        for (CompletableFuture<?> future : futures) {
            if (!future.isDone()) {
                done = false;
                break;
            }
        }
        if (!done) wait(futures);
    }

    public static Language create(String language) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        load(biConsumer, "/assets/minecraft/lang/" + language + ".json");
        final Map<String, String> map = builder.build();
        return new Language() {
            public String get(String key, String fallback) {
                return map.getOrDefault(key, fallback);
            }

            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }

            public boolean isRightToLeft() {
                return false;
            }

            public OrderedText reorder(StringVisitable text) {
                return (visitor) -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
            }
        };
    }


}
