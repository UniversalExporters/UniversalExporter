package org.universal.exporter.command.type;

import net.minecraft.util.StringIdentifiable;

public enum AdvancementParamType implements StringIdentifiable {
    test;

    @Override
    public String asString() {
        return name();
    }
}
