package org.universal.exporter.utils;

import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;

public class ItemAndBlockHelper {
    private final BlockAndItemSerializable serializable;
    private final Base64Helper base64Helper;

    public ItemAndBlockHelper(BlockAndItemSerializable serializable) {
        this.serializable = serializable;
        this.base64Helper = new Base64Helper(serializable);
    }


}
