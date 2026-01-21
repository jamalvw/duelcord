package com.oopsjpeg.enigma.game.unit.gambler;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gambler.skill.BetSkill;
import discord4j.rest.util.Color;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.StatType.HEALTH_PER_TURN;

// Work in progress
public class GamblerUnit extends Unit {
    private final GameMember owner;

    private final BetSkill bet = new BetSkill(this);

    public GamblerUnit(GameMember owner) {
        this.owner = owner;
    }

    @Override
    public String getName() {
        return "Gambler";
    }

    @Override
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

    @Override
    public String getDescription() {
        return "Crits grant 10 - 20 extra gold, but deal 10% less damage.";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 150)
                .put(MAX_HEALTH, 1377)
                .put(ATTACK_POWER, 7)
                .put(HEALTH_PER_TURN, 17)
                .put(CRIT_DAMAGE, -0.1f);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{bet};
    }
}
