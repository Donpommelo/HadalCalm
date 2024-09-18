package com.mygdx.hadal.server.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.server.Lobby;
import com.mygdx.hadal.server.PacketSenderServer;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsConnection;
import com.mygdx.hadal.states.LobbyState;
import com.mygdx.hadal.text.UIText;

import java.io.IOException;

import static com.mygdx.hadal.constants.ServerConstants.SERVER_PORT;

public class ServerManager {

    //Me server
    private static Server server;

    public static void init() {
        Kryo kryo = new Kryo() {

            @Override
            public boolean isFinal(Class type) {
                if (type.equals(Array.class) || type.equals(ObjectMap.class) || type.equals(Vector2.class) || type.equals(Vector3.class)) {
                    return true;
                } else {
                    return super.isFinal(type);
                }
            }
        };
        KryoSerialization serialization = new KryoSerialization(kryo);
        server = new Server(65536, 32768, serialization);

        Listener packetListener = new Listener() {

            @Override
            public void disconnected(final Connection c) {

            }

            @Override
            public void received(final Connection c, Object o) {
                if (o instanceof PacketsConnection.LobbyInfoRequest) {
                    Gdx.app.postRunnable(() -> PacketManager.serverTCP(c.getID(), new PacketsConnection.LobbyInfoResponse(LobbyManager.getLobbyInfo())));
                }

                else if (o instanceof PacketsConnection.CreateLobbyRequest p) {
                    Gdx.app.postRunnable(() -> {
                        if (!HadalGame.VERSION.equals(p.version)) {
                            PacketManager.serverTCP(c.getID(), new PacketsConnection.ConnectReject(UIText.INCOMPATIBLE.text(HadalGame.VERSION)));
                            return;
                        }

                        LobbyManager.addLobby(c.getID(), p.lobbyName, p.hostName, p.playerCapacity);
                    });
                }

                else if (o instanceof PacketsConnection.ConnectToLobby p) {
                    Gdx.app.postRunnable(() -> {
                        Lobby joinedLobby = LobbyManager.getLobbies().get(p.lobbyID);

                        if (null != joinedLobby) {

                            //TODO: obtain mode settings of lobby for mid-join

                            //reject clients with wrong version
                            if (!HadalGame.VERSION.equals(p.version)) {
                                PacketManager.serverTCP(c.getID(), new PacketsConnection.ConnectReject(UIText.INCOMPATIBLE.text(HadalGame.VERSION)));
                                return;
                            }

                            //if no server password, the client connects.
                            if (!"".equals(JSONManager.setting.getServerPassword())) {
                                //password being null indicates the client just attempted to connect.
                                //otherwise, we check whether the password entered matches
                                if (p.password == null) {
                                    PacketManager.serverTCP(c.getID(), new Packets.PasswordRequest());
                                    return;
                                } else if (!JSONManager.setting.getServerPassword().equals(p.password)) {
                                    PacketManager.serverTCP(c.getID(), new PacketsConnection.ConnectReject(UIText.INCORRECT_PASSWORD.text()));
                                    return;
                                }
                            }
//                            addNotificationToAllExcept(ps, c.getID(), "", UIText.CLIENT_CONNECTED.text(p.name), true, DialogBox.DialogType.SYSTEM);
                        }
                    });
                }
            }
        };

        server.addListener(packetListener);
        try {
            server.bind(SERVER_PORT, SERVER_PORT);
        } catch (IOException e) {
            if (StateManager.states.peek() instanceof LobbyState lobby) {
                lobby.setNotification(UIText.PORT_FAIL.text(Integer.toString(SERVER_PORT)));
            }
        }

        PacketManager.serverPackets(server, new PacketSenderServer());
        server.start();
    }

    public static void dispose() throws IOException {
        if (server != null) {
            server.stop();
            server.dispose();
            server = null;
        }
    }
}
