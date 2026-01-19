package com.oopsjpeg.enigma.game.unit.duelist.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.duelist.buff.BlitzBuff;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BlitzSkill extends Skill {
    public static final float ATTACK_RATIO = 0.65f;

    public BlitzSkill(Unit unit) {
        super(unit, 50, 3);
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();

        for (Buff b : actor.getBuffs()) {
            if (b.isDebuff()) b.remove();
        }

        BlitzBuff blitz = new BlitzBuff(actor, actor, 1, ATTACK_RATIO);

        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Blitz** - Attacks will cost 25 energy!");
        actor.addBuff(blitz, Emote.BUFF);
        output.add(actor.updateStats());

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Blitz";
    }

    @Override
    public String getDescription() {
        return "Cleanse all debuffs. For the rest of the turn, attacks deal __" + percent(ATTACK_RATIO) + " Damage__ but cost **25** energy. ";
    }
}
