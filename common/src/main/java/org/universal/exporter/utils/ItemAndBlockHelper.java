package org.universal.exporter.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.*;
import org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FoodType.foodType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType.nbtType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ToolType.toolType;
import static org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType.factorCalculationDataType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType.statusEffectInstanceType;
import static org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType.statusEffectType;
import static org.universal.exporter.utils.Base64Helper.icon;
import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;

@SuppressWarnings("UnusedReturnValue")
public class ItemAndBlockHelper extends DefaultHelper<ItemAndBlockHelper> {


    public ItemAndBlockHelper(String registerName, boolean this$advanceParameters) {
        super(registerName, this$advanceParameters);
    }

    @Override
    public ItemAndBlockHelper self() {
        return this;
    }

    public ItemAndBlockHelper defaultSetItemSettings(ItemStack stack, ItemType type) {
        type.maxStackSize = stack.getItem().getMaxCount();
        type.maxDurability = stack.getItem().getMaxDamage();
        TagKey.codec(RegistryKeys.ITEM).map(itemTagKey -> findAllOredicts(type, itemTagKey));
        type.icon(icon().itemStackToBase(stack));
        return self();
    }

    @NotNull
    public ItemType save(ItemStack stack, BlockAndItemSerializable blockAndItem) {
        return ItemType.itemType(type -> {
            defaultSetItemSettings(stack, type)
                    .language(blockAndItem, stack)
                    // LanguageHelper.get
                    .checkAndSaveInFood(stack, type)
                    // stack.getItem().isFood()
                    .blockSettings(type, stack)
                    // instanceof BlockItem block instanceof FluidBlock else other
                    .miningTool(stack, type) // instanceof MiningToolItem miningTool
                    .armor(stack, type) // instanceof ArmorItem armorItem
                    .fuelSet(stack, type) // add fuel time if fuel time == 0 item isn't fuel
                    .setHasNbt(type, stack.getNbt())// nbtCompound != null
            ;
        });
    }

    public ItemAndBlockHelper armor(ItemStack stack, ItemType type) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            type.armor = ArmorType.armorType(armor -> {
                armor.enchantability = armorItem.getEnchantability();
                armor.equipmentSlot = armorItem.getType().getEquipmentSlot().getName();
                armor.type = armorItem.getType().getName();
                armor.protection = armorItem.getProtection();
                armor.knockbackResistance = armorItem.getMaterial().getKnockbackResistance();
                armor.toughness = armorItem.getToughness();
                for (ItemStack matchingStack : armorItem.getMaterial().getRepairIngredient().getMatchingStacks()) {
                    armor.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
                }
            });

        }
        return self();
    }

    @SuppressWarnings("unchecked")
    public ItemAndBlockHelper miningTool(ItemStack stack, ItemType type) {
        if (stack.getItem() instanceof MiningToolItem miningTool) {

            type.tool = toolType(tool -> {
                tool.tagId = miningTool.effectiveBlocks.id().toString();
                ToolMaterial toolMaterial = ((Supplier<ToolMaterial>) miningTool).get();
                tool.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
                tool.attackDamage = toolMaterial.getAttackDamage();
                tool.miningLevel = toolMaterial.getMiningLevel();
                tool.enchantability = toolMaterial.getEnchantability();
                for (ItemStack matchingStack : toolMaterial.getRepairIngredient().getMatchingStacks()) {
                    tool.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
                }
            });
        }
        return self();
    }

    public NbtType nbt(NbtCompound nbtCompound) {
        NbtType nbtType = null;
        if (nbtCompound != null) {
            nbtType = nbtType(nbt -> nbtCompound.getKeys().forEach(key -> nbt.entries.put(key, Objects.requireNonNull(nbtCompound.get(key)))));
        }
        return nbtType;
    }

    public ItemAndBlockHelper fuelSet(ItemStack stack, ItemType type) {
        type.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(stack.getItem(), 0);
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

    public ItemAndBlockHelper blockSettings(ItemType type, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState defaultState = block.getDefaultState();

            type.asBlock = blockType(blockType ->
                    defaultBlockType(blockType, defaultState, block)
                            .advanceParameterBlockType(blockType, defaultState, block));
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
            language(statusEffectType, effect.getTranslationKey());

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



    public ItemAndBlockHelper checkAndSaveInFood(ItemStack stack, ItemType type) {
        if (stack.getItem().isFood()) {
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
