package org.universal.exporter.registry.fabric;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import org.universal.exporter.registry.CommandListening;

import java.util.function.Supplier;

public class RegistryAllImpl {
    public static void registryCommand(CommandListening listening) {
        CommandRegistrationCallback.EVENT.register(listening::register);
    }

    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        ArgumentTypeRegistry.registerArgumentType(id, infoClass, ConstantArgumentSerializer.of(typeSupplier));
    }
}
