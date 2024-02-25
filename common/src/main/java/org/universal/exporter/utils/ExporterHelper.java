package org.universal.exporter.utils;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.type.ModidType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExporterHelper {
    private static final Path exporter = UniExporterExpectPlatform.getGameFolder().resolve("exporter");
    private final String modid;
    private final Path modidDir;
    private final Path itemAndBlockExporter;
    public final Map<String, ArrayList<BlockAndItemSerializable>> items = new HashMap<>();
    public final Map<String, ArrayList<BlockAndItemSerializable>> noShowItems = new HashMap<>();
    public final Map<String, ArrayList<BlockAndItemSerializable>> recipeItems = new HashMap<>();
    private final ArrayList<ItemGroup> groups = new ArrayList<>();
    private final boolean advancement;
    private final BlockAndItems blockAndItems;
    private final CommandContext<ServerCommandSource> ctx;
    public ExporterHelper(ModidType modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        this(modid.asString(), advancement, ctx);
    }

    public ExporterHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        this.ctx = ctx;
        this.modid = modid;
        this.advancement = advancement;
        blockAndItems = new BlockAndItems();
        modidDir = exporter.resolve(modid);
        itemAndBlockExporter = modidDir.resolve("item-and-block.json");
        replaceDirPath(modidDir);
    }

    public void itemExporter() {
        itemGroupItemExporter();
        registryItemExporter();
        recipesItemExporter();
    }

    public void saveItemExporter() {
        items.forEach(this::setItems);
        noShowItems.forEach(this::setItems);//导出除tab之外的注册表内的物品
    }

    private void setItems(String registerName, ArrayList<BlockAndItemSerializable> serializables) {
        for (BlockAndItemSerializable serializable : serializables) {
            if (serializable.type.asBlock != null) {
                if (serializable.type.asBlock.asFluid != null) {
                    blockAndItems.fluid(registerName, serializable);
                } else {
                    blockAndItems.block(registerName, serializable);
                }
            }
            else if (serializable.type.asFluid != null) {
                blockAndItems.bucket(registerName, serializable);
            }
            else if (serializable.type.armor != null) {
                blockAndItems.armor(registerName, serializable);
            }
            else if (serializable.type.tool != null) {
                blockAndItems.tool(registerName, serializable);
            }
            else if (serializable.type.asFood != null) {
                blockAndItems.food(registerName, serializable);
            }
            else {
                blockAndItems.item(registerName, serializable);
            }

        }
    }

    public void registryItemExporter() {
        for (Identifier id : Registries.ITEM.getIds()) {
            if (!items.containsKey(id.toString()) && id.getNamespace().equals(modid)) {
                put(id, new ItemStackHelper(Registries.ITEM.get(id).getDefaultStack(), null, advancement), recipeItems);
            }
        }
    }

    public void recipesItemExporter() {
        for (Recipe<?> recipe : ctx.getSource().getServer().getRecipeManager().values()) {
            ItemStack output = recipe.getOutput(ctx.getSource().getRegistryManager());
            Identifier id = Registries.ITEM.getId(output.getItem());
            put(id, new ItemStackHelper(output, null, advancement), recipeItems);
        }
    }

    public void put(Identifier id, ItemStackHelper helper, Map<String, ArrayList<BlockAndItemSerializable>> items) {
        if (equalsItem(id, helper)) {
            ArrayList<BlockAndItemSerializable> list = items.containsKey(id.toString()) ? items.get(id.toString()) : new ArrayList<>();
            list.add(helper);
            items.put(id.toString(), list);
        }
    }

    public boolean equalsItem(Identifier id, BlockAndItemSerializable serializable) {
        return
                (!(items.containsKey(id.toString()) || items.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid)) ||
                (!(noShowItems.containsKey(id.toString()) || noShowItems.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid)) ||
                (!(recipeItems.containsKey(id.toString()) || recipeItems.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid))
                ;
    }

    public void itemGroupItemExporter() {
        MinecraftClient instance = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
        if (networkHandler == null || instance.world == null) return;
        DynamicRegistryManager registryManager = instance.world.getRegistryManager();
        ItemGroups.updateDisplayContext(networkHandler.getEnabledFeatures(), true, registryManager);
        ArrayList<ItemGroup> groups = getGroups();
        for (ItemGroup group : ItemGroups.getGroups()) {
            if (!groups.contains(group)) {
                Collection<ItemStack> displayStacks = group.getDisplayStacks();
                for (ItemStack displayStack : displayStacks) {
                    Identifier id = Registries.ITEM.getId(displayStack.getItem());
                    put(id, new ItemStackHelper(displayStack, group, advancement), items);
                }
            }
        }

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

    public ArrayList<ItemGroup> getGroups() {
        if (groups.isEmpty())
            groups.addAll(ImmutableList.of(ItemGroups.HOTBAR, ItemGroups.INVENTORY, ItemGroups.SEARCH).stream().map(Registries.ITEM_GROUP::get).toList());
        return groups;
    }
}
