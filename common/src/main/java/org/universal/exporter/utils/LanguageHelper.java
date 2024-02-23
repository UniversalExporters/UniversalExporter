package org.universal.exporter.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.utils.ILanguageHelper;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class LanguageHelper extends Language implements ILanguageHelper<TextContent> {


    final Map<String, String> map;
    protected LanguageHelper(String language) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        load(biConsumer, "/assets/minecraft/lang/" + language + ".json");
        map = builder.build();
    }


    public static ILanguageHelper<TextContent> en_us() {
        ILanguageHelper<?> en = en_us.get();
        if (en == null) en_us.set(new LanguageHelper("en_us"));
        return (ILanguageHelper<TextContent>) en_us.get();
    }

    public static ILanguageHelper<TextContent> zh_cn() {
        ILanguageHelper<?> zh = zh_cn.get();
        if (zh == null) zh_cn.set(new LanguageHelper("zh_cn"));
        return (ILanguageHelper<TextContent>) zh_cn.get();
    }

    @Override
    public String get(String key, String fallback) {
        return map.getOrDefault(key, fallback);
    }

    @Override
    public Map<String, String> map() {
        return map;
    }

    @Override
    public boolean hasTranslation(String key) {
        return  map().containsKey(key);
    }

    @Override
    public String get(TextContent ctx) {
        return null;
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
