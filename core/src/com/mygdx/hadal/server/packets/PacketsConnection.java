package com.mygdx.hadal.server.packets;

import com.mygdx.hadal.server.LobbyInfo;

public class PacketsConnection {

    public static class LobbyInfoRequest {

        public LobbyInfoRequest() {}
    }

    public static class LobbyInfoResponse {
        public LobbyInfo[] lobbies;

        public LobbyInfoResponse() {}

        public LobbyInfoResponse(LobbyInfo[] lobbies) {
            this.lobbies = lobbies;
        }
    }

    public static class CreateLobbyRequest {
        public String version;
        public String lobbyName;
        public String hostName;
        public int playerCapacity;

        public CreateLobbyRequest() {}

        public CreateLobbyRequest(String version, String lobbyName, String hostName, int playerCapacity) {
            this.version = version;
            this.lobbyName = lobbyName;
            this.hostName = hostName;
            this.playerCapacity = playerCapacity;
        }
    }

    public static class ConnectToLobby {
        public int lobbyID;
        public String version;
        public String name;
        public String password;
        public ConnectToLobby() {}

        public ConnectToLobby(int lobbyID, String version, String name, String password) {
            this.lobbyID = lobbyID;
            this.version = version;
            this.name = name;
            this.password = password;
        }
    }

    public static class ConnectReject {
        public String msg;
        public ConnectReject() {}

        /**
         * ConnectReject is sent from the Server to the Client to reject a connection.
         * This is done when the server is full, or if the server is in the middle of a game.
         * @param msg: message to be displayed by the client
         */
        public ConnectReject(String msg) {
            this.msg = msg;
        }
    }
}
