package org.universal.exporter.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.uniexporter.exporter.adapter.serializable.type.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.platform.Mod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public class UniExporterExpectPlatformImpl {
    /**
     * This is our actual method to {@link UniExporterExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        var fluidType = fluid.getFluidType();
        return source
                .supportsBoating(fluidType.supportsBoating(null))
                .canSwim(fluidType.canSwim(null))
                .canDrown(fluidType.canDrownIn(null));
    }

    public static Path getGameFolder() {
        return FMLPaths.GAMEDIR.get();
    }

    public static List<Mod> getMods() {
        ModList modList = ModList.get();
        List<Mod> list = new ArrayList<>();
        for (IModInfo mod : modList.getMods()) {
            list.add(new Mod(mod.getModId()));
        }
        return list;
    }

    public static void registryCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {

    }
}
