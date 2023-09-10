package com.mygdx.hadal.utils;

public class PacketUtil {

    public static byte percentToByte(float percent) {
        if (percent < 0) {
            return -128;
        }
        if (percent > 1) {
            return (byte) Math.round(127.0f);
        }
        return (byte) Math.round(percent * 127.0f);
    }

    public static float byteToPercent(byte packetByte) {
        if (packetByte == -128) {
            return -1.0f;
        }
        return (packetByte & 0xFF) / 127.0f;
    }
}
