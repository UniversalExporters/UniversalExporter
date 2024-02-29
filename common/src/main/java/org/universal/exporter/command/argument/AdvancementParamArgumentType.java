package org.universal.exporter.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import org.universal.exporter.command.type.AdvancementParamType;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

public class AdvancementParamArgumentType extends EnumArgumentType<AdvancementParamType> {

    private static final Codec<AdvancementParamType>  ADVANCEMENT_PARAM_TYPE_CODEC = StringIdentifiable.createCodec(AdvancementParamArgumentType::getAdvancementParams, (name) -> name.toLowerCase(Locale.ROOT));

    private static AdvancementParamType[] getModidTypes() {
        return Arrays.stream(AdvancementParamType.values()).toArray(AdvancementParamType[]::new);
    }

    public static AdvancementParamType[] getAdvancementParams() {
        return AdvancementParamType.values();
    }

    protected AdvancementParamArgumentType() {
        super(ADVANCEMENT_PARAM_TYPE_CODEC, AdvancementParamArgumentType::getModidTypes);
    }

    public static AdvancementParamType getAdvancementParamType(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, AdvancementParamType.class);
    }

    public static AdvancementParamArgumentType advancementParams() {
        return new AdvancementParamArgumentType();
    }
}
