package org.universal.exporter.utils;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.AdvancementSerializable;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.uniexporter.exporter.adapter.serializable.type.advancement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.uniexporter.exporter.adapter.serializable.AdvancementSerializable.advancement;
import static org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementDisplayType.advancementDisplayType;
import static org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementRewardsType.advancementRewardsType;
import static org.universal.exporter.utils.Base64Helper.icon;
import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;

public class AdvancementHelper extends DefaultHelper<AdvancementHelper> {

    public AdvancementHelper(String registerName, boolean this$advanceParameters) {
        super(registerName, this$advanceParameters);
    }

    @Override
    public AdvancementHelper self() {
        return this;
    }


    public AdvancementHelper parentAdvancementSet(Advancement parent, Advancements advancements) {
        var display = parent.getDisplay();
        AdvancementRewards rewards = parent.getRewards();
        CommandFunction.LazyContainer function = rewards.function;
        Map<String, AdvancementCriterion> criteria = parent.getCriteria();
        String[][] requirements = parent.getRequirements();
        advancements.advancement(registerName, advancement(advancement -> {
            if (display != null)
                advancement.display = advancementDisplayType(advancementDisplay -> displaySet(advancementDisplay, display));
            rewardsSet(advancement, rewards, function);
            advancement.englishName(en_us().get(parent.text.getContent()));
            advancement.name(zh_cn().get(parent.text.getContent()));
            for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
                CriterionConditions conditions = entry.getValue().getConditions();
                if (conditions != null)
                    advancement
                            .criteria(entry.getKey(), new AdvancementCriterionType()
                                    .registerName(conditions.getId().toString()));
            }
            for (String[] requirement : requirements)
                advancement.requirements(requirement);
            advancement.sendsTelemetryEvent(parent.sendsTelemetryEvent());
        }));
        return self();
    }

    private AdvancementHelper rewardsSet(AdvancementSerializable advancement, AdvancementRewards rewards, CommandFunction.LazyContainer function) {
        advancement
                .rewards(advancementRewardsType(advancementRewardsType -> {;
                    advancementRewardsType.experience(rewards.experience);
                }));
        advancement.rewards.loots = Arrays.stream(rewards.loot).map(Identifier::toString).collect(Collectors.toCollection(ArrayList::new));
        advancement.rewards.recipes = Arrays.stream(rewards.getRecipes()).map(Identifier::toString).collect(Collectors.toCollection(ArrayList::new));
        advancement.rewards
                .function(LazyContainerType.lazyContainerType(lazyContainerType -> {
                    lazyContainerType.function(CommandFunctionType.commandFunctionType(commandFunctionType -> {
                        if ( function.getId() != null) commandFunctionType.id( function.getId().toString());
                    }));
                }));
        return self();
    }

    private AdvancementHelper displaySet(AdvancementDisplayType advancementDisplay, AdvancementDisplay display) {
        advancementDisplay.title = zh_cn().get(display.getTitle().getContent());
        advancementDisplay.englishTitle = en_us().get(display.getTitle().getContent());
        advancementDisplay.description = zh_cn().get(display.getDescription().getContent());
        advancementDisplay.englishDescription = en_us().get(display.getDescription().getContent());
        advancementDisplay.icon(icon().itemStackToBase(display.getIcon()));
        if (display.getBackground() != null) {
            advancementDisplay.background(display.getBackground().toString(), this$advanceParameters);
        }
        advancementDisplay.frame = display.getFrame().getId();
        advancementDisplay.showToast = display.shouldShowToast();
        advancementDisplay.announceToChat = display.shouldAnnounceToChat();
        advancementDisplay.hidden = display.isHidden();
        return self();
    }
}
