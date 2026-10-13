package de.jozelot.stoneUniverse_Proxy.core;

import de.jozelot.stoneUniverse_Proxy.StoneUniverse_Proxy;
import de.jozelot.stoneUniverse_Proxy.data.redis.RedisConnection;
import de.jozelot.stoneUniverse_Proxy.interfaces.Bootstrap;

public class PluginBootstrap implements Bootstrap {

    private final StoneUniverse_Proxy plugin;

    private RedisConnection redisConnection;

    public PluginBootstrap(StoneUniverse_Proxy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        this.redisConnection = new RedisConnection(plugin);
        return true;
    }

    @Override
    public boolean enable() {
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
}
