package com.oopsjpeg.enigma.game.event;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.hook.*;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class DamageEvent extends Event {
    private final GameMember victim;
    private float damage;
    private boolean blocked = false;

    private float onHitScale = 1.0f;
    private boolean isAbleToCrit = false;
    private boolean isGoingToCrit = false;
    private float critMul;
    private float healing;
    private float shielding;

    private boolean isOnHit;
    private boolean isAttack;
    private boolean isSkill;
    private boolean isDoT;
    private boolean ignoreShield;

    public DamageEvent(GameMember attacker, GameMember victim) {
        super(attacker, Emote.ATTACK);
        this.victim = victim;
    }

    public GameMember getVictim() {
        return victim;
    }

    @Override
    public List<Hook<? extends Event>> createPipeline() {
        List<Hook<? extends Event>> hooks = new ArrayList<>();
        hooks.addAll(getActor().getHooks(EventType.DAMAGE_ALL));
        hooks.addAll(getActor().getHooks(EventType.DAMAGE_DEALT));
        hooks.addAll(victim.getHooks(EventType.DAMAGE_ALL));
        hooks.addAll(victim.getHooks(EventType.DAMAGE_RECEIVED));
        hooks.add(new BlockHook());
        hooks.add(new CriticalStrikeHook());
        hooks.add(new DodgeHook());
        hooks.add(new LifeStealHook());
        hooks.add(new ResistanceHook());
        hooks.add(new ShieldHook());
        return hooks;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = Math.max(0, damage);
    }

    public void addDamage(float amount) {
        setDamage(getDamage() + amount);
    }

    public void multiplyDamage(float mul) {
        setDamage(getDamage() * mul);
    }

    public void subtractDamage(float amount) {
        setDamage(getDamage() - amount);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isOnHit() {
        return isOnHit;
    }

    public void setIsOnHit(boolean onHit) {
        isOnHit = onHit;
    }

    public float getOnHitScale() {
        return onHitScale;
    }

    public void setOnHitScale(float onHitScale) {
        this.onHitScale = onHitScale;
    }

    public boolean isAbleToCrit() {
        return isAbleToCrit;
    }

    public void setIsAbleToCrit(boolean ableToCrit) {
        this.isAbleToCrit = ableToCrit;
    }

    public boolean isGoingToCrit() {
        return isGoingToCrit;
    }

    public void setIsGoingToCrit(boolean goingToCrit) {
        isGoingToCrit = goingToCrit;
    }

    public boolean isDoT() {
        return isDoT;
    }

    public void setIsDoT(boolean dot) {
        isDoT = dot;
    }

    public void crit() {
        isGoingToCrit = true;
    }

    public float getHealing() {
        return healing;
    }

    public void setHealing(float healing) {
        this.healing = Math.max(0, healing);
    }

    public void addHealing(float healing) {
        setHealing(getHealing() + healing);
    }

    public float getShielding() {
        return shielding;
    }

    public void setShielding(float shielding) {
        this.shielding = Math.max(0, shielding);
    }

    public void addShielding(float amount) {
        setShielding(getShielding() + amount);
    }

    public boolean isAttack() {
        return isAttack;
    }

    public void setIsAttack(boolean attack) {
        isAttack = attack;
    }

    public boolean isSkill() {
        return isSkill;
    }

    public void setIsSkill(boolean skill) {
        isSkill = skill;
    }

    public boolean isIgnoreShield() {
        return ignoreShield;
    }

    public void setIgnoreShield(boolean ignoreShield) {
        this.ignoreShield = ignoreShield;
    }

    @Override
    public void complete() {
        getGame().getMode().handleDamage(this);

        if (healing > 0)
            getOutput().add(EventDispatcher.dispatch(new HealEvent(getActor(), healing)));
        if (shielding > 0)
            getOutput().add(EventDispatcher.dispatch(new ShieldEvent(getActor(), shielding)));

        // Summon damaging
        //if (event.target.hasBlockingSummon())
        //{
        //    Summon summon = event.target.getBlockingSummon();
        //    summon.takeHealth(event.damage + event.bonus);
        //    if (summon.getHealth() <= 0) summon.remove();
        //    event.output.add(0, Util.damageText(event, event.actor.getUsername(), event.target.getUsername() + "'s " + summon.getName(), emote, source));
        //}

        if (damage > 0) {
            victim.subtractHealth(round(damage));
            getOutput().add(0, Util.damageText(this, getActor().getUsername(), victim.getUsername(), getEmote(), getSource()));
        }
    }
}