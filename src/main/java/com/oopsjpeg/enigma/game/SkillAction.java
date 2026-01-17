package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.util.Cooldown;

public abstract class SkillAction implements GameAction {
    public final Skill skill;

    public SkillAction(Skill skill)
    {
        this.skill = skill;
    }

    @Override
    public abstract String act(GameMember actor);

    public Cooldown getCooldown()
    {
        return skill.getCooldown();
    }

    @Override
    public int getEnergy() {
        return skill.getCost();
    }
}
