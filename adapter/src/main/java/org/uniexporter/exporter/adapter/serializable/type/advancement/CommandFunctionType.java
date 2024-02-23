package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.faces.Self;

import java.util.function.Consumer;

public class CommandFunctionType implements Self<CommandFunctionType> {
    public String id;

    public static CommandFunctionType commandFunctionType(Consumer<CommandFunctionType> consumer) {
        CommandFunctionType t = new CommandFunctionType();
        consumer.accept(t);
        return t;
    }

    public CommandFunctionType id(String id) {
        this.id = id;
        return self();
    }
}
