package com.mygdx.hadal.managers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.server.PacketSender;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class PacketManager {

    private static Client client;
    private static Server server;

    private static PacketSender sender;

    public static void clientPackets(Client client, PacketSender sender) {
        Kryo kryo = client.getKryo();
        Packets.allPackets(kryo);

        PacketManager.client = client;
        PacketManager.sender = sender;
    }

    public static void serverPackets(Server server, PacketSender sender) {
        Kryo kryo = server.getKryo();
        Packets.allPackets(kryo);

        PacketManager.server = server;
        PacketManager.sender = sender;
    }

    public static void clientTCP(Object p) {
        sender.clientTCP(p, client);
    }

    public static void clientUDP(Object p) {
        sender.clientUDP(p, client);
    }

    public static void serverTCP(int connID, Object p) {
        sender.serverTCP(connID, p, server);
    }

    public static void serverUDP(int connID, Object p) {
        sender.serverUDP(connID, p, server);
    }

    public static void serverTCPAll(PlayState state, Object p) {
        sender.serverTCPAll(state, p, server);
    }

    public static void serverUDPAll(PlayState state, Object p) {
        sender.serverUDPAll(state, p, server);
    }

    public static void serverTCPAllExcept(PlayState state, int connID, Object p) { sender.serverTCPAllExcept(state, connID, p, server); }

    public static void serverUDPAllExcept(PlayState state, int connID, Object p) { sender.serverUDPAllExcept(state, connID, p, server); }

    public static Client getClient() { return client; }

    public static void setClient(Client client) { PacketManager.client = client; }
}
