package org.universal.exporter.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.FluidType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.platform.Mod;
import org.universal.exporter.registry.CommandListening;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;



public class UniExporterExpectPlatformImpl<A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>, I extends ArgumentSerializer<A, T>> {
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

    public static final ArrayList<CommandListening> listens = new ArrayList<>();

    public static void registryCommand(CommandListening listening) {
        listens.add(listening);
    }

    public static final Map<Identifier, Supplier<ArgumentSerializer<?, ?>>> arguments = new HashMap<>();



    @SuppressWarnings("RedundantCast")
    public static <A extends ArgumentType<?>> void registerArgument(Identifier id, Class<A> infoClass, Supplier<A> typeSupplier) {
        arguments.put(id, () -> ArgumentTypes.registerByClass(infoClass, ConstantArgumentSerializer.of(typeSupplier)));
    }
}
