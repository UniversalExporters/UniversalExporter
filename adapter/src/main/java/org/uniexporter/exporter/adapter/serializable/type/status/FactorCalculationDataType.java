package org.uniexporter.exporter.adapter.serializable.type.status;

import java.util.Objects;
import java.util.function.Consumer;

public class FactorCalculationDataType {
    public int paddingDuration;
    public float factorStart;
    public float factorTarget;
    public float factorCurrent;
    public int effectChangedTimestamp;
    public float factorPreviousFrame;
    public boolean hadEffectLastTick;

    public static FactorCalculationDataType factorCalculationDataType(Consumer<FactorCalculationDataType> consumer) {
        FactorCalculationDataType factorCalculationDataType = new FactorCalculationDataType();
        consumer.accept(factorCalculationDataType);
        return factorCalculationDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactorCalculationDataType that = (FactorCalculationDataType) o;
        return paddingDuration == that.paddingDuration && Float.compare(factorStart, that.factorStart) == 0 && Float.compare(factorTarget, that.factorTarget) == 0 && Float.compare(factorCurrent, that.factorCurrent) == 0 && effectChangedTimestamp == that.effectChangedTimestamp && Float.compare(factorPreviousFrame, that.factorPreviousFrame) == 0 && hadEffectLastTick == that.hadEffectLastTick;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paddingDuration, factorStart, factorTarget, factorCurrent, effectChangedTimestamp, factorPreviousFrame, hadEffectLastTick);
    }
}
