package org.universal.exporter.serializable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.fluid.FlowableFluid;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;

public class Util {
    @ExpectPlatform
    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        throw new AssertionError();
    }
}
