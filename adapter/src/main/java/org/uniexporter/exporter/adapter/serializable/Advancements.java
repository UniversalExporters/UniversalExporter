package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.faces.Save;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.concurrent.ConcurrentHashMap;

public class Advancements implements Self<Advancements>, Save {
    public ConcurrentHashMap<String, AdvancementSerializable> advancements;

    @SuppressWarnings("UnusedReturnValue")
    public Advancements advancement(String registerName , AdvancementSerializable advancement) {
        if (this.advancements == null) this.advancements = new ConcurrentHashMap<>();
        advancements.put(registerName ,advancement);
        return self();
    }
}
