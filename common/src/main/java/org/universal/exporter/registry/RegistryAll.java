package org.universal.exporter.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class RegistryAll {
    @ExpectPlatform
    public static void registryCommand(CommandListening listening) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        throw new AssertionError();
    }
}
