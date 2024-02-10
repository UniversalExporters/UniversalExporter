package org.universal.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOGGER.info(UniExporterExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
