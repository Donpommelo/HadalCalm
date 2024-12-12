package com.mygdx.hadal.server.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.server.packets.Packets;

/**
 * PacketManager contains packet-sending functions for both client and server.
 */
public class PacketManager {

    private static Client client;
    private static Server server;

    public static void clientPackets(Client client) {
        Kryo kryo = client.getKryo();
        Packets.allPackets(kryo);

        PacketManager.client = client;
    }

    public static void serverPackets(Server server) {
        Kryo kryo = server.getKryo();
        Packets.allPackets(kryo);

        PacketManager.server = server;
    }

    public static void clientTCP(Object p) {
        if (null != client) {
            client.sendTCP(p);
        }
    }

    public static void clientUDP(Object p) {
        if (null != client) {
            client.sendUDP(p);
        }
    }

    public static void serverTCP(int connID, Object p) {
        if (null != server) {
            server.sendToTCP(connID, p);
        }
    }

    public static void serverUDP(int connID, Object p) {
        if (null != server) {
            server.sendToUDP(connID, p);
        }
    }

    public static void serverTCPAll(Object p) {
        if (null != server) {
            server.sendToAllTCP(p);
        }
    }

    public static void serverUDPAll(Object p) {
        if (null != server) {
            server.sendToAllUDP(p);
        }
    }

    public static void serverTCPAllExcept(int connID, Object p) {
        if (server != null) {
            server.sendToAllExceptTCP(connID, p);
        }
    }

    public static void serverUDPAllExcept(int connID, Object p) {
        if (server != null) {
            server.sendToAllExceptUDP(connID, p);
        }
    }
}
