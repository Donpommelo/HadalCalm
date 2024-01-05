package com.mygdx.hadal.constants;

public class BodyConstants {
    //Body passability types
    public static final short BIT_WALL = 1;
    public static final short BIT_PLAYER = 2;
    public static final short BIT_SENSOR = 4;
    public static final short BIT_PROJECTILE = 8;
    public static final short BIT_ENEMY = 16;
    public static final short BIT_DROPTHROUGHWALL = 32;
    public static final short BIT_PICKUP_RADIUS = 64;

    //Hitbox Filters
    public static final short PLAYER_HITBOX = -1;
    public static final short ENEMY_HITBOX = -2;

    public static final int POLYGON = 1;
    public static final int CIRCLE = 2;

}
