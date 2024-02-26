package org.uniexporter.exporter.adapter.serializable.type;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NameType {
    public ArrayList<String> translateKeys;
    @SerializedName("name")
    public String name;
    @SerializedName("englishName")
    public String englishName;

    public void translateKey(String translateKey) {
        if(this.translateKeys == null) translateKeys = new ArrayList<>();
        if (!translateKeys.contains(translateKey))
            translateKeys.add(translateKey);

    }
}
