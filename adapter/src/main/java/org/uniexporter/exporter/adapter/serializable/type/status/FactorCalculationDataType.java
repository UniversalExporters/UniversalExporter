package org.uniexporter.exporter.adapter.serializable.type.status;

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
}
