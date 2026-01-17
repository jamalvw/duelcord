package com.oopsjpeg.enigma.game.unit;

import com.oopsjpeg.enigma.game.GameObject;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public interface Unit extends GameObject {
    public String getName();
    public Color getColor();
    public String getDescription();
    public Stats getStats();
    public Skill[] getSkills();

    default EmbedCreateSpec embed()
    {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();

        embed.color(getColor());
        embed.description("## " + getName() + "\n" + getDescription() + "\n\u1CBC\u1CBC");

        for (Skill skill : getSkills())
            embed.addField("`>" + skill.getName() + "` - Cooldown: **" +
                    (skill.hasCooldown() ? skill.getCooldown().getDuration() : "None") +
                    "** - Energy Cost: **" +
                    (skill.hasCost() ? skill.getCost() : "Free") +
                    "**", skill.getDescription(), false);

        return embed.build();
    }
}
