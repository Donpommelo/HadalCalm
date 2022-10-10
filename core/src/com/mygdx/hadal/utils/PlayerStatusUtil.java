package com.mygdx.hadal.utils;

import com.mygdx.hadal.constants.Constants;

public class PlayerStatusUtil {

    public static short statusToCode(boolean grounded, boolean running, boolean hovering, boolean reloading, boolean invisible,
                               boolean translucent, boolean tranparent) {

        return (short) ((grounded ? Constants.GROUNDED : 0) |
                (running ? Constants.RUNNING : 0) |
                (hovering ? Constants.HOVERING : 0) |
                (reloading ? Constants.RELOADING : 0) |
                (invisible ? Constants.INVISIBLE : 0) |
                (translucent ? Constants.TRANSLUCENT : 0) |
                (tranparent ? Constants.TRANSPARENT : 0));
    }

    public static boolean codeToGrounded(short code) {
        return codeToStatus(code, Constants.GROUNDED);
    }

    public static boolean codeToRunning(short code) {
        return codeToStatus(code, Constants.RUNNING);
    }

    public static boolean codeToHovering(short code) {
        return codeToStatus(code, Constants.HOVERING);
    }

    public static boolean codeToReloading(short code) {
        return codeToStatus(code, Constants.RELOADING);
    }

    public static boolean codeToInvisible(short code) {
        return codeToStatus(code, Constants.INVISIBLE);
    }

    public static boolean codeToTranslucent(short code) {
        return codeToStatus(code, Constants.TRANSLUCENT);
    }

    public static boolean codeToTransparent(short code) {
        return codeToStatus(code, Constants.TRANSPARENT);
    }

    private static boolean codeToStatus(short code, short status) {
        return code == (code | status);
    }
}
