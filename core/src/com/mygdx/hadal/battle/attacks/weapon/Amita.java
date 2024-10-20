package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Amita extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(72, 72);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 8.8f;
    private static final float RECOIL = 6.0f;
    private static final float KNOCKBACK = 13.0f;

    public static final int NUM_ORBITALS = 8;
    private static final Vector2 ORBITAL_SIZE = new Vector2(24, 24);
    private static final float ORBITAL_RANGE = 0.8f;
    private static final float ORBITAL_SPEED = 720.0f;
    private static final float ACTIVATED_SPEED = 40.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_ORANGE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ELECTRIC_CHAIN.playSourced(state, startPosition, 0.4f);
        user.recoil(startVelocity, RECOIL);

        //we create an invisible hitbox that moves in a straight line.
        Hitbox center = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        center.setEffectsHit(false);
        center.setEffectsVisual(false);

        center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
        center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
        center.addStrategy(new DieSound(state, center, user.getBodyData(), SoundEffect.MAGIC3_BURST, 0.5f).setSynced(false));
        center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {

            private final Vector2 angle = new Vector2(0, ORBITAL_RANGE);
            @Override
            public void create() {
                for (int i = 0; i < NUM_ORBITALS; i++) {
                    angle.setAngleDeg(angle.angleDeg() + 360.0f / NUM_ORBITALS);

                    //we create several orbiting projectiles that circle the invisible center
                    //when the center hits a wall, the orbitals move outwards
                    Hitbox orbital = new RangedHitbox(state, startPosition, ORBITAL_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                            true, true, user, PROJ_SPRITE);
                    orbital.setSyncDefault(false);
                    orbital.setEffectsMovement(false);

                    orbital.addStrategy(new ControllerDefault(state, orbital, user.getBodyData()));
                    orbital.addStrategy(new DamageStandard(state, orbital, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                            DamageSource.AMITA_CANNON, DamageTag.RANGED).setRepeatable(true));
                    orbital.addStrategy(new ContactWallDie(state, orbital, user.getBodyData()));
                    orbital.addStrategy(new DieParticles(state, orbital, user.getBodyData(), Particle.ORB_IMPACT).setSyncType(SyncType.NOSYNC));
                    orbital.addStrategy(new HitboxStrategy(state, orbital, user.getBodyData()) {

                        private final Vector2 centerPos = new Vector2();
                        private final Vector2 offset = new Vector2();
                        private float currentAngle = angle.angleDeg();
                        private boolean activated = false;
                        private float controllerCount;
                        private static final float pushInterval = 1 / 60f;
                        @Override
                        public void controller(float delta) {
                            controllerCount += delta;
                            while (controllerCount >= pushInterval) {
                                controllerCount -= pushInterval;

                                if (center.getBody() != null && center.isAlive()) {
                                    currentAngle += ORBITAL_SPEED * delta;

                                    centerPos.set(center.getPosition());
                                    offset.set(0, ORBITAL_RANGE).setAngleDeg(currentAngle);
                                    orbital.setTransform(centerPos.add(offset), orbital.getAngle());
                                } else if (!activated) {
                                    activated = true;
                                    hbox.setLinearVelocity(new Vector2(0, ACTIVATED_SPEED).setAngleDeg(currentAngle));
                                    hbox.setLifeSpan(LIFESPAN);
                                }
                            }
                        }
                    });

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(orbital.getEntityID(), orbital, false, ObjectLayer.HBOX);
                    }
                }
            }
        });
        return center;
    }
}