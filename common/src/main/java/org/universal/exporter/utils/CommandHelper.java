package org.universal.exporter.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.universal.exporter.command.type.ModidType;

import java.util.Arrays;
import java.util.function.Consumer;

public class CommandHelper {
    protected final ModidType this$modid;

    protected final CommandContext<ServerCommandSource> context;

    public CommandHelper(ModidType this$modid, CommandContext<ServerCommandSource> context) {
        this.this$modid = this$modid;
        this.context = context;
    }

    public void generationAll(Consumer<String> consumer) {
        if (this$modid != null)
            consumer.accept(this$modid.name());
        else
            Arrays.stream(ModidType.values()).map(ModidType::name).forEach(consumer);

    }
}
