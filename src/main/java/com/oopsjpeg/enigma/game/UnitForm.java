package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public abstract class UnitForm extends GameObject {
    private final Unit unit;
    private final String name;
    private final Color color;

    public UnitForm(Unit unit, String name, Color color) {
        this.unit = unit;
        this.name = name;
        this.color = color;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public abstract Skill[] getSkills();

    public EmbedCreateSpec embed() {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();

        embed.color(getColor());
        embed.description("### " + getName());

        for (Skill skill : getSkills())
            embed.addField(skillField(skill));

        return embed.build();
    }

    private EmbedCreateFields.Field skillField(Skill skill) {
        return EmbedCreateFields.Field.of("`>" + skill.getName() + "` - Cooldown: **" +
                (skill.hasCooldown() ? skill.getCooldown().getDuration() : "None") +
                "** - Energy Cost: **" +
                (skill.hasCost(unit.getOwner()) ? skill.getCost(unit.getOwner()) : "Free") +
                "**", skill.getSimpleDescription(), false);
    }
}
