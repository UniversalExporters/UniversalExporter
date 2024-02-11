package org.uniexporter.exporter.adapter.faces;

import java.lang.reflect.Field;

public interface Self<T extends Self<T>> {
    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T advancementParameters(String name, Object value) throws Exception {
        Field declaredField = this.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        declaredField.set(this, value);
        return (T) this;
    }
}
