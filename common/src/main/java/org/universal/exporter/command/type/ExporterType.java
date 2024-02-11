package org.universal.exporter.command.type;

import net.minecraft.util.StringIdentifiable;

public enum ExporterType implements StringIdentifiable {
    itemandblock, advancements;

    @Override
    public String asString() {
        return name();
    }
}
