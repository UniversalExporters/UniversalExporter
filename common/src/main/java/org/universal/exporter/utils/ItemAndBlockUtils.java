package org.universal.exporter.utils;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;


public class ItemAndBlockUtils {

    public static void setItemName(ItemStack stack, BlockAndItemSerializable blockAndItem) {
        String translationKey = stack.getTranslationKey();
        blockAndItem.translateKey(translationKey);
        blockAndItem.englishName(SimpleLanguage.en_us.getOrNull(translationKey));
        blockAndItem.name(Language.getInstance().get(translationKey, null));
    }

    public static String get(Text text, boolean isEnUs, NameType type) {
        TextContent content = text.getContent();
        if (content instanceof TranslatableTextContent key) {
            return getTranslate(isEnUs, type, key);
        } else if (content instanceof LiteralTextContent ctx) {
            return ctx.string();
        } else if (text instanceof MutableText mutableText) {
            return appendTextSet(isEnUs, type, mutableText);
        }
        return null;
    }

    @NotNull
    private static String appendTextSet(boolean isEnUs, NameType type, MutableText mutableText) {
        StringBuilder sb = new StringBuilder();
        for (Text sibling : mutableText.getSiblings()) {
            sb.append(get(sibling, isEnUs, type));
        }
        return sb.toString();
    }

    private static String getTranslate(boolean isEnUs, NameType type, TranslatableTextContent key) {
        type.translateKey(key.getKey());
        if (isEnUs) {
            return SimpleLanguage.en_us.getOrNull(key.getKey());
        } else {
            return Language.getInstance().get(key.getKey(), null);

        }
    }

    public static void nbt(NbtCompound nbtCompound, NbtType nbt) {
        for (String key : nbtCompound.getKeys()) {
            NbtElement nbtElement = nbtCompound.get(key);
            if (nbtElement instanceof NbtCompound compound) {
                NbtType nbtType = compoundSet(compound);
                nbt.entry(key, nbtType);
            }
            else if (nbtElement instanceof AbstractNbtNumber number) {
                nbt.entry(key, number.numberValue());
            } else if (nbtElement instanceof AbstractNbtList<?> list) {
                nbtArraySet(list);
            }
            else if (nbtElement instanceof NbtString string) {
                nbt.entry(key, string.asString());
            }
        }
    }

    private static ArrayList<?> nbtArraySet(AbstractNbtList<?> list) {
        ArrayList<Number> listn = new ArrayList<>();
        ArrayList<Object> listo = new ArrayList<>();
        findList(list, listn, listo);
        if (!listo.isEmpty()) {
            listo.addAll(listn);
            return listo;
        }
        return listn;
    }

    private static void findList(AbstractNbtList<?> list, ArrayList<Number> listn, ArrayList<Object> listo) {
        list.forEach(element -> {
            if (element instanceof AbstractNbtNumber number) {
                listn.add(number.numberValue());
            } else if (element instanceof AbstractNbtList<?> list1) {
                listo.add(nbtArraySet(list1));
            } else if (element instanceof NbtCompound compound) {
                listo.add(compoundSet(compound));
            }
        });
    }

    private static NbtType compoundSet(NbtCompound compound) {
        NbtType type = new NbtType();
        nbt(compound, type);
        return type;
    }


}
