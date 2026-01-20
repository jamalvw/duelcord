package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill.DAMAGE;
import static com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill.DAMAGE_SP_RATIO;
import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

public class InfiniteBuff extends Buff {
    private final float chance;

    public InfiniteBuff(GameMember owner, GameMember source, int totalTurns, float power, float chance) {
        super(owner, source, "Infinite", false, totalTurns, power);
        this.chance = chance;
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
                        if (e.getAttacker() != getOwner()) return;
                        if (e.isDoT()) return;

                        float damage = DAMAGE + (getOwner().getStats().get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);

                        if (RANDOM.nextFloat() < chance) {
                            e.proposeEffect(() -> {
                                GameMember victim = e.getVictim();
                                if (!victim.hasBuff(VoidburnDebuff.class)) {
                                    VoidburnDebuff burn = new VoidburnDebuff(victim, e.getAttacker(), 3, damage);
                                    e.getOutput().add(victim.addBuff(burn, Emote.VOIDBURN));
                                } else {
                                    VoidburnDebuff burn = (VoidburnDebuff) victim.getBuff(VoidburnDebuff.class);
                                    burn.setPower(burn.getPower() + damage);
                                    burn.reset();
                                    e.getOutput().add(Emote.VOIDBURN + "**Voidburn**'s damage increased to **" + burn.formatPower() + "**!");
                                }
                            });
                        }
                    }
                }
        };
    }

    @Override
    public String getStatus(GameMember member) {
        return "Infinite: " + percent(chance) + " chance to apply Voidburn";
    }
}
