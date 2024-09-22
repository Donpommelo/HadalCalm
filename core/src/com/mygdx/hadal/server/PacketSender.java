package com.mygdx.hadal.server;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.states.PlayState;

public class PacketSender {

    public void clientTCP(Object p, Client client) {
        if (null != client) {
            client.sendTCP(p);
        }
    }

    public void clientUDP(Object p, Client client) {
        if (null != client) {
            client.sendTCP(p);
        }
    }

    public void serverTCP(int connID, Object p, Server server) {
        if (null != server) {
            server.sendToTCP(connID, p);
        }
    }

    public void serverUDP(int connID, Object p, Server server) {
        if (null != server) {
            server.sendToUDP(connID, p);
        }
    }

    public void serverTCPAll(PlayState state, Object p, Server server) {
        if (null != server) {
            server.sendToAllTCP(p);
        }
    }

    public void serverUDPAll(PlayState state, Object p, Server server) {
        if (null != server) {
            server.sendToAllUDP(p);
        }
    }

    public void serverTCPAllExcept(PlayState state, int connID, Object p, Server server) {
        if (server != null) {
            server.sendToAllExceptTCP(connID, p);
        }
    }

    public void serverUDPAllExcept(PlayState state, int connID, Object p, Server server) {
        if (server != null) {
            server.sendToAllExceptUDP(connID, p);
        }
    }
}
