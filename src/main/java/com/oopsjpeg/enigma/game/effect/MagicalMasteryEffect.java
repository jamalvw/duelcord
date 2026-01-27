package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
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

        onDamageDealt(Priority.PRE_CALCULATION, event -> {
            if (!event.isSkill()) return;

            event.queueAction(() -> {
                if (skillCount.stack()) {
                    event.addDamage(event.getVictim().getStats().get(MAX_HEALTH) * getPower());
                    skillCount.reset();
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return "Skills recharge __" + cdReduction + "__ turn faster." +
                "\nEvery __" + skillCount.getMax() + "__ damaging Skills, deal __" + percent(getPower()) + "__ of the enemy's max health as damage.";
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
