package de.jozelot.stoneuniverse.commands;

import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.interfaces.JCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class PlayerInfoCommand implements JCommand {

    private final StoneUniverse plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerInfoCommand(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        String mainName = names.iterator().next();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            var mainBuilder = Commands.literal(mainName)
                    .then();

            LiteralCommandNode<CommandSourceStack> mainNode = mainBuilder.build();
            commands.register(mainNode, "Command for seing all players");

            for (String name : names) {
                if (name.equalsIgnoreCase(mainName)) continue;
                commands.register(
                        Commands.literal(name)
                                .requires(mainBuilder.getRequirement())
                                .executes(mainBuilder.getCommand())
                                .redirect(mainNode)
                                .build(),
                        "Alias for the command."
                );
            }
        });
    }
}
