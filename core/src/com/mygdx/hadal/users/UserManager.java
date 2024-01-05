package com.mygdx.hadal.users;

import com.badlogic.gdx.utils.ObjectMap;

public class UserManager {

    //These keep track of all connected users maped to their connection id. Host = 0.
    private final ObjectMap<Integer, User> users = new ObjectMap<>();

    //this is the player's own connID
    private int connID;

    /**
     * This returns the number of non-spectator, non-bot players. used to determine boss hp scaling.
     */
    public int getNumPlayers() {
        int playerNum = 0;

        for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
            if (!conn.value.isSpectator() && 0.0f <= conn.key) {
                playerNum++;
            }
        }
        return playerNum;
    }

    public void resetUsers() {
        users.clear();
    }

    public User getOwnUser() {
        return users.get(connID);
    }

    public ObjectMap<Integer, User> getUsers() { return users; }

    public int getConnID() { return connID; }

    public void setConnID(int connID) { this.connID = connID; }
}
