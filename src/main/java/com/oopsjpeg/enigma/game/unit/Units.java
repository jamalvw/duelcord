package com.oopsjpeg.enigma.game.unit;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;
import com.oopsjpeg.enigma.game.unit.duelist.DuelistUnit;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.game.unit.hacker.HackerUnit;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;
import com.oopsjpeg.enigma.game.unit.shifter.ShifterUnit;

public enum Units {
    ASSASSIN("Assassin"),
    GUNSLINGER("Gunslinger"),
    REAVER("Reaver"),
    DUELIST("Duelist"),
    HACKER("Hacker"),
    SHIFTER("Shifter");

    public static Units fromName(String query)
    {
        for (Units unit : values())
        {
            String name = unit.getName().toLowerCase();

            if (query.equals(name) || (query.length() >= 3 && name.startsWith(query)))
                return unit;
        }
        return null;
    }

    private final String name;

    Units(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Unit create(GameMember member) {
        switch(this) {
            case ASSASSIN: return new AssassinUnit(member);
            case GUNSLINGER: return new GunslingerUnit(member);
            case REAVER: return new ReaverUnit(member);
            case DUELIST: return new DuelistUnit(member);
            case HACKER: return new HackerUnit(member);
            case SHIFTER: return new ShifterUnit(member);
            default: throw new IllegalArgumentException();
        }
    }
}
