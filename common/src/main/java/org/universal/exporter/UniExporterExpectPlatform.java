package org.universal.exporter;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

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
    public static List<Mod> getMods() {throw new AssertionError(); }



//    @ExpectPlatform
//    public static FluidType set(FluidState state) {
//        throw new AssertionError();
//    }
}
