package org.uniexporter.exporter.adapter.serializable.type;

public class FluidType {
    public boolean canSwim;
    public boolean canDrown;
    public boolean supportsBoating;
    public String asBucket;

    public boolean isSource;

    public FluidType canSwim(boolean canSwim) {
        this.canSwim = canSwim;
        return this;
    }

    public FluidType canDrown(boolean canDrown) {
        this.canDrown = canDrown;
        return this;
    }

    public FluidType supportsBoating(boolean supportsBoating) {
        this.supportsBoating = supportsBoating;
        return this;
    }

    public FluidType asBucket(String asBucket) {
        this.asBucket = asBucket;
        return this;
    }

    public FluidType source(boolean source) {
        isSource = source;
        return this;
    }
}
