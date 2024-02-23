package org.uniexporter.exporter.adapter.utils;

import org.uniexporter.exporter.adapter.serializable.type.NameType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("rawtypes")
public interface ILanguageHelper<T> {
    AtomicReference<ILanguageHelper<?>> en_us = new AtomicReference<>();
    AtomicReference<ILanguageHelper<?>> zh_cn = new AtomicReference<>();

    Map<String, String> map();
    default void get(NameType type, String translationKey) {
        ILanguageHelper en = en_us.get();
        if (equals(en) && hasTranslation(translationKey))
            type.englishName = get(translationKey);
        ILanguageHelper zh = zh_cn.get();
        if (equals(zh)  && hasTranslation(translationKey))
            type.name = get(translationKey);
    }
    boolean hasTranslation(String key);
    String get(String key);
    String get(T ctx);

}
