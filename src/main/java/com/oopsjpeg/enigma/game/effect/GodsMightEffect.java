package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.object.Effect;

public class GodsMightEffect extends Effect {
    public GodsMightEffect(GameMember owner) {
        super(owner, "Might of God", 0, null);

        onDamageDealt(Priority.PRE_CALCULATION, e -> {
            if (!e.isSkill()) return;

            e.setIsAbleToCrit(true);
        });
    }

    @Override
    public String getDescription() {
        return "Skills can critically strike.";
    }
}
