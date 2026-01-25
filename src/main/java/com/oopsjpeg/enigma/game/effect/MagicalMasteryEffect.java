package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Stacker;

import static com.oopsjpeg.enigma.game.StatType.COOLDOWN_REDUCTION;
import static com.oopsjpeg.enigma.game.StatType.MAX_HEALTH;
import static com.oopsjpeg.enigma.util.Util.percent;

public class MagicalMasteryEffect extends Effect {
    private final int cdReduction;
    private final Stacker skillCount;

    public MagicalMasteryEffect(GameMember owner, int cdReduction, int skillLimit, float power) {
        super(owner, "Magical Mastery", power, null);
        this.cdReduction = cdReduction;
        this.skillCount = new Stacker(skillLimit);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.PRE_CALCULATION;
            }

            @Override
            public void execute(DamageEvent event) {
                if (event.getActor() != getOwner()) return;
                if (!event.isSkill()) return;

                event.proposeEffect(() -> {
                    if (skillCount.stack()) {
                        event.addDamage(event.getVictim().getStats().get(MAX_HEALTH) * getPower());
                        skillCount.reset();
                    }
                });
            }
        });
    }

    @Override
    public String getDescription() {
        return "Skills recharge **" + cdReduction + "** turns faster." +
                "\nEvery **" + skillCount.getMax() + "** damaging Skills, deal __" + percent(getPower()) + "__ of the target's max health.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Magical Mastery: " + percent(getPower());
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(COOLDOWN_REDUCTION, cdReduction);
    }
}
