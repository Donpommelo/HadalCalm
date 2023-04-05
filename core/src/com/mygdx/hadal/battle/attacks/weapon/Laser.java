package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Laser extends SyncedAttacker {

    public static final int PROJECTILE_WIDTH = 40;
    private static final int PROJECTILE_HEIGHT = 30;
    public static final float LIFESPAN = 0.25f;
    public static final float BASE_DAMAGE = 24.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 12.0f;

    private static final Vector2 TRAIL_SIZE = new Vector2(30, 30);
    private static final float TRAIL_SPEED = 120.0f;
    private static final float TRAIL_LIFESPAN = 3.0f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_BEAM;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        float distance = PROJECTILE_WIDTH;
        if (extraFields.length >= 1) {
            distance = extraFields[0];
        }
        SoundEffect.LASER2.playSourced(state, startPosition, 0.8f);
        user.recoil(startVelocity, RECOIL);

        //Create Hitbox from position to wall using raycast distance. Set angle and position of hitbox and make it static.
        Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(distance * PPM, PROJECTILE_HEIGHT), LIFESPAN,
                startVelocity, user.getHitboxFilter(),true, true, user, PROJ_SPRITE) {

            private final Vector2 newPosition = new Vector2();
            @Override
            public void create() {
                super.create();

                //this makes the laser hbox's lifespan unmodifiable
                setLifeSpan(LIFESPAN);

                //Rotate hitbox to match angle of fire.
                float newAngle = MathUtils.atan2(startVelocity.y , startVelocity.x);
                newPosition.set(getPosition()).add(new Vector2(startVelocity).nor().scl(size.x / 2 / PPM));
                setTransform(newPosition.x, newPosition.y, newAngle);
            }
        };
        hbox.setEffectsVisual(false);
        hbox.setEffectsMovement(false);
        hbox.setPositionBasedOnUser(true);
        hbox.makeUnreflectable();

        hbox.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.LASER_RIFLE,
                DamageTag.ENERGY, DamageTag.RANGED).setConstantKnockback(true, startVelocity));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setDrawOnSelf(false).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.6f, true).setSynced(false));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));

        //the trail creates particles along the projectile's length
        Hitbox trail = new RangedHitbox(state, user.getPixelPosition(), TRAIL_SIZE, TRAIL_LIFESPAN, startVelocity.nor().scl(TRAIL_SPEED),
                user.getHitboxFilter(), true, true, user, Sprite.NOTHING);
        trail.setSyncDefault(false);
        trail.setEffectsHit(false);
        trail.setEffectsMovement(false);
        trail.makeUnreflectable();

        trail.setPassability((short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

        trail.addStrategy(new ControllerDefault(state, trail, user.getBodyData()));
        trail.addStrategy(new TravelDistanceDie(state, trail, user.getBodyData(), distance));
        trail.addStrategy(new CreateParticles(state, trail, user.getBodyData(), Particle.LASER_TRAIL, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));
        if (!state.isServer()) {
            ((ClientState) state).addEntity(trail.getEntityID(), trail, false, ClientState.ObjectLayer.EFFECT);
        }
        return hbox;
    }
}