package org.universal.exporter;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.fluid.FlowableFluid;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.List;

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
    public static List<Mod> getMods() {
        throw new AssertionError();
    }

//    @ExpectPlatform
//    public static FluidType set(FluidState state) {
//        throw new AssertionError();
//    }
}
