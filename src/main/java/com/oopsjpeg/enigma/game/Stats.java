package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.util.Util;

import java.util.HashMap;
import java.util.Map;

import static com.oopsjpeg.enigma.game.StatType.*;

public class Stats
{
    private final Map<StatType, Float> values = new HashMap<>();

    public Stats()
    {
        values.put(MAX_HEALTH, 0.0f);
        values.put(MAX_ENERGY, 0.0f);
        values.put(ATTACK_POWER, 0.0f);
        values.put(ATTACK_COST, 0.0f);
        values.put(SKILL_POWER, 0.0f);
        values.put(CRIT_CHANCE, 0.0f);
        values.put(CRIT_DAMAGE, 0.0f);
        values.put(LIFE_STEAL, 0.0f);
        values.put(RESIST, 0.0f);
        values.put(SKILL_RESIST, 0.0f);
        values.put(BLOCK_CHANCE, 0.0f);
        values.put(DODGE, 0.0f);
        values.put(COOLDOWN_REDUCTION, 0.0f);

        values.put(GOLD_PER_TURN, 0.0f);
        values.put(HEALTH_PER_TURN, 0.0f);
    }

    public Stats put(StatType type, float value)
    {
        switch (type)
        {
            case MAX_HEALTH:
            case MAX_ENERGY:
            case ATTACK_POWER:
            case SKILL_POWER:
            case GOLD_PER_TURN:
            case HEALTH_PER_TURN:
                value = Math.max(0, value);
                break;
            case CRIT_CHANCE:
            case SKILL_RESIST:
            case BLOCK_CHANCE:
                value = Util.limit(value, 0, 1);
                break;
            case RESIST:
                value = Util.limit(value, 0, 0.8f);
                break;
        }
        values.put(type, value);
        return this;
    }

    public float get(StatType type)
    {
        return values.getOrDefault(type, 0.0f);
    }

    public int getInt(StatType type)
    {
        return (int) Math.ceil(get(type));
    }

    public Stats add(StatType type, float value)
    {
        put(type, get(type) + value);
        return this;
    }

    public Stats sub(StatType type, float value)
    {
        put(type, get(type) - value);
        return this;
    }

    public Stats mul(StatType type, float value)
    {
        put(type, get(type) * value);
        return this;
    }

    public Stats div(StatType type, float value)
    {
        put(type, get(type) / value);
        return this;
    }

    public Stats putAll(Stats other)
    {
        values.clear();
        other.values.keySet().forEach(k -> put(k, other.values.get(k)));
        return this;
    }

    public Stats addAll(Stats other)
    {
        other.values.keySet().forEach(k -> put(k, values.get(k) + other.values.get(k)));
        return this;
    }

    @Override
    public String toString()
    {
        return values.toString();
    }
}