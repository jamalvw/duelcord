package com.oopsjpeg.enigma.game.event;

import com.oopsjpeg.enigma.game.Event;
import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.util.Emote;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.MAX_HEALTH;
import static java.lang.Math.round;

public class HealEvent extends Event {
    private float amount;

    public HealEvent(GameMember actor, float amount) {
        this(actor);
        this.amount = amount;
    }

    public HealEvent(GameMember actor) {
        super(actor, Emote.HEAL);
    }

    @Override
    public List<Hook<? extends Event>> createPipeline() {
        var pipeline = new ArrayList<Hook<? extends Event>>();
        pipeline.addAll(getActor().getHooks(EventType.HEAL_ALL));
        pipeline.addAll(getActor().getHooks(EventType.HEAL_RECEIVED));

        GameMember observer = getGame().getRandomTarget(getActor());
        pipeline.addAll(observer.getHooks(EventType.HEAL_ALL));
        pipeline.addAll(observer.getHooks(EventType.HEAL_SEEN));

        return pipeline;
    }

    @Override
    public void complete() {
        if (amount > 0) {
            getActor().addHealth(round(amount));
            getOutput().add(0, Emote.HEAL + "**" + getActor().getUsername() + "** healed for **" + round(amount) + "**! [**"
                    + getActor().getHealth() + " / " + getActor().getStats().getInt(MAX_HEALTH) + "**]"
                    + (hasSource() ? " (" + getSource() + ")" : ""));
        }
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void addAmount(float amount) {
        this.amount += amount;
    }

    public void subtractAmount(float amount) {
        this.amount -= amount;
    }

    public void multiplyAmount(float multiplier) {
        amount *= multiplier;
    }
}
