package org.uniexporter.exporter.adapter;

import org.uniexporter.exporter.adapter.config.LoadAndGeneratorConfig;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.ItemType;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static final LoadAndGeneratorConfig<BlockAndItemSerializable> bas = new LoadAndGeneratorConfig<>(
            new File(System.getProperty("user.dir"), "build").toPath().resolve("test.json"),
            BlockAndItemSerializable.class,
            new BlockAndItemSerializable()
                    .englishName("stick")
                    .name("木棍")
                    .registerName("minecraft:stick")
                    .type(new ItemType())


    );
    public static void main(String[] args) {
        System.out.println(bas.get());
        bas.save();
    }
}
