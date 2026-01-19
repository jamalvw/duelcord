package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;

public interface GameObject
{
    default DamageHook[] getDamageHooks() {
        return new DamageHook[0];
    }

    default String getStatus(GameMember member)
    {
        return null;
    }

    default String onTurnStart(GameMember member)
    {
        return null;
    }

    default String onTurnEnd(GameMember member)
    {
        return null;
    }

    default String onDefend(GameMember member)
    {
        return null;
    }

    default String onSkillUsed(GameMember member)
    {
        return null;
    }

    default float onHeal(float healAmount)
    {
        return healAmount;
    }

    default float onShield(float shieldAmount)
    {
        return shieldAmount;
    }
}
