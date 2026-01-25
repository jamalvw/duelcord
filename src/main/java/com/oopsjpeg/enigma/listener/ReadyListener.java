package com.oopsjpeg.enigma.listener;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.service.GameService;
import com.oopsjpeg.enigma.service.QueueService;
import com.oopsjpeg.enigma.game.GameState;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Listener;
import com.oopsjpeg.enigma.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.MessageEditSpec;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ReadyListener implements Listener
{
    private final Enigma instance;

    public ReadyListener(Enigma instance)
    {
        this.instance = instance;
    }

    @Override
    public void register(GatewayDiscordClient client)
    {
        client.on(ReadyEvent.class).subscribe(this::onReady);

        //instance.getMongo().loadPlayers();

        QueueService queues = instance.getQueueService();
        GameService games = instance.getGameService();

        schedule(queues::refresh, 12, 12, TimeUnit.SECONDS);
        //Enigma.SCHEDULER.scheduleAtFixedRate(() -> instance.getPlayers().values().stream()
        //        .filter(queues::isInQueue)
        //        .filter(p -> Instant.now().isAfter(p.getQueueTime().plus(5, ChronoUnit.MINUTES)))
        //        .forEach(p ->
        //        {
        //            p.removeQueue();
        //            Util.sendFailure(p.getUser().getPrivateChannel().block(), "You've been removed from queue as there are currently no players available for that mode.");
        //        }), 2, 2, TimeUnit.MINUTES);
        schedule(games::checkForAfkGames, 1, 1, TimeUnit.MINUTES);
        schedule(() -> instance.getLeaderboardChannel().getMessagesBefore(Snowflake.of(Instant.now()))
                .switchIfEmpty(instance.getLeaderboardChannel().createEmbed(e -> e.setTitle("...")))
                .blockFirst()
                .edit(MessageEditSpec.builder().addEmbed(Util.leaderboard()
                                .withFooter(EmbedCreateFields.Footer.of("Updates every 10 minutes.", null)))
                        .build())
                .subscribe(), 0, 10, TimeUnit.MINUTES);
    }

    public static void schedule(Runnable task, long delay, long period, TimeUnit unit) {
        Enigma.SCHEDULER.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                // This is the "Safety Net"
                System.err.printf("[%s] Task failed: %s%n",
                        Thread.currentThread().getName(), t.getMessage());
                t.printStackTrace();
            }
        }, delay, period, unit);
    }

    public void onReady(ReadyEvent event)
    {
        Enigma.LOGGER.info("Enigma is ready.");
    }

    public Enigma getInstance()
    {
        return this.instance;
    }
}
