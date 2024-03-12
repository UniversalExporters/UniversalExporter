package org.universal.exporter.utils.abstracts;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.universal.exporter.command.type.AdvancementParamType;
import org.universal.exporter.platform.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BasicHelper {
    protected static final Path exporter = Mod.getGameFolder().resolve("exporter");
    protected final CommandContext<ServerCommandSource> ctx;
    protected final String modid;
    protected final Path modidDir;

    protected final BlockAndItems blockAndItems;
    protected final List<AdvancementParamType> types;

    public final AtomicBoolean hasTypes = new AtomicBoolean();

    protected void runAdvanceParams(AdvancementParamType type, Runnable runnable) {
        if (types == null) return;
        if (!hasTypes.get() || types.contains(type)) runnable.run();
    }

    public BasicHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx, AdvancementParamType[] types) {
        this.ctx = ctx;
        this.modid = modid;
        blockAndItems = new BlockAndItems();
        modidDir = exporter.resolve(modid);
        this.types = advancement ? Arrays.asList(types) : new ArrayList<>();
        if (!this.types.isEmpty())
            hasTypes.set(true);
        replaceDirPath(modidDir);
    }

    public static void replaceDirPath(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            try {
                Files.delete(path);
            } catch (IOException ignored) {}
            replaceDirPath(path);
        }
    }
}
