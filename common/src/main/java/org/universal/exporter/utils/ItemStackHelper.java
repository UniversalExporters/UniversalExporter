package org.universal.exporter.utils;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectType;

import static org.uniexporter.exporter.adapter.serializable.type.status.FactorCalculationDataType.factorCalculationDataType;

public class ItemStackHelper extends BlockAndItemSerializable {
    public ItemStackHelper(ItemStack stack, ItemGroup group, boolean advancement) {

        type = new ItemType();
        type.maxStackSize = stack.getMaxCount();
        type.maxDurability = stack.getMaxDamage();
        stack.streamTags().forEach(itemTagKey -> {
            type.OredictList(itemTagKey.id().toString());
        });
//        type.icon = new IconHelper(stack);
//        if (group != null) {
//            type.tab = new NameType();
//            String translationKey = ((TranslatableTextContent) group.getDisplayName().getContent()).getKey();
//            en_us().get(type.tab, translationKey);
//            zh_cn().get(type.tab, translationKey);
//        }
//        Item item = stack.getItem();
//        FoodComponent foodComponent = item.getFoodComponent();
//        if (foodComponent != null) {
//            type.asFood = new FoodType();
//            type.asFood.hunger = foodComponent.getHunger();
//            type.asFood.saturationModifier = foodComponent.getSaturationModifier();
//            type.asFood.meat = foodComponent.isMeat();
//            type.asFood.alwaysEdible = foodComponent.isAlwaysEdible();
//            type.asFood.snack = foodComponent.isSnack();
//            for (Pair<StatusEffectInstance, Float> pair : foodComponent.statusEffects) {
//                StatusEffectInstance first = pair.getFirst();
//                Float second = pair.getSecond();
//                type.asFood.statusEffects(statusEffectInstanceType(s -> foodType(s, first)), second);
//            }
//
//        }
//        if (item instanceof BlockItem blockItem) {
//            Block block = blockItem.getBlock();
//            BlockState defaultState = block.getDefaultState();
//            type.asBlock = new BlockType();
//            type.asBlock.luminance = defaultState.getLuminance();
//            type.asBlock.collidable = block.collidable;
//
//            type.asBlock.hardness = defaultState.getHardness(null, null);
//
//            type.asBlock.burnable = defaultState.isBurnable();
//
//            type.asBlock.resistance = block.getBlastResistance();
//            type.asBlock.toolRequired = defaultState.isToolRequired();
//            type.asBlock.opaque = defaultState.isOpaque();
//            type.asBlock.replaceable = defaultState.isReplaceable();
//
//            type.asBlock.slipperiness = block.getSlipperiness();//ice
//            type.asBlock.velocityMultiplier = block.getVelocityMultiplier();//run speed
//            type.asBlock.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();//jump height
//            type.asBlock.lootTableId = block.getLootTableId().toString();
//            if (advancement) {
//                type.asBlock.blockBreakParticles = defaultState.hasBlockBreakParticles();
//                type.asBlock.hasSidedTransparency = defaultState.hasSidedTransparency();
//                type.asBlock.isAir = defaultState.isAir();
//                type.asBlock.liquid = defaultState.isLiquid();
//                type.asBlock.solid = defaultState.isSolid();
//                type.asBlock.randomTicks = block.randomTicks;
//            }
//            if (block instanceof FluidBlock fluidBlock) {
//                type.asBlock.asFluid = new FluidType();
//                UniExporterExpectPlatform.fluidType(type.asBlock.asFluid, (FlowableFluid) fluidBlock.getFluidState(defaultState).getFluid());
//            }
//        }
//        else if (item instanceof BucketItem bucketItem) {
//            type.asFluid = Registries.FLUID.getId(bucketItem.fluid).toString();
//        }
//        else if (item instanceof MiningToolItem toolItem) {
//            type.tool = new ToolType();
//            type.tool.tagId = toolItem.effectiveBlocks.id().toString();
//            ToolMaterial toolMaterial = ((ToolMaterialSupplier) toolItem).universalExporter$get();
//            type.tool.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
//            type.tool.attackDamage = toolMaterial.getAttackDamage();
//            type.tool.miningLevel = toolMaterial.getMiningLevel();
//            type.tool.enchantability = toolMaterial.getEnchantability();
//            for (ItemStack matchingStack : toolMaterial.getRepairIngredient().getMatchingStacks()) {
//                type.tool.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
//            }
//        }
//        else if (item instanceof ArmorItem armorItem) {
//            type.armor = new ArmorType();
//            type.armor.type = armorItem.getType().getName();
//            type.armor.equipmentSlot = armorItem.getType().getEquipmentSlot().getName();
//            type.armor.enchantability = armorItem.getEnchantability();
//            type.armor.protection = armorItem.getProtection();
//            type.armor.toughness = armorItem.getToughness();
//            type.armor.knockbackResistance = armorItem.getMaterial().getKnockbackResistance();
//            for (ItemStack matchingStack : armorItem.getMaterial().getRepairIngredient().getMatchingStacks()) {
//                type.armor.repairIngredient(Registries.ITEM.getId(matchingStack.getItem()).toString(), nbt(matchingStack.getNbt()));
//            }
//
//        }
//        type.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(stack.getItem(), 0);
//        type.nbt = nbt(stack.getNbt());
    }

    public static NbtType nbt(NbtCompound nbtCompound) {
        NbtType nbtType = null;
        if (nbtCompound != null) {
            nbtType = new NbtType();
            for (String key : nbtCompound.getKeys()) {
                NbtElement nbtElement = nbtCompound.get(key);
                Object o;
                if (nbtElement instanceof NbtCompound compound) {
                    o = nbt(compound);
                } else {
                    o = nbtElement.asString();
                }
                nbtType.entry(key, o);
            }
        }
        return nbtType;
    }

    private static void foodType(StatusEffectInstanceType s, StatusEffectInstance first) {
        s.type = StatusEffectType.statusEffectType(st -> {
            String translationKey = first.type.getTranslationKey();

            st.color = first.type.getColor();
            st.factorCalculationDataSupplier = factorCalculationDataType(f -> {
                StatusEffectInstance.FactorCalculationData factorCurrent = first.type.getFactorCalculationDataSupplier().get();
                f.paddingDuration = factorCurrent.paddingDuration;
                f.factorStart = factorCurrent.factorStart;
                f.factorTarget = factorCurrent.factorTarget;
                f.factorCurrent = factorCurrent.factorCurrent;
                f.effectChangedTimestamp = factorCurrent.effectChangedTimestamp;
                f.factorPreviousFrame = factorCurrent.factorPreviousFrame;
                f.hadEffectLastTick = factorCurrent.hadEffectLastTick;
            });
        });
    }
}
