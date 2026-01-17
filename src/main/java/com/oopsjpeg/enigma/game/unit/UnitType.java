package com.oopsjpeg.enigma.game.unit;

import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;

public enum UnitType {
    ASSASSIN("Assassin"),
    GUNSLINGER("Gunslinger");

    public static UnitType fromName(String query)
    {
        for (UnitType unit : values())
        {
            String name = unit.getName().toLowerCase();

            if (query.equals(name) || (query.length() >= 3 && name.startsWith(query)))
                return unit;
        }
        return null;
    }

    private final String name;

    UnitType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Unit create() {
        switch(this) {
            case ASSASSIN: return new AssassinUnit();
            case GUNSLINGER: return new GunslingerUnit();
            default: throw new IllegalArgumentException();
        }
    }
}
