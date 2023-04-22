package com.mygdx.hadal.utils;

import com.mygdx.hadal.constants.Constants;

public class PlayerConditionUtil {

    public static short conditionToCode(boolean grounded, boolean jumping, boolean running, boolean hovering, boolean invisible,
                                        boolean translucent, boolean tranparent, boolean reloading, boolean shooting, boolean typing) {

        return (short) ((grounded ? Constants.GROUNDED : 0) |
                (jumping ? Constants.JUMPING : 0) |
                (running ? Constants.RUNNING : 0) |
                (hovering ? Constants.HOVERING : 0) |
                (reloading ? Constants.RELOADING : 0) |
                (shooting ? Constants.SHOOTING : 0) |
                (invisible ? Constants.INVISIBLE : 0) |
                (translucent ? Constants.TRANSLUCENT : 0) |
                (tranparent ? Constants.TRANSPARENT : 0) |
                (typing ? Constants.TYPING : 0));
    }

    public static boolean codeToCondition(short code, short condition) { return code == (code | condition); }
}
