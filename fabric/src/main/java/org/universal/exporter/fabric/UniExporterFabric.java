package org.universal.exporter.fabric;

import net.fabricmc.api.ModInitializer;
import org.universal.exporter.UniExporter;

public class UniExporterFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UniExporter.init();
    }
}
