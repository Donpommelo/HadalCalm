package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class NeptuneOrbital extends SyncedAttacker {

    private static final int NUM_ORBITALS = 8;
    private static final Vector2 PROJ_SIZE = new Vector2(80, 80);
    private static final float LIFESPAN = 5.5f;
    private static final float RANGE = 8.5f;
    private static final float BASE_DAMAGE = 12.0f;
    private static final float KNOCKBACK = 12.0f;
    private static final float SPEED = 240.0f;
    private static final float RANGE_INCREASE_SPEED = 0.1f;
    private static final float SPIN_SPEED = 0.4f;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_A;

    final Vector2 angle = new Vector2(1, 0);
    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());

        Hitbox[] hboxes = new Hitbox[NUM_ORBITALS];

        for (int i = 0; i < NUM_ORBITALS; i++) {
            angle.setAngleDeg(angle.angleDeg() + 360.0f / NUM_ORBITALS);
            RangedHitbox orbital = new RangedHitbox(state, position, PROJ_SIZE,
                    LIFESPAN, new Vector2(), user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
            orbital.makeUnreflectable();

            orbital.addStrategy(new ControllerDefault(state, orbital, user.getBodyData()));
            orbital.addStrategy(new DamageStandard(state, orbital, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                    DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setStaticKnockback(true).setRepeatable(true));
            orbital.addStrategy(new ContactUnitSound(state, orbital, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
            orbital.addStrategy(new HitboxStrategy(state, orbital, user.getBodyData()) {

                private final Vector2 centerPos = new Vector2();
                private final Vector2 offset = new Vector2();
                private float currentAngle = angle.angleDeg();
                private float controllerCount;
                private float currentRange;
                private float orbitalAngle;
                private static final float pushInterval = 1 / 60f;
                @Override
                public void controller(float delta) {
                    controllerCount += delta;
                    while (controllerCount >= pushInterval) {
                        controllerCount -= pushInterval;
                        orbitalAngle += SPIN_SPEED;
                        if (user.getBody() != null && user.isAlive()) {
                            currentAngle += SPEED * delta;

                            centerPos.set(user.getPosition());
                            offset.set(0, currentRange).setAngleDeg(currentAngle);
                            orbital.setTransform(centerPos.add(offset), orbitalAngle);

                            //orbitals start off near boss and expand outwards
                            if (currentRange < RANGE) {
                                currentRange += RANGE_INCREASE_SPEED;
                            }
                        } else {
                            die();
                        }
                    }
                }
            });

            if (i == 0) {
                orbital.addStrategy(new CreateSound(state, orbital, user.getBodyData(), SoundEffect.MAGIC25_SPELL,
                        0.8f, true).setPitch(0.5f));
            }

            hboxes[i] = orbital;
        }

        return hboxes;
    }
}