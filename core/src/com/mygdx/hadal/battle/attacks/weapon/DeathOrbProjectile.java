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
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

import static com.mygdx.hadal.constants.Constants.PPM;

public class DeathOrbProjectile extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(160, 160);
    private static final float KNOCKBACK = 0.0f;
    private static final float SPIN_SPEED = 8.0f;
    public static final float BASE_DAMAGE = 6.5f;
    public static final float SPIN_INTERVAL = 0.5f;

    public static final float MIN_RANGE = 10.0f;
    public static final float MAX_RANGE = 400.0f;
    public static final float MIN_RANGE_ROOT = 20.0f;
    public static final float MAX_LERP_RANGE = 50.0f;
    public static final float MIN_LERP_RANGE = 10.0f;
    public static final float SPEED_FAST = 18.0f;
    public static final float SPEED_SLOW = 9.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Hitbox nebula = new Hitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, 0, new Vector2(), user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        nebula.makeUnreflectable();

        nebula.addStrategy(new CreateParticles(state, nebula, user.getBodyData(), Particle.NEBULA, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC).setRotate(true).setParticleSize(80));

        nebula.addStrategy(new DieParticles(state, nebula, user.getBodyData(), Particle.NEBULA_DESPAWN)
                .setSyncType(SyncType.NOSYNC).setParticleDuration(3.0f));
        nebula.addStrategy(new HitboxStrategy(state, nebula, user.getBodyData()) {

            private float controllerCount;
            private final Vector2 homePoint = new Vector2();
            @Override
            public void create() {
                hbox.setAngularVelocity(SPIN_SPEED);
                if (extraFields.length > 1) {
                    homePoint.set(extraFields[0], extraFields[1]);
                } else {
                    homePoint.set(user.getPosition());
                }
            }

            private final Vector2 pulseVelocity = new Vector2();
            private final Vector2 userPosition = new Vector2();
            private final Vector2 hboxPosition = new Vector2();
            private final Vector2 homeDifference = new Vector2();
            private final Vector2 positionDifference = new Vector2();
            @Override
            public void controller(float delta) {

                if (!user.isAlive()) { hbox.die(); }

                float distance;
                if (user instanceof Player player) {
                    userPosition.set(user.getPosition());
                    hboxPosition.set(hbox.getPosition());
                    positionDifference.set(hboxPosition).sub(userPosition).scl(1 / PPM);

                    homeDifference.set(player.getMouseHelper().getPosition()).sub(homePoint);
                    homePoint.add(homeDifference.nor().scl(SPEED_SLOW * delta));
                    if (homePoint.dst2(userPosition) > MAX_RANGE) {
                        homeDifference.set(homePoint).sub(userPosition).nor().scl(MIN_RANGE_ROOT);
                        homePoint.set(userPosition).add(homeDifference);
                    }

                    boolean retract;
                    distance = hboxPosition.dst2(userPosition);
                    if (distance > MAX_RANGE) {
                        retract = true;
                    } else {
                        retract = !player.getShootHelper().isShooting();
                    }
                    if (retract) {
                        hbox.setLinearVelocity(userPosition.sub(hboxPosition).nor().scl(SPEED_FAST));
                        if (distance < MIN_RANGE) {
                            hbox.die();
                        }
                    } else {
                        distance = homePoint.dst2(hboxPosition);
                        if (distance > MAX_LERP_RANGE) {
                            homeDifference.set(homePoint).sub(hboxPosition).nor().scl(SPEED_FAST);
                        } else {
                            homeDifference.set(homePoint).sub(hboxPosition).nor().scl(SPEED_FAST)
                                    .scl(Math.max(0, distance - MIN_LERP_RANGE) / (MAX_LERP_RANGE - MIN_LERP_RANGE));
                        }
                        hbox.setLinearVelocity(homeDifference);
                    }
                }

                controllerCount += delta;
                while (controllerCount >= SPIN_INTERVAL) {
                    controllerCount -= SPIN_INTERVAL;

                    Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), PROJECTILE_SIZE, SPIN_INTERVAL, pulseVelocity,
                            user.getHitboxFilter(), true, true, user, Sprite.NOTHING);
                    pulse.setEffectsVisual(false);
                    pulse.makeUnreflectable();

                    pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
                    pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                            DamageSource.DIAMOND_CUTTER, DamageTag.MELEE).setStaticKnockback(true));

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(pulse.getEntityID(), pulse, false, ClientState.ObjectLayer.HBOX);
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

        if (user instanceof Player player) {
            player.getSpecialWeaponHelper().setDeathOrbHbox(nebula);
        }

        return nebula;
    }
}