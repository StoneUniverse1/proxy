package de.jozelot.stoneuniverse.mechanics.discordupdate;

import de.jozelot.stoneuniverse.interfaces.Packet;

import java.util.List;

public class ServerStatusPacket implements Packet{

    @Override
    public String getId() {
        return "ServerStatusPacket";
    }

    private double tps;
    private double mspt;
    private List<PlayerDto> players;

    public ServerStatusPacket(double tps, double mspt, List<PlayerDto> players) {
        this.tps = tps;
        this.mspt = mspt;
        this.players = players;
    }

    public double getTps() { return tps; }
    public double getMspt() { return mspt; }
    public List<PlayerDto> getPlayers() { return players; }

    public static class PlayerDto {
        private String uuid;
        private String name;
        private String lp_rank;
        private String lp_name;

        public PlayerDto(String uuid, String name, String lp_rank, String lp_name) {
            this.uuid = uuid;
            this.name = name;
            this.lp_rank = lp_rank;
            this.lp_name = lp_name;
        }

        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public String getLpRank() { return lp_rank; }
        public String getLpName() { return lp_name; }
    }
}