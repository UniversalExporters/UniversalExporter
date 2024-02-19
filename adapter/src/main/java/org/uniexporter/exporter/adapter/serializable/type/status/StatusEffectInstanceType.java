package org.uniexporter.exporter.adapter.serializable.type.status;

import java.util.function.Consumer;

public class StatusEffectInstanceType {
    public StatusEffectType type;
    public int duration;
    public int amplifier;
    public boolean ambient;
    public boolean showParticles;
    public boolean showIcon;

    public StatusEffectInstanceType hiddenEffect;
    public FactorCalculationDataType factorCalculationData;

    public static StatusEffectInstanceType statusEffectInstanceType(Consumer<StatusEffectInstanceType> consumer) {
        StatusEffectInstanceType statusEffectInstanceType = new StatusEffectInstanceType();
        consumer.accept(statusEffectInstanceType);
        return statusEffectInstanceType;
    }
}
