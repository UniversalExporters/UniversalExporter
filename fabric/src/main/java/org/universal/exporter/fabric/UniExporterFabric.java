package org.universal.exporter.fabric;

import org.universal.exporter.UniExporter;
import net.fabricmc.api.ModInitializer;

public class UniExporterFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UniExporter.init();
    }
}
