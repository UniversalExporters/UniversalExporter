package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.faces.Self;

import java.util.function.Consumer;

public class LazyContainerType implements Self<LazyContainerType> {
    public String id;
    public boolean initialized;
    public CommandFunctionType function;

    public static LazyContainerType lazyContainerType(Consumer<LazyContainerType> consumer) {
        LazyContainerType t = new LazyContainerType();
        consumer.accept(t);
        return t;
    }

    public LazyContainerType id(String id) {
        this.id = id;
        return self();
    }

    public LazyContainerType initialized(boolean initialized) {
        this.initialized = initialized;
        return self();
    }

    public LazyContainerType function(CommandFunctionType function) {
        this.function = function;
        return self();
    }
}
