package org.uniexporter.exporter.adapter.serializable;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.faces.Save;
import org.uniexporter.exporter.adapter.faces.Self;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class BlockAndItems implements Save, Self<BlockAndItems> {
    @SerializedName("items")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> items;
    @SerializedName("foods")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> foods;
    @SerializedName("armors")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> armors;
    @SerializedName("tools")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> tools;
    @SerializedName("blocks")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> blocks;
    @SerializedName("fluids")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> fluids;
    @SerializedName("buckets")
    public ConcurrentHashMap<String, ArrayList<BlockAndItemSerializable>> buckets;


    public BlockAndItems bucket(String registryName, BlockAndItemSerializable bucket) {
        if (this.buckets == null) this.buckets = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = buckets.containsKey(registryName) ? buckets.get(registryName) : new ArrayList<>();
        list.add(bucket);
        this.buckets.put(registryName, list);
        return self();
    }

    public BlockAndItems fluid(String registryName, BlockAndItemSerializable fluid) {
        if (this.fluids == null) this.fluids = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = fluids.containsKey(registryName) ? fluids.get(registryName) : new ArrayList<>();
        list.add(fluid);
        this.fluids.put(registryName, list);
        return self();
    }

    public BlockAndItems item(String registryName, BlockAndItemSerializable item) {
        if (this.items == null) this.items = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = items.containsKey(registryName) ? items.get(registryName) : new ArrayList<>();
        list.add(item);
        items.put(registryName, list);
        return self();
    }
    public BlockAndItems armor(String registryName, BlockAndItemSerializable armor) {
        if (this.armors == null) this.armors = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = armors.containsKey(registryName) ? armors.get(registryName) : new ArrayList<>();
        list.add(armor);
        armors.put(registryName, list);
        return self();
    }
    public BlockAndItems tool(String registryName, BlockAndItemSerializable tool) {
        if (this.tools == null) this.tools = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = tools.containsKey(registryName) ? tools.get(registryName) : new ArrayList<>();
        list.add(tool);
        tools.put(registryName, list);
        return self();
    }
    public BlockAndItems block(String registryName, BlockAndItemSerializable block) {
        if (this.blocks == null) this.blocks = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = blocks.containsKey(registryName) ? blocks.get(registryName) : new ArrayList<>();
        list.add(block);
        blocks.put(registryName, list);
        return self();
    }

    public BlockAndItems food(String registryName, BlockAndItemSerializable food) {
        if (this.foods == null) foods = new ConcurrentHashMap<>();
        ArrayList<BlockAndItemSerializable> list = foods.containsKey(registryName) ? foods.get(registryName) : new ArrayList<>();
        list.add(food);
        foods.put(registryName, list);
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
