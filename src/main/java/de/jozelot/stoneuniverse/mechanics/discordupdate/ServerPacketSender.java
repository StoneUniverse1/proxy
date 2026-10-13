package de.jozelot.stoneuniverse.mechanics.discordupdate;

import com.velocitypowered.api.proxy.Player;
import de.jozelot.stoneuniverse.StoneUniverse;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import redis.clients.jedis.Jedis;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerPacketSender {

    private final StoneUniverse plugin;
    private final ObjectMapper mapper = new ObjectMapper();

    public ServerPacketSender(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    public void sendStatusAsync() {
        plugin.getServer().getScheduler()
                .buildTask(plugin, this::sendStatus)
                .schedule();
    }

    public void sendStatus() {
        var server = plugin.getServer();
        double tps = 20.0;
        double mspt = 0.0;

        LuckPerms lp = LuckPermsProvider.get();
        var userManager = lp.getUserManager();
        var groupManager = lp.getGroupManager();

        List<ServerStatusPacket.PlayerDto> players = new ArrayList<>();

        for (Player player : server.getAllPlayers()) {
            UUID uuid = player.getUniqueId();
            String name = player.getUsername();

            String lpRank = "default";
            String lpName = "Default";

            try {
                User user = userManager.loadUser(uuid).join();
                if (user != null) {
                    lpRank = user.getCachedData().getMetaData().getPrimaryGroup();
                    if (lpRank == null) lpRank = "default";

                    Group group = groupManager.getGroup(lpRank);
                    if (group != null && group.getDisplayName() != null) {
                        lpName = group.getDisplayName();
                    } else {
                        lpName = lpRank;
                    }
                }
                // System.out.println("lpname: " + lpName +  " | lprank: " + lpRank);
            } catch (Exception e) {
            }

            ServerStatusPacket.PlayerDto dto = new ServerStatusPacket.PlayerDto(uuid.toString(), name, lpRank, lpName);
            players.add(dto);
        }
        sendPacket(tps, mspt, players);
    }

    private void sendPacket(double tps, double mspt, List<ServerStatusPacket.PlayerDto> players) {
        try {
            ServerStatusPacket packet = new ServerStatusPacket(tps, mspt, players);
            String jsonPayload = mapper.writeValueAsString(packet);

            try (Jedis jedis = plugin.getBootstrap().getRedisConnection().getResource()) {
                jedis.publish("network:OnlinePacket", jsonPayload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
