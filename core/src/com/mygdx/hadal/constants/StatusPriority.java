package com.mygdx.hadal.constants;

public class StatusPriority {

    //Loch Shield activates before default to avoid poor synergy with other damage reduction, since it reduces large instances
    public static final int PRIORITY_PRE_DEFAULT = 0;

    public static final int PRIORITY_DEFAULT = 1;

    //Invincibility activates before proc so it prevents things that activate upon taking damage.
    public static final int PRIORITY_SET = 6;
    public static final int PRIORITY_PROC = 7;

    //These effects both modify and vary with incoming damage.
    public static final int PRIORITY_PRE_SCALE_FRACTURE_PLATE = 8;
    public static final int PRIORITY_PRE_SCALE_FARADAYS_CAGE = 11;

    //Effects that scale to incoming damage must go after all effects that modify final value
    public static final int PRIORITY_SCALE = 13;

    //Effects that concern fatal damage calculation (Loss of Senses, Noctilucent Promise)
    public static final int PRIORITY_FATAL_CHECK = 14;

    //Super last effects; things that set a stat value, Ancient Synapse
    public static final int PRIORITY_LAST = 15;
}
