package org.universal.exporter.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Language;
import org.uniexporter.exporter.adapter.serializable.type.NameType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public class LanguageHelper extends Language {


    final Map<String, String> map;
    static LanguageHelper en_us, zh_cn;
    protected LanguageHelper(String language) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        load(biConsumer, "/assets/minecraft/lang/" + language + ".json");
        map = builder.build();
    }

    public static LanguageHelper en_us() {
        if (en_us == null) en_us = new LanguageHelper("en_us");
        return en_us;
    }
    public static LanguageHelper zh_cn() {
        if (zh_cn == null) zh_cn = new LanguageHelper("zh_cn");
        return zh_cn;
    }

    public static void get(NameType type, String translationKey) {
        if (en_us().hasTranslation(translationKey))
            type.englishName = en_us().get(translationKey);
        if (zh_cn().hasTranslation(translationKey))
            type.name = zh_cn().get(translationKey);
    }


    @Override
    public String get(String key, String fallback) {
        return map.getOrDefault(key, fallback);
    }

    @Override
    public boolean hasTranslation(String key) {
        return  map.containsKey(key);
    }

    @Override
    public boolean isRightToLeft() {
        return false;
    }

    @Override
    public OrderedText reorder(StringVisitable text) {
        return (visitor) -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
    }
}
