package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Flag Blocker simply prevents the "flag" event from passing through. The logic that does this is contained in the
 * flag hbox strategy which drops itself upon colliding with this event.
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
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
        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_PLAYER), (short) 0)
                .setBodyType(BodyDef.BodyType.StaticBody)
                .addToWorld(world);
    }

    public int getTeamIndex() { return teamIndex; }
}
