package org.uniexporter.exporter.adapter.serializable.type.status;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusEffectInstanceType that = (StatusEffectInstanceType) o;
        return duration == that.duration && amplifier == that.amplifier && ambient == that.ambient && showParticles == that.showParticles && showIcon == that.showIcon && Objects.equals(type, that.type) && Objects.equals(hiddenEffect, that.hiddenEffect) && Objects.equals(factorCalculationData, that.factorCalculationData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
    }
}
