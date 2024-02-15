package org.uniexporter.exporter.adapter.faces;

import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;

import java.lang.reflect.Field;

public interface Self<T extends Self<T>> {
    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }

    default T advancementParameters(String name, Object value) {
        try {
            Field declaredField = this.getClass().getDeclaredField(name);
            if (declaredField.isAnnotationPresent(AdvancementParameters.class)) {
                if (declaredField.getAnnotation(AdvancementParameters.class).used()) {
                    declaredField.setAccessible(true);
                    declaredField.set(this, value);
                }
            }
        } catch (Exception ignore) {}

        return self();
    }
}
