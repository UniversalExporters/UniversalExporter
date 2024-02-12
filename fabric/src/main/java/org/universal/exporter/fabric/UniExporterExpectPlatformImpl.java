package org.universal.exporter.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.fluid.FlowableFluid;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    public static Path getGameFolder() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<Mod> getMods() {
        List<Mod> mods = new ArrayList<>();
        for (ModContainer allMod : FabricLoader.getInstance().getAllMods()) {
           mods.add(new Mod(allMod.getMetadata().getId()));
        }
        return mods;
    }
}
