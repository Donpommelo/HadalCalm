package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 *
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 *
 * Fields: teamIndex: int index of the team that is trying to score by bringing enemy candy to this event
 *
 */
public class TrickorTreatBucket extends Event {

    private final static float PARTICLE_DURATION = 5.0f;

    //index of the team whose flag this spawns
    private final int teamIndex;

    public TrickorTreatBucket(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    @Override
    public void create() {
        this.eventData = new InteractableEventData(this);

        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
            Constants.BIT_SENSOR, Constants.BIT_PLAYER, (short) 0, true, eventData);
        this.body.setType(BodyDef.BodyType.KinematicBody);
    }

    public int getTeamIndex() { return teamIndex; }

    @Override
    public void loadDefaultProperties() {
        setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
        setSyncType(eventSyncTypes.ALL);
        addAmbientParticle(Particle.RING);
    }
}
