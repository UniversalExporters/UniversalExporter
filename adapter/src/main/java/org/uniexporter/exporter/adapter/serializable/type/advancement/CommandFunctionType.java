package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.faces.Self;

public class CommandFunctionType implements Self<CommandFunctionType> {
    public String id;

    public CommandFunctionType id(String id) {
        this.id = id;
        return self();
    }
}
