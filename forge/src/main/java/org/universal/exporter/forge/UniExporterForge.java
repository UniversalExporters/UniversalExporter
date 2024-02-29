package org.universal.exporter.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.registry.CommandListening;
import org.universal.exporter.registry.forge.RegistryAllImpl;

import java.util.Map;
import java.util.function.Supplier;

@Mod(UniExporter.MOD_ID)
public class UniExporterForge {

//    public static final DeferredRegister<ArgumentSerializer<?, ?>> arguments = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, UniExporter.MOD_ID);

//    public static final RegistryObject<ConstantArgumentSerializer<ExporterArgumentType>> exporter = arguments.register("exporter", () -> ArgumentTypes.registerByClass(ExporterArgumentType.class, ConstantArgumentSerializer.of(ExporterArgumentType::exporter)));
//    public static final RegistryObject<ConstantArgumentSerializer<ModidArgumentType>> modid = arguments.register("modid", () -> ArgumentTypes.registerByClass(ModidArgumentType.class, ConstantArgumentSerializer.of(ModidArgumentType::modids)));
    public UniExporterForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::registry);
        forgeEventBus.addListener(this::registerCommands);
        UniExporter.init();
    }

    @SubscribeEvent
    public void registry(RegisterEvent event) {
        for (Map.Entry<Identifier, Supplier<ArgumentSerializer<?, ?>>> entry : RegistryAllImpl.arguments.entrySet()) {
            Identifier id = entry.getKey();
            Supplier<ArgumentSerializer<?, ?>> supplier = entry.getValue();
            event.register(RegistryKeys.COMMAND_ARGUMENT_TYPE, id, supplier);
        }

    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        for (CommandListening listen : RegistryAllImpl.listens) {
            listen.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }
    }
}
