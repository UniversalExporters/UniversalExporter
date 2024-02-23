package org.uniexporter.exporter.adapter.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public interface ICommandHelper<T extends Enum<T>> {
    T[] names();
    T modid();
    default void generationAll(Consumer<String> consumer) {
        if (modid() == null) {
            Arrays.stream(names()).map(T::name).forEach(consumer);
        } else {
            consumer.accept(modid().name());
        }
    }
}
