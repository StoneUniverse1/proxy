package de.jozelot.stoneuniverse;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.jozelot.stoneuniverse.core.PluginBootstrap;
import org.slf4j.Logger;

import java.nio.file.Path;

public class StoneUniverse {

    private PluginBootstrap bootstrap;

    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;

    @Inject
    public StoneUniverse(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("Plugin Starting...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        bootstrap = new PluginBootstrap(this);

        boolean register = bootstrap.register();
        if (!register) {
            logger.error("Object register failed! Look above for further information.");
            return;
        }

        boolean enable = bootstrap.enable();
        if (!enable) {
            logger.error("Object activation failed! Look above for further information.");
            return;
        }
        // TODO: Alles schön machen, habe einfach keinen Bock gerade das direkt richtig zu machen
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        bootstrap.shutdown();
    }

    public Logger getLogger() {
        return logger;
    }
    public ProxyServer getServer() {
        return server;
    }
    public Path getDataDirectory() {
        return dataDirectory;
    }

    public PluginBootstrap getBootstrap() {
        return bootstrap;
    }
}
