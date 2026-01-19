package com.oopsjpeg.enigma.game.unit.reaver.creature;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Summon;
import com.oopsjpeg.enigma.util.Emote;

public class ReaverSummon extends Summon {
    private final float damage;

    public ReaverSummon(GameMember owner, float health, float damage) {
        super("Creature", owner, health, false);
        this.damage = damage;
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.PRE_CALCULATION;
                    }

                    @Override
                    public void execute(DamageEvent e) {
                        if (e.getAttacker() != getOwner()) return;
                        if (!e.isAttack()) return;

                        DamageEvent summonStrikeEvent = new DamageEvent(getOwner(), e.getVictim());
                        summonStrikeEvent.setIsSkill(true);
                        summonStrikeEvent.setEmote(Emote.SUMMON);
                        summonStrikeEvent.setSource(getName());
                        summonStrikeEvent.addDamage(damage);

                        e.proposeEffect(() -> e.getOutput().add(DamageManager.process(summonStrikeEvent)));
                    }
                },
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.SUMMONS;
                    }

                    @Override
                    public void execute(DamageEvent event) {
                        if (event.getVictim() != getOwner()) return;

                        takeHealth(event.getDamage());
                        if (getHealth() <= 0) {
                            remove();
                        }
                    }
                }
        };
    }
}
