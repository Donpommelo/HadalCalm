package com.mygdx.hadal.server;

import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.states.PlayState;

public class PacketSenderServer extends PacketSender {

    @Override
    public void serverTCPAll(PlayState state, Object p, Server server) {
        if (null != server) {
            Lobby lobby = ((PlayStateServer) state).getLobby();
            for (Integer clientID : lobby.getUserManager().getUsers().keys()) {
                server.sendToTCP(clientID, p);
            }
        }
    }

    @Override
    public void serverUDPAll(PlayState state, Object p, Server server) {
        if (null != server) {
            Lobby lobby = ((PlayStateServer) state).getLobby();
            for (Integer clientID : lobby.getUserManager().getUsers().keys()) {
                server.sendToUDP(clientID, p);
            }
        }
    }

    @Override
    public void serverTCPAllExcept(PlayState state, int connID, Object p, Server server) {
        if (null != server) {
            Lobby lobby = ((PlayStateServer) state).getLobby();
            for (Integer clientID : lobby.getUserManager().getUsers().keys()) {
                if (connID != clientID) {
                    server.sendToTCP(clientID, p);
                }
            }
        }
    }

    @Override
    public void serverUDPAllExcept(PlayState state, int connID, Object p, Server server) {
        if (null != server) {
            Lobby lobby = ((PlayStateServer) state).getLobby();
            for (Integer clientID : lobby.getUserManager().getUsers().keys()) {
                if (connID != clientID) {
                    server.sendToUDP(clientID, p);
                }
            }
        }
    }
}
