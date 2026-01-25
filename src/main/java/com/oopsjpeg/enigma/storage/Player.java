package com.oopsjpeg.enigma.storage;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

public class Player {
    private final String id;
    private int gems;
    private int wins;
    private int losses;
    private int rp;
    private boolean hasPlayedBefore;

    public Player(String id) {
        this.id = id;
    }

    public User getUser() {
        return Enigma.getInstance().getClient().getUserById(Snowflake.of(id)).block();
    }

    public Member getMember(Snowflake guildId) {
        return getUser().asMember(guildId).block();
    }

    public String getUsername() {
        return getUser().getUsername();
    }

    public void addGems(int gems) {
        this.gems += gems;
    }

    public void removeGems(int gems) {
        this.gems -= gems;
    }

    public void win() {
        wins++;
    }

    public void lose() {
        losses++;
    }

    public void win(float loserRp) {
        float average = (rp + loserRp) / 2;
        float weight = rp / average;
        rp += Util.limit(weight * 100, 50, 125);
        wins++;
    }

    public void lose(float winnerRp) {
        float average = (rp + winnerRp) / 2;
        float weight = rp / average;
        rp -= Util.limit(weight * 100, 50, 125);
        losses++;
    }

    public int getTotalGames() {
        return wins + losses;
    }

    public float getWinRate() {
        return getTotalGames() > 0 ? (float) wins / getTotalGames() : 0;
    }

    public int getRankedPoints() {
        if (rp == 0)
            rp = 1000;
        return rp;
    }

    public void setRankedPoints(int rankedPoints) {
        getRankedPoints();
        this.rp = Math.max(1, rankedPoints);
    }

    @Override
    public int hashCode() {
        return getUser().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Player && ((Player) o).id.equals(id));
    }

    public String getId() {
        return this.id;
    }

    public int getGems() {
        return this.gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public int getWins() {
        return this.wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return this.losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setHasPlayedBefore(boolean hasPlayedBefore) {
        this.hasPlayedBefore = hasPlayedBefore;
    }
}
