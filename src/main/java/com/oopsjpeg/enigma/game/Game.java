package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.game.buff.SilenceDebuff;
import com.oopsjpeg.enigma.game.item.PotionItem;
import com.oopsjpeg.enigma.game.object.*;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.listener.CommandListener;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.core.spec.StartThreadWithoutMessageSpec;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.oopsjpeg.enigma.game.GameState.*;
import static com.oopsjpeg.enigma.game.StatType.*;

public class Game {
    private final Enigma instance;
    private final ThreadChannel channel;
    private final Message statusMessage;
    private final GameMode mode;
    private final List<GameMember> members;
    private final CommandListener commandListener;
    private final Stacker afkTimer = new Stacker(6);

    private List<GameAction> actions = new ArrayList<>();
    private List<Distortion> distortions = new ArrayList<>();
    private LocalDateTime lastAction = LocalDateTime.now();

    private GameState gameState = PICKING;
    private int turnCount = 0;
    private int turnIndex = -1;

    public Game(Enigma instance, GameMode mode, Collection<Player> players) {
        this.instance = instance;
        this.mode = mode;

        members = players.stream().map(p -> new GameMember(this, p)).collect(Collectors.toList());
        Collections.shuffle(members);

        String threadName = getMembers().get(0).getUsername() + " vs. " + getMembers().get(1).getUsername();
        channel = instance.getMatchmakingChannel().startPrivateThread(StartThreadWithoutMessageSpec.builder()
                        .name(threadName)
                        .invitable(false)
                        .type(ThreadChannel.Type.GUILD_PRIVATE_THREAD)
                        .autoArchiveDuration(ThreadChannel.AutoArchiveDuration.DURATION1)
                        .build())
                .block();
        players.forEach(p -> channel.addMember(p.getUser()).subscribe());

        statusMessage = channel.createEmbed(EmbedCreateSpec.builder().description("Game status will appear here.").build()).block();
        statusMessage.pin().subscribe();

        commandListener = new CommandListener(instance,
                Config.getGamePrefix(),
                GameCommand.values(), channel);
        instance.addListener(commandListener);


        channel.createMessage(nextTurn()).subscribe();
    }

