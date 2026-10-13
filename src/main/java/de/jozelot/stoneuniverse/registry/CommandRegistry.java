package de.jozelot.stoneuniverse.registry;

import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.commands.PlayerInfoCommand;
import de.jozelot.stoneuniverse.interfaces.JCommand;
import de.jozelot.stoneuniverse.interfaces.Registry;

import java.util.Collection;
import java.util.List;

public class CommandRegistry implements Registry {

    private final StoneUniverse plugin;

    public CommandRegistry(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        registerCommands(new PlayerInfoCommand(plugin), List.of("playerinfo", "pinfo"));
        return true;
    }

    public void registerCommands(JCommand commandExecutor, Collection<String> names) {
        commandExecutor.register(names);
    }
}
