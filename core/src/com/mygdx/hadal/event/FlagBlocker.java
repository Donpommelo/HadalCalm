package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

public class FlagBlocker extends Event {

    private final int teamIndex;

    public FlagBlocker(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this);
        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
                Constants.BIT_SENSOR, Constants.BIT_PROJECTILE, (short) 0, true, eventData);
    }

    public int getTeamIndex() { return teamIndex; }
}
