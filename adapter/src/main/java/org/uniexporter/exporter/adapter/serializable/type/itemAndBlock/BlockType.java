package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.Optional;
import java.util.function.Consumer;

public class BlockType implements Self<BlockType> {
    @SerializedName("luminance")
    public int luminance;//block state
    @SerializedName("hasSidedTransparency")
    public boolean hasSidedTransparency;//block state
    @SerializedName("isAir")
    public boolean isAir;//block state
    @SerializedName("burnable")
    public boolean burnable;//block state
    @SerializedName("liquid")
    @Deprecated
    public boolean liquid;//block state
    @SerializedName("solid")
    @Deprecated
    public boolean solid;//block state

    @SerializedName("hardness")
    public float hardness;//block state

    @SerializedName("toolRequired")
    public boolean toolRequired;//block state
    @SerializedName("opaque")
    public boolean opaque;//block state



//    public final ContextPredicate solidBlockPredicate; //block state
//    public final ContextPredicate suffocationPredicate; //block state
//    public final ContextPredicate blockVisionPredicate; //block state
//    public final ContextPredicate postProcessPredicate; //block state
//    public final ContextPredicate emissiveLightingPredicate; //block state
//    public final Optional<Offsetter> offsetter; //block state
//    public final Instrument instrument; //block state
    @SerializedName("blockBreakParticles")
    public boolean blockBreakParticles;//block state
    @SerializedName("replaceable")
    public boolean replaceable;//block state

//    protected ShapeCache shapeCache; //block state
//    public FluidState fluidState; //block state

    @SerializedName("resistance")
    public float resistance; // block settings
    @SerializedName("collidable")
    public boolean collidable;  // block settings

//    BlockSoundGroup soundGroup;// block settings

    @SerializedName("randomTicks")
    public boolean randomTicks;// block settings
    @SerializedName("slipperiness")
    public float slipperiness;// block settings

    @SerializedName("velocityMultiplier")
    public float velocityMultiplier;// block settings
    @SerializedName("jumpVelocityMultiplier")
    public float jumpVelocityMultiplier;// block settings

    @SerializedName("lootTableId")
    public String lootTableId;// block settings

    @SerializedName("asFluid")
    public FluidType asFluid;

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
}
