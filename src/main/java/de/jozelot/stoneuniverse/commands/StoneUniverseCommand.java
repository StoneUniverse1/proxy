package de.jozelot.stoneuniverse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.jozelot.stoneuniverse.StoneUniverse;
import de.jozelot.stoneuniverse.interfaces.JCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class StoneUniverseCommand implements JCommand {
    private final StoneUniverse plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public StoneUniverseCommand(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Collection<String> names) {
        if (names == null || names.isEmpty()) return;

        var commandManager = plugin.getServer().getCommandManager();
        String[] nameArray = names.toArray(new String[0]);
        String mainName = nameArray[0];

        LiteralArgumentBuilder<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal(mainName)
                .requires(source -> source.hasPermission("stoneuniverse.admin"))
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .executes(ctx -> {
                            plugin.getBootstrap().reload();

                            ctx.getSource().sendMessage(mm.deserialize(plugin.getBootstrap().getLang().format("commands.stoneuniverse.reload.success", null)));
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
