package de.jozelot.stoneuniverse.registry;

import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.interfaces.Registry;
import de.jozelot.stoneuniverse.listener.ConnectionListener;

public class ListenerRegistry implements Registry {

    private final StoneUniverse plugin;

    public ListenerRegistry(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        var em = plugin.getServer().getEventManager();

        em.register(plugin, new ConnectionListener(plugin));

        return true;
    }
}
