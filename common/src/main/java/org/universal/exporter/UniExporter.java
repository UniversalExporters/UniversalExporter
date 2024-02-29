package org.universal.exporter;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;

import java.nio.file.Path;

import static org.universal.exporter.utils.Base64Helper.icon;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOGGER.info("UniversalExporter is Loaded!");
        UniExporterExpectPlatform.registryCommand(ExporterCommand::register);
        UniExporterExpectPlatform.registerArgument(id("exporter"), ExporterArgumentType.class, ExporterArgumentType::exporter);
        UniExporterExpectPlatform.registerArgument(id("modid"), ModidArgumentType.class, ModidArgumentType::modids);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
