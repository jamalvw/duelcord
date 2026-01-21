package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.StatType.COOLDOWN_REDUCTION;
import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.game.StatType.MAX_ENERGY;
import static com.oopsjpeg.enigma.util.Util.percent;

public class AlchemistsElixirItem extends Item {
    public static final int COST = 200;

    public AlchemistsElixirItem(GameMember owner) {
        super(owner, COST, "Alchemist's Elixir");
    }

    @Override
    public String getDescription()
    {
        return "Grants a random effect.";
    }

    @Override
    public String getTip()
    {
        return "Get a random effect";
    }

    @Override
    public String onUse(GameMember member)
    {
        int rand = Util.RANDOM.nextInt(3);

        switch (rand) {
            case 0: // Attack Power and Resist elixir
                return member.addBuff(new ElixirOfMightBuff(member, member, 25, 0.2f), Emote.POTION);
            case 1: // Skill Power and Dodge elixir
                return member.addBuff(new ElixirOfWillBuff(member, member, 30, 0.35f), Emote.POTION);
            case 2:
                return member.addBuff(new ElixirOfHasteBuff(member, member, 25, 1), Emote.POTION);
            default:
                return "The elixir did nothing..!";
        }
    }

    @Override
    public boolean canUse(GameMember member)
    {
        return true;
    }

    @Override
    public boolean removeOnUse()
    {
        return true;
    }

    @Override
    public boolean isBuyable()
    {
        return false;
    }

    static class ElixirOfMightBuff extends Buff {
        private final int attackPower;
        private final float resist;

        public ElixirOfMightBuff(GameMember owner, GameMember source, int attackPower, float resist)
        {
            super(owner, source, "Elixir of Might", false, 2, true, attackPower + resist);
            this.attackPower = attackPower;
            this.resist = resist;
        }

        @Override
        public String onTurnStart(GameMember member)
        {
            return Emote.POTION + "**" + member.getUsername() + "** has __" + attackPower + " bonus Attack Power__ and __" + percent(resist) + " bonus Resist__.";
        }

        @Override
        public String getStatus(GameMember member)
        {
            return "Elixir of Might: " + attackPower + " bonus AP, " + percent(resist) + " bonus Resist";
        }

        @Override
        public Stats getStats()
        {
            return new Stats()
                    .put(ATTACK_POWER, attackPower)
                    .put(RESIST, resist);
        }
    }

    static class ElixirOfWillBuff extends Buff {
        private final int skillPower;
        private final float dodge;

        public ElixirOfWillBuff(GameMember owner, GameMember source, int skillPower, float dodge)
        {
            super(owner, source, "Elixir of Will", false, 2, true, skillPower + dodge);
            this.skillPower = skillPower;
            this.dodge = dodge;
        }

        @Override
        public String onTurnStart(GameMember member)
        {
            return Emote.POTION + "**" + member.getUsername() + "** has __" + skillPower + " bonus Skill Power__ and __" + percent(dodge) + " bonus Dodge__.";
        }

        @Override
        public String getStatus(GameMember member)
        {
            return "Elixir of Will: " + skillPower + " bonus SP, " + percent(dodge) + " bonus Dodge";
        }

        @Override
        public Stats getStats()
        {
            return new Stats()
                    .put(SKILL_POWER, skillPower)
                    .put(DODGE, dodge);
        }
    }

    static class ElixirOfHasteBuff extends Buff {
        private final int maxEnergy;
        private final int cdReduction;

        public ElixirOfHasteBuff(GameMember owner, GameMember source, int maxEnergy, int cdReduction)
        {
            super(owner, source, "Elixir of Haste", false, 2, true, maxEnergy + cdReduction);
            this.maxEnergy = maxEnergy;
            this.cdReduction = cdReduction;
        }

        @Override
        public String onTurnStart(GameMember member)
        {
            return Emote.POTION + "**" + member.getUsername() + "** has __" + maxEnergy + " bonus Energy__ and their Skills recharge __" + cdReduction + "__ turns faster.";
        }

        @Override
        public String getStatus(GameMember member)
        {
            return "Elixir of Haste: " + maxEnergy + " bonus Energy, " + cdReduction + " CDR";
        }

        @Override
        public Stats getStats()
        {
            return new Stats()
                    .put(MAX_ENERGY, maxEnergy)
                    .put(COOLDOWN_REDUCTION, cdReduction);
        }
    }
}
