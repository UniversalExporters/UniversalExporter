package org.universal.exporter.command.type;

import net.minecraft.util.StringIdentifiable;

public enum ExporterType implements StringIdentifiable {
    itemandblock, advancements;

    public Runnable runnable;

    public void setRunnable(Runnable... runnable) {
        if (this.runnable == null && runnable.length != 0) {
            this.runnable = runnable[0];
        }
    }
    @Override
    public String asString() {
        return name();
    }

}
