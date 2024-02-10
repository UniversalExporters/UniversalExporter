package org.universal.exporter.forge;

import dev.architectury.platform.forge.EventBuses;
import org.universal.exporter.UniExporter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UniExporter.MOD_ID)
public class UniExporterForge {
    public UniExporterForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(UniExporter.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        UniExporter.init();
    }
}
