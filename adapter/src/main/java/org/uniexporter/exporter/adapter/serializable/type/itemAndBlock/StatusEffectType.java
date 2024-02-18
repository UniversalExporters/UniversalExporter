package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class StatusEffectType {
    private ConcurrentHashMap<EntityAttribute, EntityAttributeModifier> attributeModifiers = new ConcurrentHashMap<>();
    private StatusEffectCategory category;
    private int color;

    public String name;
    public String englishName;
    private FactorCalculationDataType factorCalculationDataSupplier;
    private Object effectRenderer;
}
