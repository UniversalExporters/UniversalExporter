package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import org.uniexporter.exporter.adapter.faces.Self;

public class BlockType implements Self<BlockType> {
    public int luminance;
    public float hardness;
    public float resistance;

    public FluidType asFluid;

    public BlockType luminance(int luminance) {
        this.luminance = luminance;
        return self();
    }

    public BlockType hardness(float hardness) {
        this.hardness = hardness;
        return self();
    }

    public BlockType resistance(float resistance) {
        this.resistance = resistance;
        return self();
    }

    public BlockType asFluid(FluidType asFluid) {
        this.asFluid = asFluid;
        return self();
    }
}
