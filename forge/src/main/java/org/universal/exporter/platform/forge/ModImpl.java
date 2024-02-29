package org.universal.exporter.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModImpl {

    public static List<Mod> getMods() {
        ModList modList = ModList.get();
        List<Mod> list = new ArrayList<>();
        for (IModInfo mod : modList.getMods()) {
            list.add(new Mod(mod.getModId()));
        }
        return list;
    }

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getGameFolder() {
        return FMLPaths.GAMEDIR.get();
    }
}