    public String nextTurn() {
        final List<String> output = new ArrayList<>();

        // Handle turn ending
        if (gameState == PLAYING) {
            // On turn end
            output.addAll(getCurrentMember().getData().stream().map(e -> e.onTurnEnd(getCurrentMember())).collect(Collectors.toList()));
            // On defend
            if (turnCount >= 1 && getCurrentMember().hasEnergy() && !getCurrentMember().hasBuff(SilenceDebuff.class))
                output.add(getCurrentMember().defend());
            // Decrement buff timers
            // This used to also add to output, but updateStats() now checks for expired buffs.
            getCurrentMember().getBuffs().stream()
                    .filter(Buff::shouldCountOnTurnEnd)
                    .forEach(Buff::turn);
            // Update current member's stats
            output.add(getCurrentMember().updateStats());
        }

        // Start next turn
        turnIndex++;
        // Reset turn index at max
        if (turnIndex >= members.size()) {
            turnIndex = 0;
            // Start game once all players have picked
            if (gameState == PICKING && members.stream().allMatch(GameMember::alreadyPickedUnit))
                gameState = PLAYING;
        }

        if (gameState == PICKING) {
            // First pick message
            if (turnIndex == 0) {
                String playerList = getPlayers().stream().map(Player::getUsername).collect(Collectors.joining(", "));
                output.add("## Welcome to " + mode.getName());
                output.add("featuring **" + getMembers().get(0).getUsername() + "** vs. **" + getMembers().get(1).getUsername() + "**!");
                if (getPlayers().stream().anyMatch(p -> !p.hasPlayedBefore()))
                    output.add("\n*One or more players in this match haven't played before, so guides have been enabled.*");
            }
            output.add("# " + getCurrentMember().getMention() + "'s Pick");
            output.add("Choose a unit with **`" + commandListener.getPrefix() + GameCommand.PICK.getName() + " [unit]`**");
            output.add("Don't know who to play? Try **`" + commandListener.getPrefix() + GameCommand.PICK.getName() + " random`**");
        } else if (gameState == PLAYING) {
            GameMember member = getCurrentMember();

            turnCount++;

            // Apply distortions if enabled
            if (mode.isDistortionsEnabled()) {
                if (turnCount >= 6 && distortions.isEmpty()) {
                    Distortion distortion = Util.pickRandom(getUnusedDistortions());
                    output.add("# First Distortion");
                    output.add(Emote.DISTORTION + "**" + distortion.getName() + "** - " + distortion.getDescription());
                    distortions.add(distortion);
                    distortion.start(this);
                }

                if (turnCount >= 17 && distortions.size() < 2) {
                    Distortion distortion = Util.pickRandom(getUnusedDistortions());
                    output.add("# Second Distortion");
                    output.add(Emote.DISTORTION + "**" + distortion.getName() + "** - " + distortion.getDescription());
                    distortions.add(distortion);
                    distortion.start(this);
                }

                if (turnCount >= 28 && distortions.size() < 3) {
                    Distortion distortion = Util.pickRandom(getUnusedDistortions());
                    output.add("# Final Distortion");
                    output.add(Emote.DISTORTION + "**" + distortion.getName() + "** - " + distortion.getDescription());
                    distortions.add(distortion);
                    distortion.start(this);
                }
            }

            List<String> mainDisplay = new ArrayList<>();
            mainDisplay.add("### " + member.getMention() + "'s Turn - " + member.getGold() + " Gold");
            mainDisplay.add("> Open this channel's pinned messages to see your stats.");

            Unit unit = member.getUnit();

            // Guides
            if (member.hasGuides()) {
                // Item purchasing
                if (!member.getGuides().hasPurchasedAnItem()) {
                    Items recommended = unit.getRecommendedBuild().stream().findAny().orElse(Items.IRON_SCIMITAR);
                    mainDisplay.add("> Try buying your first item with **`>buy " + recommended.getName() + "`**.");
                }
                // Attacking
                else if (!member.getGuides().hasAttacked())
                    mainDisplay.add("> Try attacking the enemy with **`>attack`**.");

                // Second turn
                if (member.getGuides().newTurn() == 1)
                    mainDisplay.add("> Your energy gets restored each turn. You now have **" + member.getStats().getInt(MAX_ENERGY) + "**.");

                // Third turn
                if (member.getGuides().newTurn() == 2)
                    mainDisplay.add("> As the game goes on, ");

                // Low health with potion still unused
                if (member.getHealthPercentage() <= 0.5f && member.getItems().stream().anyMatch(i -> i instanceof PotionItem) && !member.getGuides().hasUsedPotion())
                    mainDisplay.add("> You're low on health! Try using your potion with **`>use Potion`**.");
            }

            // On turn start
            output.addAll(member.getData().stream().map(e -> e.onTurnStart(member)).collect(Collectors.toList()));

            member.addHealth(member.getStats().get(HEALTH_PER_TURN) * (member.isDefensive() ? 2 : 1));
            member.giveGold(mode.handleGold(140 + (turnCount * 6)));
            member.giveGold(member.getStats().getInt(GOLD_PER_TURN));
            member.setEnergy(member.getStats().getInt(MAX_ENERGY));
            member.setShield(0);
            member.setDefensive(false);

            List<String> readiedCds = new ArrayList<>();

            // Count skill cooldowns
            List<Skill> skills = new ArrayList<>(Arrays.asList(unit.getSkills()));
            if (unit.hasForms())
                skills.addAll(Arrays.asList(unit.getForm().getSkills()));

            readiedCds.addAll(skills.stream()
                    .filter(Skill::hasCooldown)
                    .filter(skill -> {
                        Cooldown cooldown = skill.getCooldown();
                        return cooldown.count() && cooldown.tryNotify();
                    })
                    .map(skill -> "**`>" + skill.getName() + "`**")
                    .collect(Collectors.toList()));

            // Count item cooldowns
            readiedCds.addAll(member.getItems().stream()
                    .filter(i -> i.canUse(member))
                    .filter(Item::hasCooldown)
                    .filter(item -> {
                        Cooldown cd = item.getCooldown();
                        return cd.count() && cd.tryNotify();
                    })
                    .map(item -> "**`" + item.getName() + "`**")
                    .collect(Collectors.toList()));

            getCurrentMember().getBuffs().stream()
                    .filter(b -> !b.shouldCountOnTurnEnd())
                    .forEach(Buff::turn);

            if (readiedCds.size() == 1)
                mainDisplay.add("> " + readiedCds.get(0) + " is ready to use.");
            else if (!readiedCds.isEmpty())
                mainDisplay.add("> " + Util.joinWithAnd(readiedCds) + " are ready to use.");
            // Low health warning
            if (member.getHealthPercentage() < 0.2f)
                output.add(Emote.WARN + "**" + member.getUsername() + "** is critically low on health.");
            // Queued item can be afforded reminder
            if (member.hasQueuedItem() && member.getGold() >= member.getQueuedItem().create(null).build(member.getItems()).getCost())
                mainDisplay.add("> You can afford to **`>buy " + member.getQueuedItem().getName() + "`** now.");

            // Update current member's stats
            output.add(getCurrentMember().updateStats());

            output.addAll(mainDisplay);
        }

        updateStatus();

        return Util.joinNonEmpty("\n", output);
    }

