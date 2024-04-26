package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Nebula extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(80, 80);
    private static final Vector2 PULSE_SIZE = new Vector2(160, 160);
    private static final float KNOCKBACK = 0.0f;
    public static final float BASE_DAMAGE = 18.0f;

    public static final float MAX_RANGE = 420.0f;
    public static final float MAX_RANGE_GUTTER = 440.0f;
    public static final float SPEED_FAST = 30.0f;
    public static final float SPEED_SLOW = 16.0f;
    public static final float SPEED_INTERVAL = 1.2f;
    public static final float RECHARGE_SPEED_MULTIPLIER = 2.5f;
    public static final float MIN_RETRACT_AGE = 0.5f;

    private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Hitbox nebula = new Hitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, 0, new Vector2(), user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        nebula.makeUnreflectable();
        nebula.setSynced(true);
        nebula.setSyncedDelete(true);

        nebula.addStrategy(new CreateParticles(state, nebula, user.getBodyData(), Particle.NEBULA, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC).setRotate(true).setParticleSize(50));

        nebula.addStrategy(new DieParticles(state, nebula, user.getBodyData(), Particle.NEBULA_DESPAWN)
                .setSyncType(SyncType.NOSYNC).setParticleDuration(3.0f));
        nebula.addStrategy(new HitboxStrategy(state, nebula, user.getBodyData()) {

            private float hboxAge;
            private float homingSpeed = SPEED_FAST;
            private final Vector2 userPosition = new Vector2();
            private final Vector2 hboxPosition = new Vector2();
            private final Vector2 homeDifference = new Vector2();
            private final Vector2 positionDifference = new Vector2();
            @Override
            public void controller(float delta) {
                if (!user.isAlive()) { hbox.die(); }

                float distance;
                if (user instanceof Player player && state.isServer()) {
                    userPosition.set(user.getPosition());
                    hboxPosition.set(hbox.getPosition());
                    positionDifference.set(hboxPosition).sub(userPosition).scl(1 / PPM);

                    homeDifference.set(player.getMouseHelper().getPosition()).sub(hboxPosition);

                    boolean retract, gutter = false;
                    distance = hboxPosition.dst2(userPosition);
                    hboxAge += delta;
                    if (distance > MAX_RANGE) {
                        retract = true;
                        if (distance < MAX_RANGE_GUTTER && player.getShootHelper().isShooting()) {
                            gutter = true;
                        }
                    } else if (hboxAge < MIN_RETRACT_AGE) {
                        retract = false;
                    } else {
                        retract = !player.getShootHelper().isShooting();
                    }

                    if (retract) {
                        if (gutter) {
                            hbox.setLinearVelocity(0, 0);
                        } else {
                            homingSpeed = Math.min(SPEED_FAST, homingSpeed + delta * RECHARGE_SPEED_MULTIPLIER *
                                    (SPEED_FAST - SPEED_SLOW) / SPEED_INTERVAL);
                            hbox.setLinearVelocity(userPosition.sub(hboxPosition).nor().scl(homingSpeed));
                        }
                    } else {
                        homingSpeed = Math.max(SPEED_SLOW, homingSpeed - delta * (SPEED_FAST - SPEED_SLOW) / SPEED_INTERVAL);
                        homeDifference.nor().scl(homingSpeed);
                        hbox.setLinearVelocity(homeDifference);
                    }
                }
            }

            @Override
            public void die() {
                if (hbox.getState().isServer()) {
                    hbox.queueDeletion();
                } else {
                    hbox.setAlive(false);
                    ((ClientState) state).removeEntity(hbox.getEntityID());
                }
            }
        });
        nebula.addStrategy(new DamagePulse(state, nebula, user.getBodyData(), PULSE_SIZE, BASE_DAMAGE, KNOCKBACK,
                DamageSource.NEBULIZER, DamageTag.RANGED));

        if (user instanceof Player player) {
            player.getSpecialWeaponHelper().setDeathOrbHbox(nebula);
        }

        return nebula;
    }
}