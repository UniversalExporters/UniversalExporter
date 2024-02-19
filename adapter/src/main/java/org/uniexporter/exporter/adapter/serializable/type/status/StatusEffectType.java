package org.uniexporter.exporter.adapter.serializable.type.status;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StatusEffectType {

//    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();

//    private final StatusEffectCategory category;
    public int color;
    public String name;
    public String englishName;
    public FactorCalculationDataType factorCalculationDataSupplier;
    public static StatusEffectType statusEffectType(Consumer<StatusEffectType> consumer) {
        StatusEffectType statusEffectType = new StatusEffectType();
        consumer.accept(statusEffectType);
        return statusEffectType;
    }
}
