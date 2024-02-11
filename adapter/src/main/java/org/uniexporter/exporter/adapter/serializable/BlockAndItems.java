package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.faces.Save;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockAndItems implements Save, Self<BlockAndItems> {
    public ConcurrentHashMap<String, BlockAndItemSerializable> items;
    public ConcurrentHashMap<String, BlockAndItemSerializable> armors;
    public ConcurrentHashMap<String, BlockAndItemSerializable> tools;
    public ConcurrentHashMap<String, BlockAndItemSerializable> blocks;

    public BlockAndItems item(String registryName, BlockAndItemSerializable item) {
        if (this.items == null) this.items = new ConcurrentHashMap<>();
        items.put(registryName, item);
        return self();
    }
    public BlockAndItems armor(String registryName, BlockAndItemSerializable armor) {
        if (this.armors == null) this.armors = new ConcurrentHashMap<>();
        armors.put(registryName, armor);
        return self();
    }
    public BlockAndItems tool(String registryName, BlockAndItemSerializable tool) {
        if (this.tools == null) this.tools = new ConcurrentHashMap<>();
        tools.put(registryName, tool);
        return self();
    }
    public BlockAndItems block(String registryName, BlockAndItemSerializable block) {
        if (this.blocks == null) this.blocks = new ConcurrentHashMap<>();
        blocks.put(registryName, block);
        return self();
    }

}
