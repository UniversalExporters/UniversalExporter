package org.uniexporter.exporter.adapter.serializable.type;

import org.uniexporter.exporter.adapter.faces.Self;

public class IconType implements Self<IconType> {
    public String smallIcon;
    public String largeIcon;

    public IconType largeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
        return self();
    }

    public IconType smallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
        return self();
    }
}
