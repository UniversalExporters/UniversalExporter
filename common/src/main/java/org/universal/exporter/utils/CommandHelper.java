package org.universal.exporter.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.uniexporter.exporter.adapter.utils.ICommandHelper;
import org.universal.exporter.command.type.ModidType;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class CommandHelper implements ICommandHelper<ModidType> {
    protected final ModidType this$modid;

    protected final CommandContext<ServerCommandSource> context;

    public CommandHelper(ModidType this$modid, CommandContext<ServerCommandSource> context) {
        this.this$modid = this$modid;
        this.context = context;
    }

    @Override
    public ModidType modid() {
        return this$modid;
    }

    @Override
    public ModidType[] names() {
        return ModidType.values();
    }
}
