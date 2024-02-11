package org.universal.exporter.forge;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.loading.FMLPaths;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;

import java.nio.file.Path;

public class UniExporterExpectPlatformImpl {
    /**
     * This is our actual method to {@link UniExporterExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        var fluidType = fluid.getFluidType();
        return source
                .supportsBoating(fluidType.supportsBoating(null))
                .canSwim(fluidType.canSwim(null))
                .canDrown(fluidType.canDrownIn(null));
    }

    public static Path getGameFolder() { return FMLPaths.GAMEDIR.get(); }


}
