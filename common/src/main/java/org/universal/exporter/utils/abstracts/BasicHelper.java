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

public abstract class BasicHelper {
    protected static final Path exporter = Mod.getGameFolder().resolve("exporter");
    protected final CommandContext<ServerCommandSource> ctx;
    protected final String modid;
    protected final Path modidDir;

    protected final BlockAndItems blockAndItems;

    protected final boolean advancement;
    protected final List<AdvancementParamType> types;

    public BasicHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx, AdvancementParamType[] types) {
        this.ctx = ctx;
        this.modid = modid;
        this.advancement = advancement;
        blockAndItems = new BlockAndItems();
        modidDir = exporter.resolve(modid);
        this.types = Arrays.asList(types);
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
