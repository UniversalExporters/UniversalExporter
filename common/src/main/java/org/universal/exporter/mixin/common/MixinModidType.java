package org.universal.exporter.mixin.common;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.platform.Mod;

import java.util.Arrays;

@Mixin(value = ModidType.class, remap = false)
public class MixinModidType {
    @SuppressWarnings("target")
    @Shadow
    @Final
    @Mutable
    private static ModidType[] $VALUES;

    @Invoker("<init>")
    public static ModidType init(String name, int ordinal) {
        throw new AssertionError();
    }

    static {
        Mod.getModids().forEach(MixinModidType::universal$exporter$create);
    }

    @Unique
    private static void universal$exporter$create(String name) {
        var ordinal = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        $VALUES[ordinal] = init(name, ordinal);
    }
}
