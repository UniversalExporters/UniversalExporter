package org.universal.exporter.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.command.EnumArgument;
import org.universal.exporter.UniExporter;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ExporterArgumentType;

import java.util.ArrayList;
import java.util.List;

@Mod(UniExporter.MOD_ID)
public class UniExporterForge {

    public static final DeferredRegister<ArgumentSerializer<?, ?>> arguments = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, UniExporter.MOD_ID);

    public static final RegistryObject<ConstantArgumentSerializer<ExporterArgumentType>> exporter = arguments.register("exporter", () -> ArgumentTypes.registerByClass(ExporterArgumentType.class, ConstantArgumentSerializer.of(ExporterArgumentType::exporter)));

    public UniExporterForge() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        arguments.register(modEventBus);
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        UniExporter.init();
        forgeEventBus.register(this);

    }

    @SubscribeEvent
    public void register(RegisterCommandsEvent event) {
        ExporterCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }
}
