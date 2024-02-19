package org.universal.exporter.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import org.universal.exporter.command.type.ModidType;

import java.util.Arrays;
import java.util.Locale;

public class ModidArgumentType extends EnumArgumentType<ModidType> {

    private static final Codec<ModidType> MODID_TYPE_CODEC = StringIdentifiable.createCodec(ModidArgumentType::getModidTypes, (name) -> name.toLowerCase(Locale.ROOT));

    private static ModidType[] getModidTypes() {
        return Arrays.stream(ModidType.values()).toArray(ModidType[]::new);
    }

    protected ModidArgumentType() {
        super(MODID_TYPE_CODEC, ModidArgumentType::getModidTypes);
    }

    public static ModidType getModidType(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, ModidType.class);
    }

    public static ModidArgumentType modids() {
        return new ModidArgumentType();
    }
}
