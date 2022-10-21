package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.TrickOrTreating;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 *
 * @author Matannia Muchnold
 */
public class TrickorTreatCandy extends Event {

    private static final Vector2 CANDY_SIZE = new Vector2(80, 80);
    private static final int SPREAD = 90;
    private static final float VELO_AMP = 7.5f;
    private static final float LIFESPAN = 240.0f;

    //short delay before scrap can be picked up
    private static final float PRIME_CD = 0.5f;

    private final Vector2 startVelo = new Vector2(0, 1);

    public TrickorTreatCandy(PlayState state, Vector2 startPos) {
        super(state, startPos, CANDY_SIZE, LIFESPAN);

        setEventSprite(Sprite.NASU);
        setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
        setGravity(1.0f);
        addAmbientParticle(Particle.SPARKLE);
        setSynced(true);
    }

    private final Vector2 newVelocity = new Vector2();
    @Override
    public void create() {

        this.eventData = new EventData(this) {

            @Override
            public void onTouch(HadalData fixB) {
                if (isAlive() && fixB instanceof PlayerBodyData playerData && delay <= 0) {
                    event.queueDeletion();

                    //in single player, cansy gives the player 1 candy
                    TrickOrTreating candyStatus = (TrickOrTreating) playerData.getStatus(TrickOrTreating.class);
                    if (null != candyStatus) {
                        candyStatus.incrementCandyCount();
                    }

                    new ParticleEntity(state, fixB.getEntity(), Particle.SPARKLE, 1.0f, 1.0f, true, SyncType.CREATESYNC);

                    //activate effects that activate upon picking up scrap
                    SoundEffect.COIN3.playExclusive(state, getPixelPosition(), playerData.getPlayer(), 1.0f, false);
                }
            }
        };

        this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, true,
                Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_SENSOR), (short) 0, true, eventData);
        FixtureBuilder.createFixtureDef(body, new Vector2(), size, false, 0, 0, 0.0f, 1.0f,
                Constants.BIT_SENSOR, (short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL), (short) 0);

        float newDegrees = startVelo.angleDeg() + MathUtils.random(-SPREAD, SPREAD + 1);
        newVelocity.set(startVelo);
        setLinearVelocity(newVelocity.nor().scl(VELO_AMP).setAngleDeg(newDegrees));
    }

    private float delay = PRIME_CD;
    @Override
    public void controller(float delta) {
        if (delay >= 0) {
            delay -= delta;
        }


    }
}
