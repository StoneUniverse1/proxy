package de.jozelot.stoneuniverse.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import de.jozelot.stoneuniverse.StoneUniverse;

public class ConnectionListener {

    private final StoneUniverse plugin;

    public ConnectionListener(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        plugin.getBootstrap().getServerPacketSender().sendStatusAsync();
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        plugin.getBootstrap().getServerPacketSender().sendStatusAsync();
    }
}
