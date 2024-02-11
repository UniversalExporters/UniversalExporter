package org.universal.exporter.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.*;
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
import org.uniexporter.exporter.adapter.serializable.type.ItemType;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.utils.Base64Helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Language.load;

public class ExporterCommand {

    private static Language en_us, zh_cn;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(literal("ue")
                .then(argument("select", ExporterArgumentType.exporter())
                        .executes(ExporterCommand::select)));
    }

    private static int select(CommandContext<ServerCommandSource> context) {
        ExporterType select = ExporterArgumentType.getExporter(context, "select");
        if (select.equals(ExporterType.item)) {
            itemAndBlockExporter();
        }
        return 1;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void itemAndBlockExporter() {

        BlockAndItems blockAndItems = new BlockAndItems();

        Set<Identifier> ids = Registries.ITEM.getIds();

        var mods = Platform.getMods().stream().toList();

        ExecutorService executorService = Executors.newFixedThreadPool(mods.size());
        CompletableFuture<?>[] futures = new CompletableFuture[mods.size()];
        for (int i = 0; i < mods.size(); i++) {
            final Mod mod = mods.get(i);
            futures[i] = CompletableFuture.runAsync(() -> {
                BlockAndItemSerializable serializable = new BlockAndItemSerializable();String modid = mod.getModId();
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
                    new Base64Helper(type).itemToBase(item);
                    serializable
                            .englishName(en_us().get(item.getTranslationKey()))
                            .name(zh_cn().get(item.getTranslationKey()));
                    if (item instanceof BlockItem blockItem) {

                    } else if (item instanceof MiningToolItem tool) {

                    } else if (item instanceof BucketItem fluid) {

                    } else if (item instanceof ArmorItem armor) {

                    } else {
                        serializable
                                .type(type.type("item"));
                    }
                    blockAndItems.items.put(registryId.toString(), serializable);
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


    public static Language en_us() {
        if (en_us == null) en_us = create("en_us");
        return en_us;
    }

    public static Language zh_cn() {
        if (zh_cn == null) zh_cn = create("zh_cn");
        return zh_cn;
    }

    public static Language create(String language) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        load(biConsumer, "/assets/minecraft/lang/" + language + ".json");
        final Map<String, String> map = builder.build();
        return new Language() {
            public String get(String key, String fallback) {
                return (String)map.getOrDefault(key, fallback);
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
