package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class SpawnType {

    @SerializedName("primaryColor")
    public int primaryColor;
    @SerializedName("secondaryColor")
    public int secondaryColor;

    @SerializedName("type")
    public String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpawnType spawnType = (SpawnType) o;
        return primaryColor == spawnType.primaryColor && secondaryColor == spawnType.secondaryColor && Objects.equals(type, spawnType.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryColor, secondaryColor, type);
    }
}
