package org.universal.exporter.mixin;

import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.universal.exporter.accessor.LanguageAccessor;

@Mixin(Language.class)
public abstract class MixinLanguage implements LanguageAccessor {
    @Shadow public abstract String get(String key);

    @Shadow public abstract String get(String key, String fallback);

}
