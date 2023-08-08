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

public class DeathOrbProjectile extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(120, 120);
    private static final float KNOCKBACK = 0.0f;
    private static final float SPIN_SPEED = 8.0f;
    public static final float BASE_DAMAGE = 8.5f;
    public static final float SPIN_INTERVAL = 0.017f;

    public static final float MIN_RANGE = 10.0f;
    public static final float MAX_RANGE = 400.0f;
    public static final float MIN_CHASE_RANGE = 1.0f;
    public static final float SPEED_RETRACT = 18.0f;
    public static final float SPEED_CHASE = 9.0f;

    private static final Sprite PROJ_SPRITE = Sprite.BUZZSAW;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Hitbox hbox = new Hitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, 0, new Vector2(), user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.makeUnreflectable();

        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float controllerCount;
            @Override
            public void create() { hbox.setAngularVelocity(SPIN_SPEED); }

            private final Vector2 pulseVelocity = new Vector2();
            private final Vector2 userPosition = new Vector2();
            private final Vector2 hboxPosition = new Vector2();
            @Override
            public void controller(float delta) {

                if (!user.isAlive()) { hbox.die(); }

                if (user instanceof Player player) {
                    userPosition.set(user.getPosition());
                    hboxPosition.set(hbox.getPosition());

                    boolean retract;
                    float distance = hboxPosition.dst2(userPosition);
                    if (distance > MAX_RANGE) {
                        retract = true;
                    } else {
                        retract = !player.getShootHelper().isShooting();
                    }
                    if (retract) {
                        hbox.setLinearVelocity(userPosition.sub(hboxPosition).nor().scl(SPEED_RETRACT));
                        if (distance < MIN_RANGE) {
                            hbox.die();
                        }
                    } else if (player.getMouseHelper().getPosition().dst2(hboxPosition) > MIN_CHASE_RANGE) {
                        hbox.setLinearVelocity(player.getMouseHelper().getPosition().sub(hboxPosition).nor().scl(SPEED_CHASE));
                    }  else {
                        hbox.setLinearVelocity(0, 0);
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
            player.getSpecialWeaponHelper().setDeathOrbHbox(hbox);
        }

        return hbox;
    }
}