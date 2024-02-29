package org.universal.exporter;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universal.exporter.command.ExporterCommand;
import org.universal.exporter.command.argument.AdvancementParamArgumentType;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.registry.RegistryAll;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOGGER.info("UniversalExporter is Loaded!");
        RegistryAll.registryCommand(ExporterCommand::register);
        RegistryAll.registerArgument(id("exporter"), ExporterArgumentType.class, ExporterArgumentType::exporter);
        RegistryAll.registerArgument(id("modid"), ModidArgumentType.class, ModidArgumentType::modids);
        RegistryAll.registerArgument(id("advancement_params"), AdvancementParamArgumentType.class, AdvancementParamArgumentType::advancementParams);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
