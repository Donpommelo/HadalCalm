package com.mygdx.hadal.server;

import com.mygdx.hadal.effects.ParticleColor;

public enum AlignmentFilter {

    NONE(-3, ParticleColor.NOTHING),
    PLAYER1(-4),
    PLAYER2(-5),
    PLAYER3(-6),
    PLAYER4(-7),
    PLAYER5(-8),
    PLAYER6(-9),
    PLAYER7(-10),
    PLAYER8(-11),

    TEAM_CHARTREUSE(-13, ParticleColor.CHARTREUSE),
    TEAM_PLUM(-14, ParticleColor.PLUM),
    TEAM_ORANGE(-14, ParticleColor.ORANGE),
    TEAM_RED(-12, ParticleColor.RED),
    TEAM4_SKY_BLUE(-15, ParticleColor.SKY_BLUE),

    ;

    private final short filter;
    private final boolean team;
    private final ParticleColor color;
    private boolean used;

    AlignmentFilter(int filter) {
        this.filter = (short) filter;
        this.team = false;
        this.color = ParticleColor.NOTHING;
    }

    AlignmentFilter(int filter, ParticleColor color) {
        this.filter = (short) filter;
        this.team = true;
        this.color = color;
    }

    public static AlignmentFilter getUnusedAlignment() {
        for (AlignmentFilter filter: AlignmentFilter.values()) {
            if (!filter.isUsed() && !filter.isTeam()) {
                filter.setUsed(true);
                return filter;
            }
        }
        return NONE;
    }

    public short getFilter() { return filter; }

    public boolean isTeam() { return team; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }

    public ParticleColor getColor() { return color; }
}
