package de.jozelot.stoneuniverse.core;

import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.data.config.ConfigLoader;
import de.jozelot.stoneuniverse.data.config.ConfigManager;
import de.jozelot.stoneuniverse.data.redis.RedisConnection;
import de.jozelot.stoneuniverse.interfaces.Bootstrap;

public class PluginBootstrap implements Bootstrap {

    private final StoneUniverse plugin;

    private ConfigLoader configLoader;
    private ConfigManager configManager;

    private RedisConnection redisConnection;

    public PluginBootstrap(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        this.configLoader = new ConfigLoader(plugin, plugin.getDataDirectory());
        this.configManager = new ConfigManager(plugin);
        this.redisConnection = new RedisConnection(plugin);
        return true;
    }

    @Override
    public boolean enable() {
        if (!configLoader.load()) return false;
        if (!redisConnection.init()) return false;
        return true;
    }

    @Override
    public void shutdown() {
        if (redisConnection != null) redisConnection.close();
    }

    @Override
    public void reload() {

    }

    public ConfigManager getConfig() {
        return configManager;
    }
}
