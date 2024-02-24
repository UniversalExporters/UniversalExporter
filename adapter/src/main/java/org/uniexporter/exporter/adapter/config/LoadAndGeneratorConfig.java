package org.uniexporter.exporter.adapter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * auto generation
 * @author baka4n
 * @since 8
 * @param <T> config
 */
public class LoadAndGeneratorConfig<T> implements Supplier<T> {

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private final T defaultConfig;
    private final Path configPath;
    private final Class<T> clazz;
    private T instance;
    public LoadAndGeneratorConfig(Path configPath, Class<T> clazz, T defaultConfig) {
        this.configPath = configPath;
        this.clazz = clazz;
        this.defaultConfig = defaultConfig;
        final Path parent = configPath.getParent();
        try {
            Files.createDirectories(parent);
        } catch (IOException ignored) {}
        try(BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            instance = GSON.fromJson(reader, clazz);
        } catch (IOException ignored) {}

    }

    public void set(Consumer<T> consumer) {
        consumer.accept(instance);
        if (clazz.isAnnotationPresent(AutoSave.class)) {
            save();
        }
    }

    public void save() {
        try(BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
            GSON.toJson(instance, writer);
        } catch (IOException ignored) {}
    }

    @Override
    public T get() {
        if (instance == null) {
            instance = defaultConfig;
            save();
        }
        return instance;
    }
}
