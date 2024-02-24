package org.uniexporter.exporter.adapter.serializable;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.faces.Self;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;

import java.util.function.Consumer;

public class BlockAndItemSerializable extends NameType implements Self<BlockAndItemSerializable> {

    @SerializedName("type")
    public ItemType type;

    public static BlockAndItemSerializable blockAndItemSerializable(Consumer<BlockAndItemSerializable> consumer) {
        BlockAndItemSerializable serializable = new BlockAndItemSerializable();
        consumer.accept(serializable);
        return serializable;
    }



    public BlockAndItemSerializable name(String name) {
        this.name = name;
        return self();
    }

    public BlockAndItemSerializable englishName(String englishName) {
        this.englishName = englishName;
        return self();
    }

    public BlockAndItemSerializable type(ItemType type) {
        this.type = type;
        return self();
    }
}
