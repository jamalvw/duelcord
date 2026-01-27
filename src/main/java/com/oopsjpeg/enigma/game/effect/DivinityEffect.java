package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.event.ShieldEvent;
import com.oopsjpeg.enigma.game.object.Effect;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class DivinityEffect extends Effect {
    private final float spRatio;

    public DivinityEffect(GameMember owner, float power) {
        this(owner, power, 0);
    }

    public DivinityEffect(GameMember owner, float power, float spRatio) {
        super(owner, "Divinity", power, null);
        this.spRatio = spRatio;
    }

    public float getTotalPower(float sp) {
        return getPower() + (sp * spRatio);
    }

    @Override
    public String onDefend(GameMember member) {
        ShieldEvent e = new ShieldEvent(member, getTotalPower(member.getStats().get(SKILL_POWER)));
        return EventDispatcher.dispatch(e);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Divinity: " + Math.round(getTotalPower(member.getStats().get(SKILL_POWER)));
    }

    @Override
    public String getDescription() {
        return "Defending shields for **" + getPower() + "** " + (spRatio > 0 ? "+ __" + percent(spRatio) + " Skill Power__" : "") + ".";
    }
}
