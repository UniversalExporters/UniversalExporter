package org.universal.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Path exporter = UniExporterExpectPlatform.getGameFolder().resolve("exporter");

    public static void init() {
        LOGGER.info("UniversalExporter is Loaded!");

    }
}
