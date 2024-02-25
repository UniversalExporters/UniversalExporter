package org.uniexporter.exporter.adapter.serializable;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.faces.Self;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;

import java.io.Serializable;
import java.util.Objects;
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

    //计算处理完成后实行多线程(部分)
    public void init(Runnable runnable) {
        runnable.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockAndItemSerializable that = (BlockAndItemSerializable) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
