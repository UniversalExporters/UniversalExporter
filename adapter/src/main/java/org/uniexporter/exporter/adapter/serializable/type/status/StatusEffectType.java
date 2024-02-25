package org.uniexporter.exporter.adapter.serializable.type.status;

import org.uniexporter.exporter.adapter.serializable.type.NameType;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StatusEffectType extends NameType {

//    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();

//    private final StatusEffectCategory category;
    public int color;
    public FactorCalculationDataType factorCalculationDataSupplier;
    public static StatusEffectType statusEffectType(Consumer<StatusEffectType> consumer) {
        StatusEffectType statusEffectType = new StatusEffectType();
        consumer.accept(statusEffectType);
        return statusEffectType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusEffectType that = (StatusEffectType) o;
        return color == that.color && Objects.equals(factorCalculationDataSupplier, that.factorCalculationDataSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, factorCalculationDataSupplier);
    }
}
