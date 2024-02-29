package org.universal.exporter.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;
import java.util.List;

public class Mod {
    public final String modid;

    public Mod(String modid) {
        this.modid = modid;
    }

    @ExpectPlatform
    public static List<Mod> getMods() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGameFolder() {
        throw new AssertionError();
    }

    public static List<String> getModids() {
        return getMods().stream().map(mod -> mod.modid).toList();
    }
}
