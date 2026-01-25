package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.TrapType;
import com.oopsjpeg.enigma.game.object.*;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.listener.CommandListener;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Pity;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.*;
import java.util.stream.Collectors;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;
import static java.lang.Math.max;
import static java.lang.Math.round;

public class GameMember
{
    private final Game game;
    private final Player player;
    private Unit unit;
    private boolean alive = true;
    private boolean defensive = false;
    private TrapType lastTrapType = null;

    CommandListener commands;

    private final List<Item> items = new ArrayList<>();
    private final Map<Class<? extends Effect>, Effect> effects = new HashMap<>();
    private final List<Buff> buffs = new ArrayList<>();
    private final List<Summon> summons = new ArrayList<>();

    private final Pity critPity = new Pity(0, 0.5f);

    private int health = 0;
    private int gold = 0;
    private int energy = 0;
    private int shield = 0;

    private Stats stats = new Stats();

    public GameMember(Game game, Player player)
    {
        this.game = game;
        this.player = player;
    }

    public User getUser()
    {
        return player.getUser();
    }

    public String getUsername()
    {
        return getUser().getUsername();
    }

    public String getMention()
    {
        return getUser().getMention();
    }

    public float getRankedPoints()
    {
        return getPlayer().getRankedPoints();
    }

    public List<GameObject> getData()
    {
        List<GameObject> data = new ArrayList<>();
        data.add(getUnit());
        data.addAll(getItems());
        data.addAll(getEffects());
        data.addAll(getBuffs());
        data.addAll(getSummons());
        return data;
    }

    public List<Item> getItems()
    {
        return items;
    }

    public Item getItem(String query) {
        return items.stream()
                .filter(i -> query.equalsIgnoreCase(i.getName()) || (query.length() >= 3
                        && i.getName().toLowerCase().startsWith(query.toLowerCase())))
                .findAny().orElse(null);
    }

    public List<Effect> getEffects()
    {
        return new ArrayList<>(effects.values());
    }

    public Effect getEffect(Class<? extends Effect> effect)
    {
        return effects.get(effect);
    }

    public void addEffect(Effect effect)
    {
        effects.put(effect.getClass(), effect);
    }

    public boolean hasEffect(Effect effect)
    {
        return effects.containsKey(effect.getClass());
    }

    public Buff getBuff(Class<? extends Buff> buffType)
    {
        return getBuffs().stream().filter(buff -> buff.getClass().equals(buffType)).findAny().orElse(null);
    }

    public List<Buff> getBuffs()
    {
        return new ArrayList<>(buffs);
    }

    public boolean hasBuff(Class<? extends Buff> buffType)
    {
        return getBuffs().stream().anyMatch(buff -> buff.getClass().equals(buffType));
    }

    public String addBuff(Buff buff, String emote)
    {
        final List<String> output = new ArrayList<>();

        // If the member already has this buff, replace it
        if (hasBuff(buff.getClass())) buffs.removeIf(b -> b.getClass().equals(buff.getClass()));

        buffs.add(buff);
        output.add(emote + "**" + getUsername() + "** received **" + buff.getName() + "**" +
                (buff.hasPower() ? " (" + buff.formatPower() + ")" : "") +
                (buff.getTotalTurns() > 1 ? " for **" + buff.getTotalTurns() + "** turns" : "") + "!");
        output.add(updateStats());
        return Util.joinNonEmpty("\n", output);
    }

    public String removeBuff(Buff buff)
    {
        final List<String> output = new ArrayList<>();
        buffs.remove(buff);
        output.add(updateStats());
        return Util.joinNonEmpty("\n", output);
    }

    public String removeBuffs(Class<? extends Buff> buffType)
    {
        return Util.joinNonEmpty("\n", getBuffs().stream()
                .filter(buff -> buff.getClass().equals(buffType))
                .map(this::removeBuff).
                collect(Collectors.toList()));
    }

    public List<Summon> getSummons() {
        return new ArrayList<>(summons);
    }

