package org.universal.exporter.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.FlowableFluid;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;

import java.nio.file.Path;

public class UniExporterExpectPlatformImpl {
    /**
     * This is our actual method to {@link UniExporterExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        return source;
    }


}
