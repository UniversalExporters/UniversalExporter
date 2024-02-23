package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.faces.Save;
import org.uniexporter.exporter.adapter.faces.Self;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class BlockAndItems implements Save, Self<BlockAndItems> {
    public ConcurrentHashMap<String, BlockAndItemSerializable> items;
    public ConcurrentHashMap<String, BlockAndItemSerializable> foods;
    public ConcurrentHashMap<String, BlockAndItemSerializable> armors;
    public ConcurrentHashMap<String, BlockAndItemSerializable> tools;
    public ConcurrentHashMap<String, BlockAndItemSerializable> blocks;
    public ConcurrentHashMap<String, BlockAndItemSerializable> fluids;

    public BlockAndItems fluid(String registerName, BlockAndItemSerializable fluid) {
        if (this.fluids == null) this.fluids = new ConcurrentHashMap<>();
        this.fluids.put(registerName, fluid);
        return self();
    }

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

    public BlockAndItems food(String registryName, BlockAndItemSerializable food) {
        if (this.foods == null) foods = new ConcurrentHashMap<>();
        foods.put(registryName, food);
        return self();
    }

    public BlockAndItemSerializable find(String registerName) {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                //noinspection unchecked
                ConcurrentHashMap<String, BlockAndItemSerializable> o = (ConcurrentHashMap<String, BlockAndItemSerializable>) declaredField.get(this);
                if (o != null && o.containsKey(registerName))
                    return o.get(registerName);

            } catch (IllegalAccessException ignored) {
            }
        }
        return null;

    }

//    @SafeVarargs
//    public final BlockAndItemSerializable map(String registerName, ConcurrentHashMap<String, BlockAndItemSerializable>... maps) {
//        for (ConcurrentHashMap<String, BlockAndItemSerializable> map : maps) {
//            if (map != null && map.containsKey(registerName)) {
//                return map.get(registerName);
//            }
//        }
//        return null;
//    }

}
