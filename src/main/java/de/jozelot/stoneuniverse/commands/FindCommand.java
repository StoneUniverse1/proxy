package de.jozelot.stoneuniverse.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.interfaces.JCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;

public class FindCommand implements JCommand {

    private final StoneUniverse plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public FindCommand(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        var commandManager = plugin.getServer().getCommandManager();
        String[] nameArray = names.toArray(new String[0]);
        String mainName = nameArray[0];

        LiteralArgumentBuilder<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal(mainName)
                .requires(commandSource -> commandSource.hasPermission("stoneuniverse.commands.find.use"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("target", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (Player player : plugin.getServer().getAllPlayers()) {
                                String username = player.getUsername();
                                if (username.toLowerCase().startsWith(context.getInput().toLowerCase())) builder.suggest(username);
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            String targetName = StringArgumentType.getString(context, "target");

                            Optional<Player> optPlayer = plugin.getServer().getPlayer(targetName);

                            if (optPlayer.isPresent()) {
                                Player player = optPlayer.get();
                                String server = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : "N/A";

                                source.sendMessage(mm.deserialize(plugin.getBootstrap().getLang().format("commands.find.success", Map.of("player", player.getUsername(), "server", server))));
                            } else {
                                source.sendMessage(mm.deserialize(plugin.getBootstrap().getLang().format("system.error.player-not-found", Map.of("target", targetName))));
                            }

                            return 1;
                        })
                );

        BrigadierCommand brigadierCommand = new BrigadierCommand(node);

        var metaBuilder = commandManager.metaBuilder(mainName).plugin(plugin);
        for (int i = 1; i < nameArray.length; i++) {
            metaBuilder.aliases(nameArray[i]);
        }

        commandManager.register(metaBuilder.build(), brigadierCommand);
    }
}