package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.faces.Save;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.ArrayList;

public class Advancements implements Self<Advancements>, Save {
    public ArrayList<AdvancementSerializable> advancements;

    public Advancements advancement(AdvancementSerializable advancement) {
        if (this.advancements == null) this.advancements = new ArrayList<>();
        advancements.add(advancement);
        return self();
    }
}
