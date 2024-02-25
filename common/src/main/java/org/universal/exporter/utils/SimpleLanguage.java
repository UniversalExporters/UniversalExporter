package org.universal.exporter.utils;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;
import org.universal.exporter.command.type.ModidType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.uniexporter.exporter.adapter.faces.Save.gson;

public class SimpleLanguage {
    final Map<String, String> language = new HashMap<>();
    public static SimpleLanguage en_us = new SimpleLanguage("en_us");
    public SimpleLanguage(String languageKind) {

        for (ModidType value : ModidType.values()) {
            String path = "/assets/" + value.asString() + "/lang/" + languageKind + ".json";

            InputStream resourceAsStream = net.minecraft.util.Language.class.getResourceAsStream(path);
            InputStreamReader json;
            if (resourceAsStream != null) {
                json = new InputStreamReader(resourceAsStream);
                Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
                language.putAll(map);
                try {
                    json.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public String getOrNull(String key) {
        return language.getOrDefault(key, null);
    }

    public String get(String key) {
        return getOrDefault(key);
    }


    public String getOrDefault(String key) {
        return language.getOrDefault(key, key);
    }
}
