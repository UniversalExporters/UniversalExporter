package org.universal.exporter;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universal.exporter.command.ExporterCommand;

import java.nio.file.Path;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Path exporter = Platform.getGameFolder().resolve("exporter");

    public static void init() {
        CommandRegistrationEvent.EVENT.register(ExporterCommand::register);
        LOGGER.info(UniExporterExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