    public String addSummon(Summon summon, String emote)
    {
        final List<String> output = new ArrayList<>();
        summons.add(summon);
        output.add(emote + "**" + getUsername() + "** summoned **" + summon.getName() + "** (" + round(summon.getHealth()) + " HP)!");
        output.add(updateStats());
        return Util.joinNonEmpty("\n", output);
    }

    public List<Hook<?>> getHooks(Class<? extends Event> eventClass) {
        return getData().stream()
                .flatMap(o -> o.getHooks(eventClass).stream())
                .collect(Collectors.toList());
    }

    public boolean hasBlockingSummon()
    {
        return summons.stream().anyMatch(Summon::isBlocker);
    }

    public Summon getBlockingSummon()
    {
        return summons.stream().filter(Summon::isBlocker).findFirst().orElse(null);
    }

    public boolean alreadyPickedUnit()
    {
        return getUnit() != null;
    }

    public String updateStats()
    {
        final List<String> output = new ArrayList<>();

        stats.putAll(unit.getStats());

        for (Item item : getItems())
        {
            stats.addAll(item.getStats());

            for (Effect newEffect : item.getEffects())
                if (!hasEffect(newEffect)) addEffect(newEffect);
                else
                {
                    // If this effect is stronger than the old one, replace it
                    Effect oldEffect = getEffect(newEffect.getClass());
                    if (newEffect.getPower() > oldEffect.getPower())
                        addEffect(newEffect);
                }
        }

        for (Effect effect : getEffects()) {
            stats.addAll(effect.getStats());
        }

        for (Buff buff : getBuffs())
        {
            if (buff.shouldRemove())
            {
                buffs.remove(buff);
                if (!buff.isSilent())
                    output.add(Emote.TIME + "**" + getUsername() + "** no longer has **" + buff.getName() + "**.");
            } else
                stats.addAll(buff.getStats());
        }

        for (Summon summon : getSummons())
        {
            if (summon.shouldRemove())
            {
                summons.remove(summon);
                output.add(Emote.DEFEAT + "**" + getUsername() + "'s " + summon.getName() + "** has perished.");
            }
        }

        critPity.setChance(stats.get(CRIT_CHANCE));

        if (game.getGameState() == GameState.PLAYING && !hasHealth()) output.add(lose());

        return Util.joinNonEmpty("\n", output);
    }

    public String act(GameAction action) {
        game.getActions().add(action);

        takeEnergy(action.getCost(this));

        final List<String> output = new ArrayList<>();
        output.add(action.execute(this));
        output.add(updateStats());

        if (!hasEnergy())
            output.add(game.nextTurn());
        else if (!hasHealth())
            output.add(lose());

        game.updateStatus();

        return Util.joinNonEmpty("\n", output);
    }

    public String shield(float shieldAmount)
    {
        for (GameObject o : getData()) shieldAmount = o.onShield(shieldAmount);

        giveShield(round(shieldAmount));

        return Emote.SHIELD + "**" + getUsername() + "** shielded for **" + round(shieldAmount)
                + "**! [**" + getShield() + "**]";
    }

    public String heal(float healAmount)
    {
        return heal(healAmount, null, true);
    }

    public String heal(float healAmount, String source)
    {
        return heal(healAmount, source, true);
    }

    public String heal(float healAmount, String source, boolean message)
    {
        for (GameObject o : getData()) healAmount = o.onHeal(healAmount);

        giveHealth(round(healAmount));

        if (message)
            return Emote.HEAL + "**" + getUsername() + "** healed for **" + round(healAmount) + "**! [**"
                    + getHealth() + " / " + stats.getInt(MAX_HEALTH) + "**]"
                    + (source == null ? "" : " (" + source + ")");

        return null;
    }

