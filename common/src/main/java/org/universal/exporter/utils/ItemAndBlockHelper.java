package org.universal.exporter.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.universal.exporter.UniExporterExpectPlatform;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FoodType.foodType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType.nbtType;
import static org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType.factorCalculationDataType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType.statusEffectInstanceType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType.statusEffectType;
import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;

@SuppressWarnings("UnusedReturnValue")
public class ItemAndBlockHelper extends DefaultHelper<ItemAndBlockHelper> {
    private final String registerName;
    private final boolean this$advanceParameters;
    public ItemAndBlockHelper(String registerName, boolean this$advanceParameters) {
        this.registerName = registerName;
        this.this$advanceParameters = this$advanceParameters;
    }

    @Override
    public ItemAndBlockHelper self() {
        return this;
    }

    public ItemAndBlockHelper checkAndSaveInBlockOrFluids(BlockAndItems blockAndItems, BlockAndItemSerializable blockAndItem, BlockType blockType, Block block, BlockState defaultState) {
        if (block instanceof FluidBlock fluidBlock) {
            if (blockAndItems.fluids == null) blockAndItems.fluids = new ConcurrentHashMap<>();
            blockAndItems.fluids.put(registerName, blockAndItem);
            blockType.asFluid = UniExporterExpectPlatform.fluidType(new FluidType(), (FlowableFluid) fluidBlock.getFluidState(defaultState).getFluid());
        } else {
            if (blockAndItems.blocks == null) blockAndItems.blocks = new ConcurrentHashMap<>();
            blockAndItems.blocks.put(registerName, blockAndItem);
        }
        return self();
    }

    @SuppressWarnings("deprecation")
    public ItemAndBlockHelper advanceParameterBlockType(BlockType blockType, BlockState defaultState, Block block) {
        if (this$advanceParameters) {
            blockType.blockBreakParticles = defaultState.hasBlockBreakParticles();
            blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
            blockType.isAir = defaultState.isAir();
            blockType.liquid = defaultState.isLiquid();
            blockType.solid = defaultState.isSolid();
            blockType.ticksRandomly = defaultState.hasRandomTicks();
            blockType.randomTicks = block.randomTicks;
        }
        return self();
    }

    public ItemAndBlockHelper defaultBlockType(BlockType blockType, BlockState defaultState, Block block) {
        blockType.luminance = defaultState.getLuminance();
        blockType.collidable = block.collidable;
        blockType.hardness = block.getHardness();

        blockType.burnable = defaultState.isBurnable();

        blockType.resistance = block.getBlastResistance();
        blockType.toolRequired = defaultState.isToolRequired();
        blockType.opaque = defaultState.isOpaque();
        blockType.replaceable = defaultState.isReplaceable();

        blockType.slipperiness = block.getSlipperiness();//ice
        blockType.velocityMultiplier = block.getVelocityMultiplier();//run speed
        blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();//jump height
        blockType.lootTableId = block.getLootTableId().toString();
        return self();
    }

    public ItemAndBlockHelper blockSettings(BlockAndItems blockAndItems, BlockAndItemSerializable blockAndItem, ItemType type, Item item, ItemAndBlockHelper helper) {
        if (item instanceof BlockItem blockItem) {

            Block block = blockItem.getBlock();
            BlockState defaultState = block.getDefaultState();

            type.asBlock = blockType(blockType ->
                    defaultBlockType(blockType, defaultState, block)
                            .advanceParameterBlockType(blockType, defaultState, block)
                            .checkAndSaveInBlockOrFluids(blockAndItems, blockAndItem, blockType, block, defaultState));
        }
        return self();
    }

    public ItemAndBlockHelper setHasNbt(ItemType type, NbtCompound nbtCompound) {
        if (nbtCompound != null) {
            type.nbt = nbtType(nbt -> nbtCompound.getKeys().forEach(key -> nbt.entries.put(key, Objects.requireNonNull(nbtCompound.get(key)))));
        }
        return self();
    }

    public ItemAndBlockHelper statusEffect(StatusEffectInstanceType type, StatusEffectInstance instance) {
        StatusEffect effect = instance.type;
        type.type = statusEffectType(statusEffectType -> {
            statusEffectType.color = effect.getColor();
            var translationKey = effect.getTranslationKey();
            if (en_us().hasTranslation(translationKey))
                statusEffectType.englishName = en_us().get(translationKey);
            if (zh_cn().hasTranslation(translationKey))
                statusEffectType.name = zh_cn().get(translationKey);
            effect.getFactorCalculationDataSupplier().ifPresent(factorCalculationData ->
                    type.factorCalculationData = factorCalculationDataType(factorCalculationDataType ->
                            factorCalculationData(factorCalculationDataType, factorCalculationData)
                    ));
        });
        type.ambient = instance.ambient;
        type.amplifier = instance.amplifier;
        type.duration = instance.duration;
        type.showParticles = instance.showParticles;
        type.showIcon = instance.showIcon;

        if (instance.hiddenEffect != null) {
            type.hiddenEffect = statusEffectInstanceType(statusEffect -> {
                statusEffect(statusEffect, instance.hiddenEffect);
            });
        }
        instance.getFactorCalculationData().ifPresent(factorCalculationData ->
                type.factorCalculationData = factorCalculationDataType(factorCalculationDataType ->
                        factorCalculationData(factorCalculationDataType, factorCalculationData)
                ));
        return self();
    }

    public ItemAndBlockHelper factorCalculationData(FactorCalculationDataType type, StatusEffectInstance.FactorCalculationData data) {
        type.paddingDuration = data.paddingDuration;
        type.factorStart = data.factorStart;
        type.factorTarget = data.factorTarget;
        type.factorCurrent = data.factorCurrent;
        type.effectChangedTimestamp = data.effectChangedTimestamp;
        type.factorPreviousFrame = data.factorPreviousFrame;
        type.hadEffectLastTick = data.hadEffectLastTick;
        return self();
    }

    public void foodTypeSet(ItemStack stack, ItemType type, Item item) {
        var foodComponent = item.getFoodComponent();
        assert foodComponent != null;
        type.asFood = foodType(food -> {
            food.hunger = foodComponent.getHunger();
            food.meat = foodComponent.isMeat();
            food.alwaysEdible = foodComponent.isAlwaysEdible();
            food.snack = foodComponent.isSnack();
            food.saturationModifier = foodComponent.getSaturationModifier();
            type.maxUseTime = item.getMaxUseTime(stack);
            foodComponent.statusEffects.forEach(pair -> {
                if (food.statusEffects == null) food.statusEffects = new ConcurrentHashMap<>();
                food.statusEffects(statusEffectInstanceType(statusEffectInstance -> {
                    statusEffect(statusEffectInstance, pair.getFirst());
                }), pair.getSecond());
            });
        });
    }



    public ItemAndBlockHelper checkAndSaveInFood(ItemStack stack, BlockAndItems blockAndItems, BlockAndItemSerializable blockAndItem, ItemType type) {
        if (stack.getItem().isFood()) {
            blockAndItems.food(registerName, blockAndItem);
            foodTypeSet(stack, type, stack.getItem());
        }
        return self();
    }

    @NotNull
    public static TagKey<Item> findAllOredicts(ItemType type, TagKey<Item> itemTagKey) {
        if (type.OredictList == null) type.OredictList = new ArrayList<>();
        type.OredictList.add(itemTagKey.id().toString());
        return itemTagKey;
    }
}
