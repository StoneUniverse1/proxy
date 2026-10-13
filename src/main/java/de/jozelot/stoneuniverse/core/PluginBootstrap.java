package de.jozelot.stoneuniverse.core;

import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.data.config.ConfigLoader;
import de.jozelot.stoneuniverse.data.config.ConfigManager;
import de.jozelot.stoneuniverse.data.redis.RedisConnection;
import de.jozelot.stoneuniverse.interfaces.Bootstrap;
import de.jozelot.stoneuniverse.interfaces.Registry;
import de.jozelot.stoneuniverse.mechanics.discordupdate.ServerPacketSender;
import de.jozelot.stoneuniverse.registry.ListenerRegistry;

public class PluginBootstrap implements Bootstrap {

    private final StoneUniverse plugin;

    private ConfigLoader configLoader;
    private ConfigManager configManager;

    private Registry listenerRegistry;

    private RedisConnection redisConnection;
    private ServerPacketSender serverPacketSender;

    private HeartBeat heartBeat;

    public PluginBootstrap(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        this.configLoader = new ConfigLoader(plugin, plugin.getDataDirectory());
        this.configManager = new ConfigManager(plugin);
        this.listenerRegistry = new ListenerRegistry(plugin);
        this.redisConnection = new RedisConnection(plugin);
        this.serverPacketSender = new ServerPacketSender(plugin);
        this.heartBeat = new HeartBeat(plugin);
        return true;
    }

    @Override
    public boolean enable() {
        if (!configLoader.load()) return false;
        if (!listenerRegistry.register()) return false;
        if (!redisConnection.init()) return false;
        heartBeat.start();
        return true;
    }

    @Override
    public void shutdown() {
        if (redisConnection != null) redisConnection.close();
        heartBeat.shutdown();
    }

    @Override
    public void reload() {

    }

    public ConfigManager getConfig() {
        return configManager;
    }
    public RedisConnection getRedisConnection() {
        return redisConnection;
    }
    public ServerPacketSender getServerPacketSender() {
        return serverPacketSender;
    }
}
