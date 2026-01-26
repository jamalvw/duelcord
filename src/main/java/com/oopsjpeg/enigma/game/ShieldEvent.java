package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.util.Emote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.round;

public class ShieldEvent extends Event {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private float amount;

    public ShieldEvent(GameMember actor, float amount) {
        this(actor);
        setAmount(amount);
    }

    public ShieldEvent(GameMember actor) {
        super(actor);
        setEmote(Emote.SHIELD);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public void addAmount(float amount) {
        this.amount += amount;
    }

    public void subtractAmount(float amount) {
        this.amount -= amount;
    }

    @Override
    public void complete() {
        for (PendingAction action : getEffects()) {
            LOGGER.debug("Executing pending action {}", action.getClass().getSimpleName());
            action.execute();
            getOutput().add(getActor().updateStats());
        }

        if (amount > 0) {
            getActor().addShield(round(amount));
            getOutput().add(0, Emote.SHIELD + "**" + getActor().getUsername() + "** shielded for **" + round(amount)
                    + "**! [**" + getActor().getShield() + "** left]"
                    + (hasSource() ? " (" + getSource() + ")" : ""));
        }
    }
}
