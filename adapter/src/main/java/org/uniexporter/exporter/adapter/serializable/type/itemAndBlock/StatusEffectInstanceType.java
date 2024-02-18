package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import java.util.ArrayList;

public class StatusEffectInstanceType {
    public StatusEffectType type;
    public int duration;
    public int amplifier;
    public boolean ambient;
    public boolean showParticles;
    public boolean showIcon;
    
    public StatusEffectInstanceType hiddenEffect;
    public FactorCalculationDataType factorCalculationData;
    public ArrayList<ItemStackType> curativeItems;
}