    public String defend()
    {
        if (!defensive)
        {
            defensive = true;
            List<String> output = getData().stream().map(o -> o.onDefend(this)).collect(Collectors.toList());
            output.add(0, Emote.DEFEND + "**" + getUsername() + "** is defending (**" + Util.percent(getResist()) + "** resist, **" + (stats.getInt(HEALTH_PER_TURN) * 2) + "** regen)!");
            return Util.joinNonEmpty("\n", output);
        }
        return null;
    }

    public String win()
    {
        game.getInstance().getGameService().destroyGame(game);
        return Emote.VICTORY + getUser().getMention() + ", you have won the game!\n";
    }

    public String lose()
    {
        List<String> output = new ArrayList<>();
        output.add(Emote.DEFEAT + getUser().getMention() + " has been slain and removed from the game!");

        alive = false;

        if (game.getAlive().size() == 1)
        {
            game.setGameState(GameState.FINISHED);
            output.add(game.getAlive().get(0).win());
        } else if (game.getCurrentMember().equals(this))
            output.add(game.nextTurn());

        return Util.joinNonEmpty("\n", output);
    }

    public float getResist()
    {
        return stats.get(RESIST) + (defensive ? 0.25f : 0);
    }

    public float getBonusDamage()
    {
        return stats.get(ATTACK_POWER) - unit.getStats().get(ATTACK_POWER);
    }

    public float getBonusHealth()
    {
        return stats.get(MAX_HEALTH) - unit.getStats().get(MAX_HEALTH);
    }

    @Override
    public String toString()
    {
        return getPlayer().toString();
    }

