package com.mygdx.hadal.utils;

import com.mygdx.hadal.HadalGame;

import java.util.LinkedList;
import java.util.Queue;

public class UUIDUtil {


    private static final int MAX_PLAY_STATE_ID = 15;        // 4 bits for play state ID (0 to 15)

    private static int playStateID = 0;

    private static final Queue<Integer> availableUnsyncedIDs = new LinkedList<>();

    private static int currentSyncedID = 0;
    private static int currentUnsyncedID = 0;

    // Reset counters when transitioning to a new level
    public static void nextPlayState() {
        playStateID = (playStateID + 1) & MAX_PLAY_STATE_ID; // Cycle through 0–15
        availableUnsyncedIDs.clear();
        currentSyncedID = 0;
        currentUnsyncedID = 0;
    }

    // Generates a new ID for an object
    public static int generateSyncedID() {
        currentSyncedID = (currentSyncedID + 1) & ((1 << 27) - 1);
        return (playStateID << 27) | (currentSyncedID);
    }

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

    public static void releaseUnsyncedID(int unsyncedID) {
        if (unsyncedID < 0) {
            int connID = (unsyncedID >>> 25) & 0b111111;
            if (connID == HadalGame.usm.getConnID()) {
                availableUnsyncedIDs.offer(unsyncedID);
            }
        }
    }
}
