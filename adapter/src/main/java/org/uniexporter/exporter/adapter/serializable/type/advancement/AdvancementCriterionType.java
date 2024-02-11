package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.faces.Self;

public class AdvancementCriterionType implements Self<AdvancementCriterionType> {
    public String registerName;

    public AdvancementCriterionType registerName(String registerName) {
        this.registerName = registerName;
        return self();
    }
}
