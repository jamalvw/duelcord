package com.oopsjpeg.enigma;

public class Guides {
    private boolean purchasedAnItem = false;
    private boolean attacked = false;
    private boolean usedPotion = false;
    private int turnCount;

    public boolean hasPurchasedAnItem() {
        return purchasedAnItem;
    }

    public void purchasedAnItem() {
        purchasedAnItem = true;
    }

    public boolean hasAttacked() {
        return attacked;
    }

    public void attacked() {
        attacked = true;
    }

    public boolean hasUsedPotion() {
        return usedPotion;
    }

    public void usedPotion() {
        usedPotion = true;
    }

    public int newTurn() {
        return turnCount++;
    }
}
