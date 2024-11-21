package com.mygdx.hadal.utils;

import com.mygdx.hadal.HadalGame;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The UUIDUtil handles the setting of uuids for newly created entities.
 * This exists so we can shrink sync packets by using an int instead of uuids serialized as 2 longs.
 * Server-created: Positive int. The first 4 bits are a "playstate id" to avoid clients receiving old state's packets
 * Client-created: First bit makes it negative. Next 6 are used for the connID of the client.
 * When an object is created through receiving a create packet, the sender decides the uuid.
 * Because of this, clients need to include connID to avoid collision
 */
public class UUIDUtil {

    private static final int MAX_PLAY_STATE_ID = 15;        // 4 bits for play state ID (0 to 15)

    //Server playstate id. Cycles between 0-15 and prevents clients from processing delete/sync packets from old playstate.
    private static int playStateID = 0;

    //pool of client UUIDs that are freed as entities are deleted
    private static final Queue<Integer> availableUnsyncedIDs = new LinkedList<>();

    private static int currentSyncedID = 0;
    private static int currentUnsyncedID = 0;

    // Reset counters when transitioning to a new level
    public static void nextPlayState() {
        playStateID = (playStateID + 1) & MAX_PLAY_STATE_ID;
        availableUnsyncedIDs.clear();
        currentSyncedID = 0;
        currentUnsyncedID = 0;
    }

    /**
     * Generates a new ID for an server-created object using the playstate id and an incrementing entity id
     */
    public static int generateSyncedID() {
        currentSyncedID = (currentSyncedID + 1) & ((1 << 27) - 1);
        return (playStateID << 27) | (currentSyncedID);
    }

    /**
     * Generates a new ID for a client-created object using the client's connection id
     */
    public static int generateUnsyncedID() {
        int newID;
        if (availableUnsyncedIDs.isEmpty()) {

            //make sure connID is within 6 bits
            int connID = HadalGame.usm.getConnID() & 0b111111;

            //make sure entityID is within 25 bits
            currentUnsyncedID = (currentUnsyncedID + 1) & 0x01FFFFFF;

            //combine bits and make negative
            newID = (1 << 31) | (connID << 25) | (currentUnsyncedID);
        } else {
            newID = availableUnsyncedIDs.poll();
        }
        return newID;
    }

    /**
     * For clients, ids are released when the entity is deleted.
     * We only want to release ids we created ourselves; NOT ids sent to us by server or other clients
     */
    public static void releaseUnsyncedID(int unsyncedID) {

        //negative ID means it was set by a client; not the server
        if (unsyncedID < 0) {

            //we extract the connID, because we only want to release it if it is our own id
            int connID = (unsyncedID >>> 25) & 0b111111;
            if (connID == HadalGame.usm.getConnID()) {
                availableUnsyncedIDs.offer(unsyncedID);
            }
        }
    }
}
