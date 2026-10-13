package de.jozelot.stoneUniverse_Proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.jozelot.stoneUniverse_Proxy.core.PluginBootstrap;
import org.slf4j.Logger;

@Plugin(id = "stoneuniverse", name = "StoneUniverse", version = "1.0.0",
        url = "https://jozelot.de", description = "Proxy Plugin for StoneUniverse", authors = {"jozelot_"})
public class StoneUniverse_Proxy {

    private PluginBootstrap bootstrap;

    private final Logger logger;
    private final ProxyServer server;

    @Inject
    public StoneUniverse_Proxy(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

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
    public PluginBootstrap getBoostrap() {
        return bootstrap;
    }
}
