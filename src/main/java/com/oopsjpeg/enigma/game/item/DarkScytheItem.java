package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.buff.SilenceDebuff;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DarkScytheItem extends Item {
    private static final int DAMAGE = 45;

    public DarkScytheItem(GameMember owner) {
        super(owner, 1225, "Dark Scythe", 3);
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KNIFE, Items.BRONZE_CUTLASS};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.CRIT_CHANCE, 0.4f)
                .put(StatType.ATTACK_POWER, 60);
    }

    @Override
    public String getTip() {
        return "Apply Silence";
    }

    @Override
    public String getDescription() {
        return "On use, deal __" + DAMAGE + "__ damage and apply __Silence__ to the enemy on their next turn.\nCooldown: " + getCooldown().getDuration();
    }

    @Override
    public boolean canUse(GameMember member) {
        return true;
    }

    @Override
    public String onUse(GameMember member) {
        List<String> output = new ArrayList<>();
        GameMember target = member.getGame().getRandomTarget(member);

        output.add(Emote.SKILL + "**" + member.getUsername() + "** used **" + getName() + "**!");

        SilenceDebuff silence = new SilenceDebuff(target, member, 1);
        output.add(target.addBuff(silence, Emote.SILENCE));

        DamageEvent damage = new DamageEvent(member, target);
        damage.setSource("Dark Scythe");
        damage.setDamage(DAMAGE);
        output.add(EventDispatcher.dispatch(damage));

        return Util.joinNonEmpty("\n", output);
    }
}
