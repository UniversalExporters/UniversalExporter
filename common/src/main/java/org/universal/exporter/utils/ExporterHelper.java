package org.universal.exporter.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.universal.exporter.command.type.AdvancementParamType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.utils.abstracts.ItemAndBlockHelper;

public class ExporterHelper {
    private final ItemAndBlockHelper itemAndBlockHelper;
    private final AdvancementParamType[] types;


    public ExporterHelper(ModidType modid, boolean advancement, CommandContext<ServerCommandSource> ctx, AdvancementParamType... types) {
        this(modid.asString(), advancement, ctx, types);
    }

    public ExporterHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx, AdvancementParamType... types) {
        itemAndBlockHelper = new ItemAndBlockHelper(modid, advancement, ctx, types);
        this.types = types;
    }


    public void itemExporter() {
        itemAndBlockHelper.itemExporter();
    }
}
