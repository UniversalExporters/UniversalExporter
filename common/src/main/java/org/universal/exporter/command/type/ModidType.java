package org.universal.exporter.command.type;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum ModidType implements StringIdentifiable {
    ;
    ModidType() {
    }

    @Override
    public String asString() {
        return name();
    }
}
