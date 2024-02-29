package org.universal.exporter.registry.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import org.universal.exporter.registry.CommandListening;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryAllImpl {
    public static final ArrayList<CommandListening> listens = new ArrayList<>();
    public static final Map<Identifier, Supplier<ArgumentSerializer<?, ?>>> arguments = new HashMap<>();

    public static void registryCommand(CommandListening listening) {
        listens.add(listening);
    }



    @SuppressWarnings("RedundantCast")
    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        arguments.put(id, () -> ArgumentTypes.registerByClass(infoClass, ConstantArgumentSerializer.of(typeSupplier)));
    }
}
