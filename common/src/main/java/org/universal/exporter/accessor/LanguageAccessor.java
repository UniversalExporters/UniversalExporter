package org.universal.exporter.accessor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.universal.exporter.command.type.ModidType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static net.minecraft.util.Language.load;

public interface LanguageAccessor {
    AtomicReference<Language> en_us = new AtomicReference<>();
    AtomicReference<Language> zh_cn = new AtomicReference<>();
    static Language en_us() {
        if (en_us.get() == null) en_us.set(setLanguage("en_us"));
        return en_us.get();
    }

    static Language zh_cn() {
        if (zh_cn.get() == null) zh_cn.set(setLanguage("en_us"));
        return zh_cn.get();
    }

    static Language setLanguage(String language) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        for (ModidType value : ModidType.values())
            load(biConsumer, "/assets/" + value.asString() + "/lang/" + language + ".json");

        final Map<String, String> map = builder.build();
        return new Language() {
            public String get(String key, String fallback) {
                return map.getOrDefault(key, fallback);
            }

            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }

            public boolean isRightToLeft() {
                return false;
            }

            public OrderedText reorder(StringVisitable text) {
                return (visitor) -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
            }
        };
    }

    default void get(NameType type, String translationKey) {
        var en = en_us();
        if (equals(en) && en.hasTranslation(translationKey))
            type.englishName = en.get(translationKey);
        var zh = zh_cn();
        if (equals(zh)  && zh.hasTranslation(translationKey))
            type.name = zh.get(translationKey);
    }

    String get(String key);
    String get(String key, String fallback);

    default String get(Text content) {
        StringBuilder sb = new StringBuilder();
        if (content instanceof MutableText mutable) {
            for (Text sibling : mutable.getSiblings()) {
                sb.append(get(sibling));
            }
        } else if (content.getContent() instanceof TranslatableTextContent translatable) {
            sb.append(get(translatable.getKey(), translatable.getFallback()));
        } else if (content.getContent() instanceof LiteralTextContent literalText) {
            sb.append(literalText.string());
        }
        return sb.toString();
    }
}
