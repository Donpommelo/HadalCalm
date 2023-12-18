package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.b2d.HadalFixture;

public class Leapfrog extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    public static final float LIFESPAN = 50.0f;
    public static final float BASE_DAMAGE = 65.0f;
    private static final float RECOIL = 6.0f;
    private static final float KNOCKBACK = 30.0f;
    private static final float FLASH_LIFESPAN = 0.5f;

    public static final int LEAP_AMOUNT = 3;
    private static final float LEAP_COUNT = 0.4f;
    private static final float MIN_LEAP_ANGLE = 45;

    private static final Sprite PROJ_SPRITE = Sprite.CANNONBALL;

    private final Vector2 adjustedVelocity = new Vector2();
    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPRING.playSourced(state, startPosition, 0.3f);
        SoundEffect.FROG_CROAK.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setGravity(7);
        hbox.setFriction(1.0f);

        adjustedVelocity.set(startVelocity);
        if (adjustedVelocity.angleDeg() > 180) {
            if (adjustedVelocity.angleDeg() > 270) {
                adjustedVelocity.setAngleDeg(MIN_LEAP_ANGLE);
            } else {
                adjustedVelocity.setAngleDeg(180 - MIN_LEAP_ANGLE);
            }
        }

        if (adjustedVelocity.angleDeg() < MIN_LEAP_ANGLE) {
            adjustedVelocity.setAngleDeg(MIN_LEAP_ANGLE);
        }
        if (adjustedVelocity.angleDeg() < 180 && adjustedVelocity.angleDeg() > 180 - MIN_LEAP_ANGLE) {
            adjustedVelocity.setAngleDeg(180 - MIN_LEAP_ANGLE);
        }

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.LEAPFROGGER,
                DamageTag.WHACKING, DamageTag.RANGED));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.4f).setSynced(false));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private FeetData feetData, leftData, rightData;
            @Override
            public void create() {
                if (hbox.getBody() != null) {
                    feetData = new FeetData(UserDataType.FEET, hbox);
                    Fixture feet = new HadalFixture(
                            new Vector2(1.0f / 2,  - hbox.getSize().y / 2),
                            new Vector2(hbox.getSize().x, hbox.getSize().y / 8),
                            BodyConstants.BIT_SENSOR, (short) (BodyConstants.BIT_DROPTHROUGHWALL | BodyConstants.BIT_WALL), hbox.getFilter())
                            .addToBody(hbox.getBody());
                    feet.setUserData(feetData);

                    leftData = new FeetData(UserDataType.FEET, hbox);
                    Fixture leftSensor = new HadalFixture(
                            new Vector2(-hbox.getSize().x / 2, 0.5f),
                            new Vector2(hbox.getSize().x / 8, hbox.getSize().y - 2),
                            BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, hbox.getFilter())
                            .addToBody(hbox.getBody());
                    leftSensor.setUserData(leftData);

                    rightData = new FeetData(UserDataType.FEET, hbox);
                    Fixture rightSensor = new HadalFixture(
                            new Vector2(hbox.getSize().x / 2,  0.5f),
                            new Vector2(hbox.getSize().x / 8, hbox.getSize().y - 2),
                            BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, hbox.getFilter())
                            .addToBody(hbox.getBody());
                    rightSensor.setUserData(rightData);

                    hbox.setDropthroughCollider(feet);
                }
            }

            private float leapAmount;
            private float groundedCount;
            @Override
            public void controller(float delta) {
                if (feetData.getNumContacts() > 0) {
                    groundedCount += delta;
                    if (groundedCount > LEAP_COUNT) {
                        if (leapAmount > LEAP_AMOUNT) {
                            hbox.die();
                        } else {
                            SoundEffect.SPRING.playSourced(state, startPosition, 0.3f);
                            SoundEffect.FROG_CROAK.playSourced(state, startPosition, 0.5f);
                            hbox.setLinearVelocity(adjustedVelocity);
                            groundedCount = 0;
                            leapAmount++;
                        }
                    }
                } else {
                    groundedCount = 0;
                }

                if (rightData.getNumContacts() > 0 && adjustedVelocity.x > 0) {
                    adjustedVelocity.scl(-1.0f, 1.0f);
                }
                if (leftData.getNumContacts() > 0 && adjustedVelocity.x < 0) {
                    adjustedVelocity.scl(-1.0f, 1.0f);
                }
            }
        });

        return hbox;
    }
}