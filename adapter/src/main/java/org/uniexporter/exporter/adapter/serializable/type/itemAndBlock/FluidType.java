package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.faces.Self;

public class FluidType implements Self<FluidType> {
    @SerializedName("canSwim")
    public boolean canSwim;
    @SerializedName("canDrown")
    public boolean canDrown;
     @SerializedName("supportsBoating")
    public boolean supportsBoating;
     @SerializedName("asBucket")
    public String asBucket;

     @SerializedName("isSource")
    public boolean isSource;


    public FluidType canSwim(boolean canSwim) {
        this.canSwim = canSwim;
        return self();
    }

    public FluidType canDrown(boolean canDrown) {
        this.canDrown = canDrown;
        return self();
    }

    public FluidType supportsBoating(boolean supportsBoating) {
        this.supportsBoating = supportsBoating;
        return self();
    }

    public FluidType asBucket(String asBucket) {
        this.asBucket = asBucket;
        return self();
    }

    public FluidType source(boolean source) {
        isSource = source;
        return self();
    }
}
