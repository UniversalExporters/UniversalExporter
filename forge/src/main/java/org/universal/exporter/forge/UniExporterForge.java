package org.universal.exporter.forge;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;

@Mod(UniExporter.MOD_ID)
public class UniExporterForge {

    public static final DeferredRegister<ArgumentSerializer<?, ?>> arguments = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, UniExporter.MOD_ID);

    public static final RegistryObject<ConstantArgumentSerializer<ExporterArgumentType>> exporter = arguments.register("exporter", () -> ArgumentTypes.registerByClass(ExporterArgumentType.class, ConstantArgumentSerializer.of(ExporterArgumentType::exporter)));
    public static final RegistryObject<ConstantArgumentSerializer<ModidArgumentType>> modid = arguments.register("modid", () -> ArgumentTypes.registerByClass(ModidArgumentType.class, ConstantArgumentSerializer.of(ModidArgumentType::modids)));
    public UniExporterForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        arguments.register(modEventBus);
        forgeEventBus.register(this);
        UniExporter.init();
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ExporterCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }
}
