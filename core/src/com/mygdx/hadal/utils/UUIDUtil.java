package com.mygdx.hadal.utils;

public class UUIDUtil {

    private static final int MIN_OBJECT_ID = -(1 << 28);     // -268,435,456
    private static final int MAX_OBJECT_ID = (1 << 28) - 1; // 28 bits for object ID
    private static final int MAX_PLAY_STATE_ID = 15;        // 4 bits for play state ID (0 to 15)

    private static int playStateID = 0;

    private static int currentSyncedID = 0;
    private static int currentUnsyncedID = 0;

    // Reset counters when transitioning to a new level
    public static void nextPlayState() {
        playStateID = (playStateID + 1) & MAX_PLAY_STATE_ID; // Cycle through 0–15
        currentSyncedID = 0;
        currentUnsyncedID = 0;
    }

    // Generates a new ID for an object
    public static int generateSyncedID() {
        int newID = (playStateID << 28) | (currentSyncedID & MAX_OBJECT_ID);
        currentSyncedID = (currentSyncedID + 1) & MAX_OBJECT_ID;
        return newID;
    }

    // Generates a unique negative ID for a client-only object
    public static int generateUnsyncedID() {
        int newID = MIN_OBJECT_ID - (currentUnsyncedID & MAX_OBJECT_ID);
        currentUnsyncedID = (currentUnsyncedID + 1) & MAX_OBJECT_ID;

        return newID;
    }
}
