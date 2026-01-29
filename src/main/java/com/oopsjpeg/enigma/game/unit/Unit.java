package com.oopsjpeg.enigma.game.unit;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.UnitForm;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.EnumSet;

public abstract class Unit extends GameObject {
    public abstract String getName();

    public abstract GameMember getOwner();

    public abstract Color getColor();

    public abstract String getDescription();

    public abstract String getSimpleDescription();

    public abstract Stats getStats();

    public abstract Skill[] getSkills();

    public UnitForm getForm() {
        return null;
    }

    public UnitForm[] getForms() {
        return null;
    }

    public boolean hasForms() {
        return getForms() != null && getForms().length > 0;
    }

    public abstract EnumSet<Items> getRecommendedBuild();

    public EmbedCreateSpec embed() {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();

        embed.color(getColor());
        embed.description("## " + getName() + "\n" + getSimpleDescription() + "\n\u1CBC\u1CBC");

        for (Skill skill : getSkills())
            embed.addField(skillField(skill));

        if (hasForms()) {
            UnitForm form = getForms()[0];
            for (Skill skill : form.getSkills())
                embed.addField(skillField(skill));
        }

        return embed.build();
    }

    private EmbedCreateFields.Field skillField(Skill skill) {
        return EmbedCreateFields.Field.of("`>" + skill.getName() + "` - Cooldown: **" +
                (skill.hasCooldown() ? skill.getCooldown().getDuration() : "None") +
                "** - Energy Cost: **" +
                (skill.hasCost(getOwner()) ? skill.getCost(getOwner()) : "Free") +
                "**", skill.getSimpleDescription(), false);
    }
}
