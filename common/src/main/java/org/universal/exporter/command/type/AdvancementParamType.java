package org.universal.exporter.command.type;

import net.minecraft.util.StringIdentifiable;

public enum AdvancementParamType implements StringIdentifiable {
    basic_tooltip,
    advance_tooltip,
    status_effects_food,
    has_sided_transparency_block,
    block_level_4,
    block_level_3,
    block_level_2,
    block_level_1,
    ;

    @Override
    public String asString() {
        return name();
    }
}
