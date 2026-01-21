package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Effect;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class KorasWillEffect extends Effect
{
    private final float spRatio;

    public KorasWillEffect(GameMember owner, float power)
    {
        this(owner, power, 0);
    }

    public KorasWillEffect(GameMember owner, float power, float spRatio)
    {
        super(owner, "Kora's Will", power, null);
        this.spRatio = spRatio;
    }

    private float getTotalPower(float sp)
    {
        return getPower() + (sp * spRatio);
    }

    @Override
    public String getDescription()
    {
        return "Skills do **" + Math.round(getPower()) + "** " + (spRatio > 0 ? "+ __" + percent(spRatio) + " Skill Power__ " : "") + "more damage.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Kora's Will: " + Math.round(getTotalPower(member.getStats().get(SKILL_POWER)));
    }
}
