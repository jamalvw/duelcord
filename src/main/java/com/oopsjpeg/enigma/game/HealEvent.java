package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.util.Emote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.oopsjpeg.enigma.game.StatType.MAX_HEALTH;
import static java.lang.Math.round;

public class HealEvent extends Event {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private float amount;

    public HealEvent(GameMember actor, float amount) {
        this(actor);
        this.amount = amount;
    }

    public HealEvent(GameMember actor) {
        super(actor);
        setEmote(Emote.HEAL);
    }

    @Override
    public void complete() {
        for (PendingAction action : getEffects()) {
            LOGGER.debug("Executing pending action {}", action.getClass().getSimpleName());
            action.execute();
            getOutput().add(getActor().updateStats());
        }

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
