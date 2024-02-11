package org.uniexporter.exporter.adapter.serializable.type;

public class BlockType {
    public int luminance;
    public float hardness;
    public float resistance;

    public FluidType asFluid;

    public BlockType luminance(int luminance) {
        this.luminance = luminance;
        return this;
    }

    public BlockType hardness(float hardness) {
        this.hardness = hardness;
        return this;
    }

    public BlockType resistance(float resistance) {
        this.resistance = resistance;
        return this;
    }

    public BlockType asFluid(FluidType asFluid) {
        this.asFluid = asFluid;
        return this;
    }
}
