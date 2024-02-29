package org.universal.exporter.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.universal.exporter.platform.Mod.getMods;

public class ModImpl {
    public static List<Mod> getMods() {
        List<Mod> mods = new ArrayList<>();
        for (ModContainer allMod : FabricLoader.getInstance().getAllMods()) {
            mods.add(new Mod(allMod.getMetadata().getId()));
        }
        return mods;
    }

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getGameFolder() {
        return FabricLoader.getInstance().getGameDir();
    }


}
