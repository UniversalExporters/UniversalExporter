package org.universal.exporter;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniexporter.exporter.adapter.serializable.type.IconType;

import java.nio.file.Path;

import static org.universal.exporter.utils.Base64Helper.icon;

public class UniExporter {
    public static final String MOD_ID = "uni_exporter";
    public static final String MOD_NAME = "UniversalExporter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOGGER.info("UniversalExporter is Loaded!");
    }
}
