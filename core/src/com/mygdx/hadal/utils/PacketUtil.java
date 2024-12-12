package com.mygdx.hadal.utils;

/**
 * The PacketUtil contains some function used to process packets sent between the client and server.
 * The purpose of these is usually to reduce the size of commonly-sent sync packets
 * @author Nerzcourt Nortour
 */
public class PacketUtil {

    private static final float SCALE_FACTOR = 10.0f;

    /**
     * These 2 functions convert a float (for position or velocity vector) into shorts and back to float
     * This is done for serialization purposes for syncing objects to save bytes
     * Note: this should never be used for values that, when multiplied by SCALE_FACTOR, are too big for a short
     */
    public static short floatToShort(float value) {
        return (short) (value * SCALE_FACTOR);
    }

    public static float shortToFloat(short value) {
        return value / SCALE_FACTOR;
    }

    /**
     * These 2 functions convert a degree angle into a byte and back to a float
     * This is done for serialization purposes for syncing objects to save bytes
     */
    public static byte angleToByte(float angle) {
        return (byte) (angle / 360.0f * 255);
    }

    public static float byteToAngle(byte angleByte) {
        return (angleByte & 0xFF) / 255.0f * 360.0f;
    }

    /**
     * This converts a float percentage (0.0 to 1.0) into a byte that can be converted back by the recipient.
     * The returned byte will be between 0 and 127.
     * A percentage of -1.0f corresponds to -128; this is a flag that is used for specific cases
     * (player is not reloading/charging as opposed to having 0% for those meters)
     * This is used for percentages that don't need to be exactly precise for ui display purposes (fuel/charge for other players)
     */
    public static byte percentToByte(float percent) {
        if (percent < 0) {
            return -128;
        }
        if (percent > 1) {
            return (byte) Math.round(127.0f);
        }
        return (byte) Math.round(percent * 127.0f);
    }

    /**
     * This converts a received byte to a float percentage (0.0 to 1.0).
     */
    public static float byteToPercent(byte packetByte) {
        if (packetByte == -128) {
            return -1.0f;
        }
        return (packetByte & 0xFF) / 127.0f;
    }
}
