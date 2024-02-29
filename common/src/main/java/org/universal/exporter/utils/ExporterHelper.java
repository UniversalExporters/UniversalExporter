package org.universal.exporter.utils;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.platform.Mod;
import org.universal.exporter.utils.abstracts.BasicHelper;
import org.universal.exporter.utils.abstracts.ItemAndBlockHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType.armorType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ToolType.toolType;
import static org.universal.exporter.utils.ItemAndBlockUtils.nbt;
import static org.universal.exporter.utils.ItemAndBlockUtils.setItemName;

public class ExporterHelper {
    private final ItemAndBlockHelper itemAndBlockHelper;


    public ExporterHelper(ModidType modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        this(modid.asString(), advancement, ctx);
    }

    public ExporterHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        itemAndBlockHelper = new ItemAndBlockHelper(modid, advancement, ctx);
    }


    public void itemExporter() {
        itemAndBlockHelper.itemExporter();
    }
}
