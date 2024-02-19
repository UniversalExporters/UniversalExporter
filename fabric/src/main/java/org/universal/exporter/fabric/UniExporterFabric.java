package org.universal.exporter.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import org.universal.exporter.UniExporter;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;

public class UniExporterFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UniExporter.init();
        ArgumentTypeRegistry.registerArgumentType(new Identifier(UniExporter.MOD_ID, "exporter"), ExporterArgumentType.class, ConstantArgumentSerializer.of(ExporterArgumentType::exporter));
        ArgumentTypeRegistry.registerArgumentType(new Identifier(UniExporter.MOD_ID, "modid"), ModidArgumentType.class, ConstantArgumentSerializer.of(ModidArgumentType::modids));
        CommandRegistrationCallback.EVENT.register(ExporterCommand::register);

    }
}
