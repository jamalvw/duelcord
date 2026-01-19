package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.util.Emote;

import java.util.ArrayList;
import java.util.List;

public class DamageEvent
{
    private final List<String> output = new ArrayList<>();
    private final List<PendingAction> effects = new ArrayList<>();

    private final GameMember attacker;
    private final GameMember victim;
    private float damage;
    private boolean blocked = false;
    private boolean cancelled = false;

    private String source = "";
    private String emote = Emote.ATTACK;
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

    public DamageEvent(GameMember attacker, GameMember victim)
    {
        this.attacker = attacker;
        this.victim = victim;
    }

    public Game getGame()
    {
        return attacker.getGame();
    }

    public void proposeEffect(PendingAction action) {
        this.effects.add(action);
    }

    public List<PendingAction> getEffects() {
        return effects;
    }

    public List<String> getOutput() {
        return output;
    }

    public GameMember getAttacker() {
        return attacker;
    }

    public GameMember getVictim() {
        return victim;
    }

    public String getEmote() {
        return emote;
    }

    public void setEmote(String emote) {
        this.emote = emote;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public void multiplyDamage(float mul)
    {
        setDamage(getDamage() * mul);
    }

    public void subtractDamage(float amount)
    {
        setDamage(getDamage() - amount);
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
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

    public void crit()
    {
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
}