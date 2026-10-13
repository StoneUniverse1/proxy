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
    private int maxPlayers;
    private List<PlayerDto> players;

    public ServerStatusPacket(double tps, double mspt, List<PlayerDto> players, int maxPlayers) {
        this.tps = tps;
        this.mspt = mspt;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public double getTps() { return tps; }
    public double getMspt() { return mspt; }
    public List<PlayerDto> getPlayers() { return players; }
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public static class PlayerDto {
        private String uuid;
        private String name;
        private String lp_rank;
        private String lp_name;
        private int lp_weight;

        public PlayerDto(String uuid, String name, String lp_rank, String lp_name, int lp_weight) {
            this.uuid = uuid;
            this.name = name;
            this.lp_rank = lp_rank;
            this.lp_name = lp_name;
            this.lp_weight = lp_weight;
        }

        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public String getLpRank() { return lp_rank; }
        public String getLpName() { return lp_name; }
        public int getLpWeight() { return lp_weight; }
    }
}