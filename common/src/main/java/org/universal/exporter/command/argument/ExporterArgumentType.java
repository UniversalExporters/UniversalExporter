package org.universal.exporter.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import org.universal.exporter.command.type.ExporterType;

import java.util.Arrays;
import java.util.Locale;

public class ExporterArgumentType extends EnumArgumentType<ExporterType> {

    private static final Codec<ExporterType> EXPORTER_CODEC = StringIdentifiable.createCodec(ExporterArgumentType::getExporterTypes, (name) -> name.toLowerCase(Locale.ROOT));

    private static ExporterType[] getExporterTypes() {
        return Arrays.stream(ExporterType.values()).toArray(ExporterType[]::new);
    }

    private ExporterArgumentType() {
        super(EXPORTER_CODEC, ExporterArgumentType::getExporterTypes);
    }

    public static ExporterArgumentType exporter() {
        return new ExporterArgumentType();
    }
    public static ExporterType getExporter(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, ExporterType.class);
    }
}
