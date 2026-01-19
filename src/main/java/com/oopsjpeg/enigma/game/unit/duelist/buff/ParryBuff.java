package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class ParryBuff extends Buff {
    private final float blockChance;

    public ParryBuff(GameMember owner, GameMember source, int totalTurns, float skillResist, float blockChance) {
        super(owner, source, "Parry", false, totalTurns, skillResist);
        this.blockChance = blockChance;
    }

    public float getBlockChance() {
        return blockChance;
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.POST_DAMAGE;
                    }

                    @Override
                    public void execute(DamageEvent e) {
                        if (e.getVictim() != getOwner()) return;
                        if (!e.isBlocked()) return;

                        e.proposeEffect(() -> {
                            EnGardeBuff buff = new EnGardeBuff(getOwner(), e.getAttacker(), 1, 25);
                            e.getOutput().add(getOwner().addBuff(buff, Emote.BUFF));
                            remove();
                        });
                    }
                }
        };
    }

    @Override
    public String onTurnStart(GameMember member) {
        remove();
        return "";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Parry: " + percent(blockChance) + " chance to block";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_RESIST, getPower())
                .put(StatType.BLOCK_CHANCE, blockChance);

    }
}
