package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This event spawns a naval-mine-football for the football game mode.
 * When the mine is destroyed, another will be spawned
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields: N/A
 *
 * @author Bacott Brembino
 */
public class FootballSpawner extends Event {

    private static final float LIFESPAN = 180.0f;
    private final static float PARTICLE_DURATION = 5.0f;

    public FootballSpawner(PlayState state, Vector2 startPos, Vector2 size) {
        super(state, startPos, size);
    }

    @Override
    public void create() {
        this.eventData = new EventData(this);

        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .addToWorld(world);
    }

    private static final float SPAWN_DELAY = 2.5f;
    private Hitbox ball;
    private float spawnCountdown;
    @Override
    public void controller(float delta) {

        //ball is spawned after a set delay
        if (spawnCountdown > 0.0f) {
            spawnCountdown -= delta;
            if (spawnCountdown <= 0.0f) {
                spawnBall();
            }
        } else {

            //spawn a ball if it is dead or nonexistent
            boolean ballded = false;
            if (ball == null) {
                ballded = true;
            } else if (!ball.isAlive()) {
                ballded = true;
            }

            if (ballded) {
                spawnCountdown = SPAWN_DELAY;
            }
        }
    }

    private static final float PUSH_MULTIPLIER = 0.6f;
    /**
     * Spawn a ball at our current location and set the objective marker to track the ball
     */
    private void spawnBall() {
        new ParticleEntity(state, this, Particle.DIATOM_IMPACT_LARGE, 0, PARTICLE_DURATION,true, SyncType.CREATESYNC);

        ball = SyncedAttack.NAUTICAL_MINE.initiateSyncedAttackSingle(state, state.getWorldDummy(), getPixelPosition(), new Vector2(),
                0.0f, PUSH_MULTIPLIER, LIFESPAN);

        EventUtils.setObjectiveMarker(state, ball, Sprite.CLEAR_CIRCLE_ALERT, HadalColor.NOTHING,true, false, false);
    }

    @Override
    public void loadDefaultProperties() {
        setSyncType(eventSyncTypes.ALL);
    }
}
