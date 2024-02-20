package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.Optional;
import java.util.function.Consumer;

public class BlockType implements Self<BlockType> {
    public int luminance;//block state
    public boolean hasSidedTransparency;//block state
    public boolean isAir;//block state
    public boolean burnable;//block state
    @Deprecated
    public boolean liquid;//block state
    @Deprecated
    public boolean solid;//block state

    public float hardness;//block state

    public boolean toolRequired;//block state
    public boolean opaque;//block state



//    public final ContextPredicate solidBlockPredicate; //block state
//    public final ContextPredicate suffocationPredicate; //block state
//    public final ContextPredicate blockVisionPredicate; //block state
//    public final ContextPredicate postProcessPredicate; //block state
//    public final ContextPredicate emissiveLightingPredicate; //block state
//    public final Optional<Offsetter> offsetter; //block state
//    public final Instrument instrument; //block state

    public boolean blockBreakParticles;//block state
    public boolean replaceable;//block state

//    protected ShapeCache shapeCache; //block state
//    public FluidState fluidState; //block state

    public float resistance; // block settings
    public boolean collidable;  // block settings

//    BlockSoundGroup soundGroup;// block settings

    public boolean randomTicks;// block settings
    public float slipperiness;// block settings

    public float velocityMultiplier;// block settings
    public float jumpVelocityMultiplier;// block settings

    public String lootTableId;// block settings

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
