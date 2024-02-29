package org.universal.exporter.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.platform.Mod;
import org.universal.exporter.registry.CommandListening;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UniExporterExpectPlatformImpl {
    /**
     * This is our actual method to {@link UniExporterExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static FluidType fluidType(FluidType source, FlowableFluid fluid) {
        return source;
    }

    public static Path getGameFolder() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<Mod> getMods() {
        List<Mod> mods = new ArrayList<>();
        for (ModContainer allMod : FabricLoader.getInstance().getAllMods()) {
           mods.add(new Mod(allMod.getMetadata().getId()));
        }
        return mods;
    }

    public static void registryCommand(CommandListening listening) {
        CommandRegistrationCallback.EVENT.register(listening::register);
    }

    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        ArgumentTypeRegistry.registerArgumentType(id, infoClass, ConstantArgumentSerializer.of(typeSupplier));
    }


}
