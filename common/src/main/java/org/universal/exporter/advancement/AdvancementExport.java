package org.universal.exporter.advancement;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class AdvancementExport {
    public static Collection<Advancement> getAdvancements(MinecraftServer server){
        return server.getAdvancementLoader().getAdvancements();
    }

    @Environment(EnvType.CLIENT)
    public static Collection<Advancement> getClientAdvancements(){
        assert MinecraftClient.getInstance().player != null;
        ClientAdvancementManager clientAdvancements = MinecraftClient.getInstance().player.networkHandler.getAdvancementHandler();
        return clientAdvancements.getManager().getAdvancements();
    }

    public static JsonObject getSingleJson(Advancement advancement){
        // To avoid exception "JsonSyntaxException: Missing trigger"
        Advancement.Builder builder;
        if (advancement.sendsTelemetryEvent()){
            builder = Advancement.Builder.create();
        }
        else {
            builder = Advancement.Builder.createUntelemetered();
        }
        builder.display(advancement.getDisplay());
        builder.rewards(advancement.getRewards());
        builder.requirements(advancement.getRequirements());
        builder.parent(advancement.getParent() == null ? null : advancement.getParent());
        Map<String, AdvancementCriterion> criteria = advancement.getCriteria();
        for (String str : criteria.keySet()){
            if (criteria.get(str).getConditions() == null){
                builder.criterion(str, new AdvancementCriterion(new ImpossibleCriterion.Conditions()));
            }
            else {
                builder.criterion(str, criteria.get(str));
            }
        }
        return builder.toJson();
    }

    public static JsonObject getAllJson(Collection<Advancement> advancements, @Nullable String namespace){
        // If "namespace" is null, all advancements will be included.
        JsonObject result = new JsonObject();
        for (Advancement adv : advancements){
            if (namespace != null && !adv.getId().getNamespace().equals(namespace)) continue;
            result.add(adv.getId().toString(), getSingleJson(adv));
        }
        return result;
    }

    public static void export(){
        // TODO
    }
}
