package org.universal.exporter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;
import org.universal.exporter.platform.Mod;
import org.universal.exporter.registry.CommandListening;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class UniExporterExpectPlatform {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGameFolder() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registryCommand(CommandListening listening) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<Mod> getMods() {
        throw new AssertionError();
    }

    public static List<String> getModids() {
        return getMods().stream().map(mod -> mod.modid).toList();
    }

    @ExpectPlatform
    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        throw new AssertionError();
    }


//    @ExpectPlatform
//    public static FluidType set(FluidState state) {
//        throw new AssertionError();
//    }
}
