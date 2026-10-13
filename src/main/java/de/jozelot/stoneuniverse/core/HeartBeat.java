package de.jozelot.stoneuniverse.core;

import de.jozelot.stoneuniverse.StoneUniverse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class HeartBeat {

    private final StoneUniverse plugin;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> runningTask;
    private final Logger logger;

    private long secondsPassed = 0;

    public HeartBeat(StoneUniverse plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void start() {
        if (runningTask != null && !runningTask.isDone()) {
            return;
        }
        logger.info("Starting scheduler...");
        secondsPassed = 0;

        plugin.getBootstrap().getServerPacketSender().sendStatusAsync();

        runningTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                secondsPassed++;

                if (secondsPassed % 5 == 0) {
                    plugin.getBootstrap().getServerPacketSender().sendStatusAsync();
                }

            } catch (Exception e) {
                logger.error("Scheduler task run into an error", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
        logger.info("Scheduler started");
    }

    public void stop() {
        if (runningTask != null) {
            runningTask.cancel(false);

            runningTask = null;
            logger.info("Scheduler stopped");
        }
    }

    public void shutdown() {
        stop();
        scheduler.shutdown();
        logger.info("Scheduler shutdown");
    }
}
