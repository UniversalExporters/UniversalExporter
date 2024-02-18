package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;
import org.uniexporter.exporter.adapter.faces.Self;
import org.uniexporter.exporter.adapter.serializable.type.IconType;

public class AdvancementDisplayType implements Self<AdvancementDisplayType> {
    public String title;
    public String englishTitle;
    public String description;
    public String englishDescription;
    public IconType icon;// this has nbt

    @AdvancementParameters(used = true)
    public String background; // advancement parameters
    public String frame;// enum
    public boolean showToast;
    public boolean announceToChat;
    public boolean hidden;
    public float x;
    public float y;

    public AdvancementDisplayType title(String title) {
        this.title = title;
        return self();
    }
    public AdvancementDisplayType description(String description) {
        this.description = description;
        return self();
    }
    public AdvancementDisplayType icon(IconType icon) {
        this.icon = icon;
        return self();
    }
    public AdvancementDisplayType background(String background, boolean isUsed) {
        return advancementParameters("background", background, isUsed);
    }
    public AdvancementDisplayType frame(String frame) {
        this.frame = frame;
        return self();
    }
    public AdvancementDisplayType showToast(boolean showToast) {
        this.showToast = showToast;
        return self();
    }
    public AdvancementDisplayType announceToChat(boolean announceToChat) {
        this.announceToChat = announceToChat;
        return self();
    }
    public AdvancementDisplayType hidden(boolean hidden) {
        this.hidden = hidden;
        return self();
    }
    public AdvancementDisplayType x(float x) {
        this.x = x;
        return self();
    }
    public AdvancementDisplayType y(float y) {
        this.y = y;
        return self();
    }

    public AdvancementDisplayType englishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
        return self();
    }

    public AdvancementDisplayType englishDescription(String englishDescription) {
        this.englishDescription = englishDescription;
        return self();
    }
}
