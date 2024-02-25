package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class BlockType implements Self<BlockType> {
    @SerializedName("luminance")
    public int luminance;//block state
    @SerializedName("hasSidedTransparency")
    public boolean hasSidedTransparency;//block state
    @SerializedName("isAir")
    public Boolean isAir;//block state
    @SerializedName("burnable")
    public Boolean burnable;//block state
    @SerializedName("liquid")
    @Deprecated
    public Boolean liquid;//block state
    @SerializedName("solid")
    @Deprecated
    public Boolean solid;//block state

    @SerializedName("hardness")
    public float hardness;//block state

    @SerializedName("toolRequired")
    public boolean toolRequired;//block state
    @SerializedName("opaque")
    public Boolean opaque;//block state



//    public final ContextPredicate solidBlockPredicate; //block state
//    public final ContextPredicate suffocationPredicate; //block state
//    public final ContextPredicate blockVisionPredicate; //block state
//    public final ContextPredicate postProcessPredicate; //block state
//    public final ContextPredicate emissiveLightingPredicate; //block state
//    public final Optional<Offsetter> offsetter; //block state
//    public final Instrument instrument; //block state
    @SerializedName("blockBreakParticles")
    public Boolean blockBreakParticles;//block state
    @SerializedName("replaceable")
    public Boolean replaceable;//block state

//    protected ShapeCache shapeCache; //block state
//    public FluidState fluidState; //block state

    @SerializedName("resistance")
    public float resistance; // block settings
    @SerializedName("collidable")
    public Boolean collidable;  // block settings

//    BlockSoundGroup soundGroup;// block settings

    @SerializedName("randomTicks")
    public boolean randomTicks;// block settings
    @SerializedName("slipperiness")
    public Float slipperiness;// block settings

    @SerializedName("velocityMultiplier")
    public Float velocityMultiplier;// block settings
    @SerializedName("jumpVelocityMultiplier")
    public Float jumpVelocityMultiplier;// block settings

    @SerializedName("lootTableId")
    public String lootTableId;// block settings

    @SerializedName("asFluid")
    public FluidType asFluid;

    @SerializedName("full")
    public boolean isFull;

    public static BlockType blockType(Consumer<BlockType> consumer) {
        BlockType blockType = new BlockType();
        consumer.accept(blockType);
        return blockType;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockType blockType = (BlockType) o;
        return luminance == blockType.luminance && hasSidedTransparency == blockType.hasSidedTransparency && Float.compare(hardness, blockType.hardness) == 0 && toolRequired == blockType.toolRequired && Float.compare(resistance, blockType.resistance) == 0 && randomTicks == blockType.randomTicks && Objects.equals(isAir, blockType.isAir) && Objects.equals(burnable, blockType.burnable) && Objects.equals(liquid, blockType.liquid) && Objects.equals(solid, blockType.solid) && Objects.equals(opaque, blockType.opaque) && Objects.equals(blockBreakParticles, blockType.blockBreakParticles) && Objects.equals(replaceable, blockType.replaceable) && Objects.equals(collidable, blockType.collidable) && Objects.equals(slipperiness, blockType.slipperiness) && Objects.equals(velocityMultiplier, blockType.velocityMultiplier) && Objects.equals(jumpVelocityMultiplier, blockType.jumpVelocityMultiplier) && Objects.equals(lootTableId, blockType.lootTableId) && Objects.equals(asFluid, blockType.asFluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(luminance, hasSidedTransparency, isAir, burnable, liquid, solid, hardness, toolRequired, opaque, blockBreakParticles, replaceable, resistance, collidable, randomTicks, slipperiness, velocityMultiplier, jumpVelocityMultiplier, lootTableId, asFluid);
    }
}
