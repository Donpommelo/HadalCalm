package com.mygdx.hadal.users;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;

/**
 * The UserManager is created when the game begins and maintains a list of users connected to the game.
 * This applies to both clients and host.
 * <p>
 * It also contains some functions to make it easier to query for the player's own user/player entity
 */
public class UserManager {

    //These keep track of all connected users mapped to their connection id. Host = 0.
    private final ObjectMap<Integer, User> users = new ObjectMap<>();

    //this is the player's own connID
    private int connID;

    //ID of the host, which can be a client. -1 means no host is designated and will be set when a user connects.
    private int hostID = -1;

    //This is an entity representing the player's controlled entity.
    private Player ownPlayer;

    /**
     * This returns the number of non-spectator, non-bot players. used to determine things like boss hp scaling, default bot count.
     */
    public int getNumPlayers() {
        int playerNum = 0;

        for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
            if (!conn.value.isSpectator() && conn.key >= 0.0f) {
                playerNum++;
            }
        }
        return playerNum;
    }

    public void addUser(User user) {
        users.put(user.getConnID(), user);
    }

    /**
     * When the server adds a user, we set that user as host if no host exists yet.
     */
    public void addUserServer(User user) {
        addUser(user);
        if (getHost() == null) {
            hostID = user.getConnID();
        }
    }

    public void removeUser(int connID) { users.remove(connID); }

    /**
     * When the server removes a user, check if we need to reassign host status to another user
     */
    public void removeUserServer(int connID) {
        removeUser(connID);

        int playerNum = 0;

        for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
            if (conn.key >= 0.0f) {
                playerNum++;
            }
        }

        //if only bot players are left, close the server. (human spectators keep lobby open)
        if (playerNum == 0) {
            Gdx.app.exit();
        } else if (connID == hostID) {
            //if the host disconnected, choose a new host from the non-bot users and inform clients
            for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
                if (conn.key >= 0.0f) {
                    hostID = conn.key;
                    PacketManager.serverTCPAll(new Packets.ServerNewHost(hostID));
                    break;
                }
            }
        }
    }

    public User getHost() {
        for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
            if (conn.key == hostID) {
                return conn.value;
            }
        }
        return null;
    }

    /**
     * This checks if an input user is on the same team as the player.
     * Null players (for spectators or users with players that haven't spawned yet) always return true
     */
    public boolean isOwnTeam(User user) {
        if (ownPlayer == null || user.getPlayer() == null) { return true; }
        return ownPlayer.getHitboxFilter() == user.getPlayer().getHitboxFilter();
    }

    public void resetUsers() {
        users.clear();
    }

    public User getOwnUser() {
        return users.get(connID);
    }

    public ObjectMap<Integer, User> getUsers() { return users; }

    public boolean isHost() { return connID == hostID; }

    public int getConnID() { return connID; }

    public void setConnID(int connID) { this.connID = connID; }

    public int getHostID() { return hostID; }

    public void setHostID(int hostID) { this.hostID = hostID; }

    public Player getOwnPlayer() { return ownPlayer; }

    public void setOwnPlayer(Player ownPlayer) { this.ownPlayer = ownPlayer; }
}