    public Game getGame()
    {
        return this.game;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public Unit getUnit()
    {
        return this.unit;
    }

    public void setUnit(Unit unit)
    {
        this.unit = unit;

        items.clear();
        effects.clear();
        buffs.clear();

        updateStats();

        items.add(Items.POTION.create(this));

        setHealth(stats.getInt(MAX_HEALTH));
        setGold(game.getMode().handleGold(175 + (100 * game.getAlive().indexOf(this))));

        commands = new CommandListener(Enigma.getInstance(), ">", unit.getSkills());
        commands.setUserLimit(getUser());
        Enigma.getInstance().addListener(commands);

        //if (unit instanceof Berserker)
        //    ((Berserker) unit).getRage().setCurrent(game.getAlive().indexOf(this));
    }

    public boolean isAlive()
    {
        return this.alive;
    }

    public void setAlive(boolean alive)
    {
        this.alive = alive;
    }

    public boolean isDefensive()
    {
        return this.defensive;
    }

    public void setDefensive(boolean defensive)
    {
        this.defensive = defensive;
    }

    public Pity getCritPity()
    {
        return critPity;
    }

    public TrapType randomTrapType() {
        TrapType[] types = Arrays.stream(TrapType.values())
                .filter(type -> type != getLastTrapType())
                .toArray(TrapType[]::new);
        TrapType type = types[RANDOM.nextInt(types.length)];
        lastTrapType = type;
        return type;
    }

    public TrapType getLastTrapType() {
        return lastTrapType;
    }

    public int getHealth()
    {
        return health;
    }

    public void setHealth(int healthAmount)
    {
        health = Util.limit(healthAmount, 0, stats.getInt(MAX_HEALTH));
    }

    public int giveHealth(int healthAmount)
    {
        setHealth(getHealth() + healthAmount);
        return getHealth();
    }

    public int takeHealth(int healthAmount)
    {
        setHealth(getHealth() - healthAmount);
        return getHealth();
    }

    public boolean hasHealth(int healthAmount)
    {
        return health > healthAmount;
    }

    public boolean hasHealth()
    {
        return health >= 1;
    }

    public float getHealthPercentage()
    {
        return health / getStats().get(MAX_HEALTH);
    }

    public int getMissingHealth()
    {
        return stats.getInt(MAX_HEALTH) - getHealth();
    }

    public int getEnergy()
    {
        return energy;
    }

    public void setEnergy(int energyAmount)
    {
        energy = Util.limit(energyAmount, 0, stats.getInt(MAX_ENERGY));
    }

    public int giveEnergy(int energyAmount)
    {
        setEnergy(getEnergy() + energyAmount);
        return getEnergy();
    }

    public int takeEnergy(int energyAmount)
    {
        setEnergy(getEnergy() - energyAmount);
        return getEnergy();
    }

    public boolean hasEnergy(int energyAmount)
    {
        return getEnergy() > energyAmount;
    }

    public boolean hasEnergy()
    {
        return hasEnergy(0);
    }

    public int getGold()
    {
        return gold;
    }

    public void setGold(int goldAmount)
    {
        gold = max(goldAmount, 0);
    }

    public int giveGold(int goldAmount)
    {
        setGold(getGold() + goldAmount);
        return getGold();
    }

    public int takeGold(int goldAmount)
    {
        setGold(getGold() - goldAmount);
        return getGold();
    }

    public boolean hasGold(int goldAmount)
    {
        return getGold() >= goldAmount;
    }

    public int getGoldDifference(int targetGoldAmount)
    {
        return targetGoldAmount - getGold();
    }

    public int getShield()
    {
        return shield;
    }

    public void setShield(float shieldAmount)
    {
        shield = round(max(shieldAmount, 0));
    }

    public float giveShield(float shieldAmount)
    {
        setShield(getShield() + shieldAmount);
        return getShield();
    }

    public float takeShield(float shieldAmount)
    {
        setShield(getShield() - shieldAmount);
        return getShield();
    }

    public boolean hasShield()
    {
        return getShield() > 0;
    }

    public Stats getStats()
    {
        return this.stats;
    }

    public void setStats(Stats stats)
    {
        this.stats = stats;
    }

    public EmbedCreateSpec getStatus()
    {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
        if (!alreadyPickedUnit())
        {
            embed.color(Color.GRAY);
            embed.author(getUsername(), null, getUser().getAvatarUrl());
            embed.description(getUsername() + " is choosing a unit to play as.");
        }
        else
        {
            Stats stats = getStats();

            if (game.getCurrentMember().equals(this))
                embed.title("**Current Turn**");

            // Core statuses
            List<String> coreStatuses = new ArrayList<>();
            coreStatuses.add("- Health: " + percent(getHealthPercentage()) + " (" + getHealth() + "/" + stats.getInt(MAX_HEALTH) + ")");
            coreStatuses.add("- Gold: " + getGold());
            coreStatuses.add("- Energy: " + getEnergy());
            coreStatuses.add("- Items: " + getItems().stream().map(Item::getName).collect(Collectors.toList()));

            // Unit status
            String unitStatus = getUnit().getStatus(this);

            // Skill statuses
            List<String> skillStatuses = Arrays.stream(getUnit().getSkills())
                    .map(skill -> skill.getStatus(this))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Buff statuses
            List<String> buffStatuses = getBuffs().stream()
                    .map(buff -> buff.getStatus(this))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Effect statuses
            List<String> effectStatuses = getEffects().stream()
                    .map(effect -> effect.getStatus(this))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Summon statuses
            List<String> summonStatuses = getSummons().stream()
                    .map(summon -> summon.getStatus(this))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            embed.author(getUnit().getName() + " (" + getUsername() + ")", null, getUser().getAvatarUrl());

            embed.description(String.join(" \n", coreStatuses) +
                    (unitStatus != null ? "\n\n" + unitStatus : "") +
                    (!skillStatuses.isEmpty() ? "\n\n" + String.join(" \n", skillStatuses) : "") +
                    (!buffStatuses.isEmpty() ? "\n\n" + String.join(" \n", buffStatuses) : "") +
                    (!summonStatuses.isEmpty() ? "\n\n" + String.join(" \n", summonStatuses) : "") +
                    (!effectStatuses.isEmpty() ? "\n\n" + String.join(" \n", effectStatuses) : ""));

            embed.color(unit.getColor());
        }
        return embed.build();
    }
}