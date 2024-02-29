package org.universal.exporter.serializable.forge;

import net.minecraft.fluid.FlowableFluid;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;

public class UtilImpl {
    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        var fluidType = fluid.getFluidType();
        return source
                .supportsBoating(fluidType.supportsBoating(null))
                .canSwim(fluidType.canSwim(null))
                .canDrown(fluidType.canDrownIn(null));
    }
}