    public void updateStatus() {
        List<EmbedCreateSpec> statuses = getNonCurrentMembers().stream()
                .map(GameMember::getStatus)
                .collect(Collectors.toList());
        statuses.add(0, getCurrentMember().getStatus());

        statusMessage.edit(MessageEditSpec.builder()
                        .embeds(statuses)
                        .build())
                .subscribe();
    }

    public List<GameMember> getNonCurrentMembers() {
        return getMembers().stream()
                .filter(member -> !getCurrentMember().equals(member))
                .collect(Collectors.toList());
    }

    public Guild getGuild() {
        return channel.getGuild().block();
    }

    public GameMember getMember(User user) {
        return members.stream()
                .filter(m -> m.getUser().equals(user))
                .findAny().orElse(null);
    }

    public GameMember getMember(Player player) {
        return members.stream()
                .filter(m -> m.getPlayer().equals(player))
                .findAny().orElse(null);
    }

    public GameMember getCurrentMember() {
        return members.get(turnIndex);
    }

    public GameMember getRandomTarget(GameMember exclude) {
        List<GameMember> targets = getAlive().stream().filter(m -> !m.equals(exclude)).collect(Collectors.toList());
        return targets.get(Util.RANDOM.nextInt(targets.size()));
    }

    public List<User> getUsers() {
        return members.stream().map(GameMember::getUser).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return members.stream().map(GameMember::getPlayer).collect(Collectors.toList());
    }

    public List<GameMember> getAlive() {
        return members.stream().filter(GameMember::isAlive).collect(Collectors.toList());
    }

    public List<GameMember> getDead() {
        return members.stream().filter(m -> !m.isAlive()).collect(Collectors.toList());
    }

    public GameMember getWinner() {
        return gameState == FINISHED ? getAlive().get(0) : null;
    }

    public Enigma getInstance() {
        return this.instance;
    }

    public ThreadChannel getChannel() {
        return this.channel;
    }

    public GameMode getMode() {
        return this.mode;
    }

    public List<GameMember> getMembers() {
        return this.members;
    }

    public CommandListener getCommandListener() {
        return this.commandListener;
    }

    public Stacker getAfkTimer() {
        return this.afkTimer;
    }

    public List<GameAction> getActions() {
        return this.actions;
    }

    public void setActions(List<GameAction> actions) {
        this.actions = actions;
    }

    public List<Distortion> getDistortions() {
        return distortions;
    }

    public void setDistortions(List<Distortion> distortions) {
        this.distortions = distortions;
    }

    public List<Distortion> getUnusedDistortions() {
        // Get all distortions
        List<Distortion> allDistortions = Arrays.stream(Distortion.values()).collect(Collectors.toList());
        // Remove distortions we already have
        allDistortions.removeIf(a -> distortions.contains(a));
        return allDistortions;
    }

    public LocalDateTime getLastAction() {
        return this.lastAction;
    }

    public void setLastAction(LocalDateTime lastAction) {
        this.lastAction = lastAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public int getTurnCount() {
        return this.turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public int getTurnIndex() {
        return this.turnIndex;
    }

    public void setTurnIndex(int turnIndex) {
        this.turnIndex = turnIndex;
    }
}
