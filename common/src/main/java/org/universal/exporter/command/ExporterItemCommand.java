package org.universal.exporter.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.type.ExporterType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Language.load;

public class ExporterItemCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(literal("ue")
                .then(argument("select", ExporterArgumentType.exporter())
                        .executes(ExporterItemCommand::select)));
    }

    private static int select(CommandContext<ServerCommandSource> context) {
        ExporterType select = ExporterArgumentType.getExporter(context, "select");
        if (select.equals(ExporterType.item)) {
            itemExporter();
        }
        return 1;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void itemExporter() {
        Set<Identifier> ids = Registries.ITEM.getIds();
        Path items = UniExporter.exporter.resolve("items");
        for (Identifier id : ids) {
            Path resolve = items.resolve(id.getNamespace()).resolve(id.getPath() + ".json");
            Path parent = resolve.getParent();
            if (!Files.exists(parent)) parent.toFile().mkdirs();
            try(BufferedWriter bw = Files.newBufferedWriter(resolve)) {
                Item item = Registries.ITEM.get(id);

                new BlockAndItemSerializable()
                        .englishName(en_us().get(item.getTranslationKey()))
                        .englishName(zh_cn().get(item.getTranslationKey()));
            } catch (IOException ignored) {
                UniExporter.LOGGER.info("export error: {}", resolve);
            }

        }
    }

    public static Language en_us() {
        return create("en_us");
    }

    public static Language zh_cn() {
        return create("zh_cn");
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
