package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Flag Blocker simply prevents the "flag" event from passing through. The logic that does this is contained in the
 * flag hbox strategy which drops itself upon colliding with this event.
 *
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 *
 * Fields: N/A
 *
 * @author Dogginbotham Drurgeon
 */
public class FlagBlocker extends Event {

    //This is the team which will drop flag upon touching this (you do not drop flag touching impasseable enemy spawn)
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
