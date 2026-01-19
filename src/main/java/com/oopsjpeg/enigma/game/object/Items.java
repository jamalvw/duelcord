package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.effect.*;
import com.oopsjpeg.enigma.game.item.*;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;

import java.util.*;

import static com.oopsjpeg.enigma.game.StatType.*;

public enum Items {
    POTION("Potion"),
    ALCHEMISTS_ELIXIR("Alchemist's Elixir"),

    RING(Tree.BASIC, "Ring"),
    KNIFE(Tree.BASIC, "Knife"),

    BONE_SPEAR(Tree.ADVANCED, "Bone Spear"),
    KORAS_AMULET(Tree.ADVANCED, "Kora's Amulet"),
    HOLY_BAND(Tree.ADVANCED, "Holy Band"),
    BLOODLUST_BLADE(Tree.ADVANCED, "Bloodlust Blade"),
    BRONZE_CUTLASS(Tree.ADVANCED, "Bronze Cutlass"),
    MIDNIGHT_DAGGER(Tree.ADVANCED, "Midnight Dagger"),

    FAITHBREAKER(Tree.COMPLETE, "Faithbreaker"),
    CRIMSON_MIGHT(Tree.COMPLETE, "Crimson Might"),
    DAWN_HAMMER(Tree.COMPLETE, "Dawn Hammer"),
    WOLFS_FANG(Tree.COMPLETE, "Wolf's Fang"),
    SOULSTEALER(Tree.COMPLETE, "Soulstealer"),
    IRON_SCIMITAR(Tree.COMPLETE, "Iron Scimitar"),
    SHADOW_REAVER(Tree.COMPLETE, "Shadow Reaver");

    private final Tree tree;
    private final String name;

    Items(String name) {
        this(null, name);
    }

    Items(Tree tree, String name) {
        this.tree = tree;
        this.name = name;
    }

    public static Items fromName(String name)
    {
        return Arrays.stream(values())
                .filter(i -> name.equalsIgnoreCase(i.name) || (name.length() >= 3
                        && i.name.toLowerCase().startsWith(name.toLowerCase())))
                .findAny().orElse(null);
    }

    public Item create(GameMember member) {
        switch(this) {
            case POTION: return new PotionItem(member);
            case ALCHEMISTS_ELIXIR: return new AlchemistsElixirItem(member);

            case RING: return new RingItem(member);
            case KNIFE: return new KnifeItem(member);

            case BONE_SPEAR: return new BoneSpearItem(member);
            case KORAS_AMULET: return new KorasAmuletItem(member);
            case HOLY_BAND: return new HolyBandItem(member);
            case BLOODLUST_BLADE: return new BloodlustBladeItem(member);
            case BRONZE_CUTLASS: return new BronzeCutlassItem(member);
            case MIDNIGHT_DAGGER: return new MidnightDaggerItem(member);

            case FAITHBREAKER: return new FaithbreakerItem(member);
            case CRIMSON_MIGHT: return new CrimsonMightItem(member);
            case DAWN_HAMMER: return new DawnHammerItem(member);
            case WOLFS_FANG: return new WolfsFangItem(member);
            case SOULSTEALER: return new SoulstealerItem(member);
            case IRON_SCIMITAR: return new IronScimitarItem(member);
            case SHADOW_REAVER: return new ShadowReaverItem(member);

            default: throw new IllegalArgumentException();
        }
    }

    public Tree getTree() {
        return tree;
    }

    public String getName() {
        return name;
    }
}
