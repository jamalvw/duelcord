package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.Build;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Item extends GameObject {
    private final GameMember owner;
    private final int cost;
    private final String name;
    private final List<Effect> effects = new ArrayList<>();

    public Item(GameMember owner, int cost, String name) {
        this.owner = owner;
        this.cost = cost;
        this.name = name;

        effects.addAll(Arrays.asList(generateEffects()));
    }

    public GameMember getOwner() {
        return owner;
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public Items[] getBuild() {
        return new Items[0];
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public Effect[] generateEffects() {
        return new Effect[0];
    }

    public Stats getStats() {
        return new Stats();
    }

    public String getDescription() {
        return null;
    }

    public String getTip() {
        return null;
    }

    public String onUse(GameMember member) {
        return null;
    }

    public boolean canUse(GameMember member) {
        return false;
    }

    public boolean removeOnUse() {
        return false;
    }

    public Build build(Collection<Item> items) {
        int reduction = 0;

        // At the start of the build chain, this == the player's items
        List<Item> postData = new ArrayList<>(items);

        // Loop nodes in the build and reduce our total cost if the node appears in our item collection
        for (Items node : getBuild()) {
            Item item = node.create(null);

            if (postData.stream().anyMatch(i -> i.getName().equals(item.getName()))) {
                // Reduce directly
                reduction += item.cost;
                Item toRemove = postData.stream().filter(i -> i.getName().equals(item.getName())).findAny().get();
                postData.remove(toRemove);
            } else if (item.getBuild() != null) {
                // Find a reduction in the build
                Build build = item.build(postData);
                reduction += build.getReduction();
                postData = build.getPostData();
            }
        }

        return new Build(this, reduction, postData);
    }

    public boolean hasTip() {
        return getTip() != null;
    }

    public boolean hasBuild() {
        return getBuild() != null && getBuild().length > 0;
    }

    public boolean isBuyable() {
        return true;
    }

    public EmbedCreateSpec embed() {
        return Util.embed(getName() + " (" + getCost() + "g)", Util.joinNonEmpty("\n\n",
                "**`>buy " + getName() + "`**",
                Util.formatStats(getStats()),
                Util.formatEffects(getEffects()),
                hasBuild() ? "*Builds from " + Arrays.stream(getBuild()).map(Items::getName).collect(Collectors.joining(" + ")) + "*" : null), Color.CYAN);
    }
}
