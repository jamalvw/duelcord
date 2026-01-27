package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class ParryBuff extends Buff {
    private final float blockChance;

    public ParryBuff(GameMember owner, GameMember source, int totalTurns, float skillResist, float blockChance) {
        super(owner, source, "Parry", false, totalTurns, false, skillResist);
        this.blockChance = blockChance;

        hook(EventType.DAMAGE_RECEIVED, Priority.POST_DAMAGE, (Consumer<DamageEvent>) e -> {
            if (e.getVictim() != getOwner()) return;
            if (!e.isBlocked()) return;

            e.queueAction(() -> {
                EnGardeBuff buff = new EnGardeBuff(getOwner(), e.getActor(), 1, 25);
                getOwner().addBuff(buff, Emote.BUFF);
                e.getOutput().add(Emote.BUFF + "**" + getOwner().getUsername() + "** parried and will gain __25 bonus energy__ next turn.");
                remove(true);
            });
        });
    }

    public float getBlockChance() {
        return blockChance;
    }

    @Override
    public String getStatus(GameMember member) {
        return "Parry: " + percent(blockChance) + " chance to block and gain 25 bonus energy";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_RESIST, getPower())
                .put(StatType.BLOCK_CHANCE, blockChance);

    }
}
