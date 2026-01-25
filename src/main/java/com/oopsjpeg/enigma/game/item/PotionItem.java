package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.EventManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.HealEvent;
import com.oopsjpeg.enigma.game.buff.PotionBuff;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.util.Emote;

public class PotionItem extends Item {
    private static final int COST = 50;
    private static final int HEAL = 160;
    private static final int TURNS = 2;

    public PotionItem(GameMember owner) {
        super(owner, COST, "Potion");
    }

    @Override
    public String getDescription() {
        return "Heal for __" + HEAL + "__ over **" + TURNS + "** turns.";
    }

    @Override
    public String getTip() {
        return "Heal for __" + HEAL + "__";
    }

    @Override
    public String onUse(GameMember member) {
        member.addBuff(new PotionBuff(member, member, TURNS, HEAL), Emote.HEAL);
        int heal = HEAL / TURNS;
        HealEvent e = new HealEvent(member, heal);
        e.setSource("Potion");
        return EventManager.process(e);
    }

    @Override
    public boolean canUse(GameMember member) {
        return !member.hasBuff(PotionBuff.class);
    }

    @Override
    public boolean removeOnUse() {
        return true;
    }

    @Override
    public boolean isBuyable() {
        return false;
    }
}
