package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

import static com.mygdx.hadal.constants.Constants.PPM;

public class MeridianMakerProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final float LIFESPAN = 6.0f;
    public static final float PROJECTILE_SPEED = 30.0f;
    public static final float BASE_DAMAGE = 45.0f;
    private static final float KNOCKBACK = 0.0f;
    private static final int CURRENT_RADIUS = 100;
    private static final float CURRENT_FORCE = 1.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.MAGIC11_WEIRD.playSourced(state, user.getPixelPosition(), 0.5f);

        final Vector2 currentVec = new Vector2(startVelocity).nor().scl(CURRENT_FORCE);

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE, LIFESPAN,
                new Vector2(startVelocity).nor().scl(PROJECTILE_SPEED), user.getHitboxFilter(), false, false, user, PROJ_SPRITE);

        hbox.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MERIDIAN_MAKER, DamageTag.MAGIC));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BRIGHT)
                .setParticleColor(HadalColor.CELESTE)
                .setParticleSize(20));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 lastPosition = new Vector2(hbox.getStartPos()).scl(PPM);

            @Override
            public void controller(float delta) {
                if (lastPosition.dst2(hbox.getPixelPosition()) > CURRENT_RADIUS * CURRENT_RADIUS) {
                    Currents current = new Currents(state, lastPosition.set(hbox.getPixelPosition()), new Vector2(CURRENT_RADIUS, CURRENT_RADIUS),
                            currentVec, LIFESPAN);

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(current.getEntityID(), current, false, ObjectLayer.EFFECT);
                    }
                }
            }
        });

        return hbox;
    }
}