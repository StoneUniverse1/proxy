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

public class PlayerInfoCommand implements JCommand {

    private final StoneUniverse plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerInfoCommand(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        var commandManager = plugin.getServer().getCommandManager();
        String[] nameArray = names.toArray(new String[0]);
        String mainName = nameArray[0];

        LiteralArgumentBuilder<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal(mainName)
                .requires(commandSource -> commandSource.hasPermission("stoneuniverse.commands.playerinfo.use"))
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

                                String uuid = player.getUniqueId().toString();

                                ProtocolVersion protocolVersion = player.getProtocolVersion();
                                String protocolId = String.valueOf(protocolVersion.getProtocol());
                                String version = protocolVersion.getMostRecentSupportedVersion();

                                String clientBrand = player.getClientBrand();
                                if (clientBrand == null) {
                                    clientBrand = "Vanilla";
                                }

                                // TODO: onlineTime
                                String onlineTime = "TODO";

                                var curServer = player.getCurrentServer();
                                String server = curServer.isPresent() ? curServer.get().getServerInfo().getName() : "N/A";

                                String ping = String.valueOf(player.getPing());
                                String ip = player.getRemoteAddress().getAddress().getHostAddress();


                                List<String> altNames = new ArrayList<>();
                                for (Player onlinePlayer : plugin.getServer().getAllPlayers()) {
                                    String onlineIp = onlinePlayer.getRemoteAddress().getAddress().getHostAddress();
                                    if (onlineIp.equals(ip)) {
                                        altNames.add(onlinePlayer.getUsername());
                                    }
                                }

                                String altsFormatted;
                                if (altNames.isEmpty()) {
                                    altsFormatted = "Keine Alts Gefunden";
                                } else {
                                    List<String> formattedAlts = new ArrayList<>();
                                    for (String alt : altNames) {
                                        formattedAlts.add("<click:run_command:/playerinfo " + alt + "><hover:show_text:'<dark_gray>╎</dark_gray> <gray>ᴋʟɪᴄᴋᴇ ꜰüʀ ɪɴꜰᴏꜱ üʙᴇʀ</gray> <yellow>" + alt + "</yellow>'><yellow>" + alt + "</yellow></click>");
                                    }
                                    altsFormatted = String.join("<dark_gray>, </dark_gray>", formattedAlts);
                                }

                                Map<String, String> placeholders = Map.ofEntries(
                                        Map.entry("player", player.getUsername()),
                                        Map.entry("uuid", uuid),
                                        Map.entry("client", clientBrand),
                                        Map.entry("version", version),
                                        Map.entry("protocol", protocolId),
                                        Map.entry("online_time", onlineTime),
                                        Map.entry("server", server),
                                        Map.entry("ping", ping),
                                        Map.entry("ip", ip),
                                        Map.entry("alts", altsFormatted)
                                );
                                List<String> lines = plugin.getBootstrap().getLang().formatList("commands.playerinfo.success", placeholders);

                                String message = String.join("<newline>", lines);
                                source.sendMessage(mm.deserialize(message));

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