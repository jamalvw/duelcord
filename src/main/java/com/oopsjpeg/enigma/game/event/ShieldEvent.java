package com.oopsjpeg.enigma.game.event;

import com.oopsjpeg.enigma.game.Event;
import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.util.Emote;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class ShieldEvent extends Event {
    private float amount;

    public ShieldEvent(GameMember actor, float amount) {
        this(actor);
        setAmount(amount);
    }

    public ShieldEvent(GameMember actor) {
        super(actor, Emote.SHIELD);
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

    @Override
    public List<Hook<? extends Event>> createPipeline() {
        var pipeline = new ArrayList<Hook<? extends Event>>();
        pipeline.addAll(getActor().getHooks(EventType.SHIELD_ALL));
        pipeline.addAll(getActor().getHooks(EventType.SHIELD_RECEIVED));

        GameMember observer = getGame().getRandomTarget(getActor());
        pipeline.addAll(observer.getHooks(EventType.SHIELD_ALL));
        pipeline.addAll(observer.getHooks(EventType.SHIELD_RECEIVED));

        return pipeline;
    }

    @Override
    public void complete() {
        if (amount > 0) {
            getActor().addShield(round(amount));
            getOutput().add(0, Emote.SHIELD + "**" + getActor().getUsername() + "** shielded for **" + round(amount)
                    + "**! [**" + getActor().getShield() + "** left]"
                    + (hasSource() ? " (" + getSource() + ")" : ""));
        }
    }
}
