package org.uniexporter.exporter.adapter.faces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Save {
    Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().disableJdkUnsafe().create();
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void save(Path path) {
        Path parent = path.getParent();
        if (!Files.exists(parent)) parent.toFile().mkdirs();
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            gson.toJson(this, writer);
        } catch (IOException ignored) {}
    }
}
