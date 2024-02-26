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
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;

import java.util.Collections;
import java.util.List;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;


public class ItemAndBlockUtils {
    public static void defaultItemProperties(ItemStack stack,
                                             BlockAndItemSerializable blockAndItem,
                                             ItemGroup group, CommandContext<ServerCommandSource> ctx, boolean advancement) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        blockAndItem.type = itemType(itemType -> {
            if (advancement) {
                List<Text> tooltip = stack.getTooltip(player, TooltipContext.BASIC);
                for (Text text : tooltip) {
                    NameType basicTooltip = new NameType();
                    basicTooltip.englishName = get(text, true);
                    basicTooltip.name = get(text, false);
                    itemType.basicTooltip(basicTooltip);
                }
                List<Text> tooltip1 = stack.getTooltip(player, TooltipContext.ADVANCED);
                for (Text text : tooltip1) {
                    NameType advanceToolTip = new NameType();
                    advanceToolTip.englishName = get(text, true);
                    advanceToolTip.name = get(text, false);
                    itemType.basicTooltip(advanceToolTip);
                }
            }
            itemType.maxStackSize = stack.getItem().getMaxCount();
            itemType.maxDurability = stack.getItem().getMaxDamage();
            stack.streamTags().forEach(itemTagKey -> {
                itemType.OredictList(itemTagKey.id().toString());
            });
            if (group != null) {
                Text displayName = group.getDisplayName();
                itemType.tab = new NameType();
                itemType.tab.englishName = get(displayName, true);
                itemType.tab.name = get(displayName, false);
            }
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) {
                itemType.nbt = new NbtType();
                nbt(nbt, itemType.nbt);
            }

        });
    }

    public static void setItemName(ItemStack stack, BlockAndItemSerializable blockAndItem) {
        String translationKey = stack.getTranslationKey();
        blockAndItem.englishName(SimpleLanguage.en_us.getOrNull(translationKey));
        blockAndItem.name(Language.getInstance().get(translationKey, null));
    }

    public static String get(Text text, boolean isEnUs) {
        TextContent content = text.getContent();
        if (content instanceof TranslatableTextContent key) {
            if (isEnUs) {
                return SimpleLanguage.en_us.getOrNull(key.getKey());
            } else {
                return Language.getInstance().get(key.getKey(), null);

            }
        } else if (content instanceof LiteralTextContent ctx) {
            return ctx.string();
        } else if (text instanceof MutableText mutableText) {
            StringBuilder sb = new StringBuilder();
            for (Text sibling : mutableText.getSiblings()) {
                sb.append(get(sibling, isEnUs));
            }
            return sb.toString();
        }
        return null;
    }

    public static void nbt(NbtCompound nbtCompound, NbtType nbt) {
        for (String key : nbtCompound.getKeys()) {
            NbtElement nbtElement = nbtCompound.get(key);
            if (nbtElement instanceof NbtCompound compound) {
                NbtType type = new NbtType();
                nbt.entry(key, type);
                nbt(compound, type);
            }
            else if (nbtElement instanceof NbtByte nbtByte) {
                nbt.entry(key, nbtByte.byteValue());
            }
            else if (nbtElement instanceof NbtByteArray nbtByte) {
                nbt.entry(key, Collections.singletonList(nbtByte.getByteArray()));
            } else if (nbtElement instanceof NbtDouble nbtDouble) {
                nbt.entry(key, nbtDouble.doubleValue());
            } else if (nbtElement instanceof NbtFloat nbtFloat) {
                nbt.entry(key, nbtFloat.floatValue());
            } else if (nbtElement instanceof NbtInt nbtInt) {
                nbt.entry(key, nbtInt);
            } else if (nbtElement instanceof NbtIntArray intArray) {
                nbt.entry(key, Collections.singletonList(intArray));
            } else if (nbtElement instanceof NbtList list) {
                nbt.entry(key, list.toString());
            } else if (nbtElement instanceof NbtLong nbtLong) {
                nbt.entry(key, nbtLong.longValue());
            } else if (nbtElement instanceof NbtLongArray longArray) {
                nbt.entry(key, Collections.singletonList(longArray.getLongArray()));
            } else if (nbtElement instanceof NbtShort nbtShort) {
                nbt.entry(key, nbtShort.shortValue());
            } else if (nbtElement instanceof NbtString string) {
                nbt.entry(key, string.asString());
            }
        }
    }


}
