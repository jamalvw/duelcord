package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.object.Item;

import java.util.List;

public class Build {
    private final Item item;
    private final int reduction;
    private final List<Item> postData;

    public Build(Item item, int reduction, List<Item> postData) {
        this.item = item;
        this.reduction = reduction;
        this.postData = postData;
    }

    public int getCost() {
        return item.getCost() - reduction;
    }

    public Item getItem() {
        return this.item;
    }

    public int getReduction() {
        return this.reduction;
    }

    public List<Item> getPostData() {
        return this.postData;
    }
}
