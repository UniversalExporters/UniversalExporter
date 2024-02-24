package org.universal.exporter.utils;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
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
    public final Map<String, ArrayList<BlockAndItemSerializable>> items = new HashMap<>();
    public final Map<String, ArrayList<BlockAndItemSerializable>> noShowItems = new HashMap<>();
    private final ArrayList<ItemGroup> groups = new ArrayList<>();
    private final boolean advancement;
    public ExporterHelper(ModidType modid, boolean advancement) {
        this(modid.asString(), advancement);
    }

    public ExporterHelper(String modid, boolean advancement) {
        this.modid = modid;
        this.advancement = advancement;
        modidDir = exporter.resolve(modid);
        replaceDirPath(modidDir);
    }

    public void itemExporter() {
        itemGroupItemExporter();
        registryItemExporter();
    }

    public void registryItemExporter() {
        for (Identifier id : Registries.ITEM.getIds()) {
            if (!items.containsKey(id.toString()) && id.getNamespace().equals(modid)) {
                ArrayList<BlockAndItemSerializable> list = noShowItems.containsKey(id.toString()) ? noShowItems.get(id.toString()) : new ArrayList<>();
                list.add(new ItemStackHelper(Registries.ITEM.get(id).getDefaultStack(), null, advancement));
                noShowItems.put(id.toString(), list);
            }
        }
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
                    if (id.getNamespace().equals(this.modid)) {
                        ArrayList<BlockAndItemSerializable> list = items.containsKey(id.toString()) ? items.get(id.toString()) : new ArrayList<>();
                        list.add(new ItemStackHelper(displayStack, group, advancement));
                        items.put(id.toString(), list);
                    }
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
