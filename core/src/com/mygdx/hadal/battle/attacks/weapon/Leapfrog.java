package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public static final Vector2 PROJECTILE_SIZE = new Vector2(75, 32);
    public static final Vector2 SPRITE_SIZE = new Vector2(100, 100);
    public static final float LIFESPAN = 50.0f;
    public static final float BASE_DAMAGE = 27.0f;
    private static final float RECOIL = 6.0f;
    private static final float KNOCKBACK = 30.0f;
    private static final float FLASH_LIFESPAN = 0.5f;

    public static final float LEAP_DELAY = 0.1f;
    public static final float LAND_DELAY = 0.1f;
    public static final int LEAP_AMOUNT = 3;
    private static final float LEAP_COUNT = 0.5f;
    private static final float LEAP_DURATION = 0.35f;
    private static final float MAX_LEAP_DURATION = 2.5f;
    private static final float MIN_LEAP_ANGLE = 45;

    private static final Sprite PROJ_SPRITE = Sprite.FROG_STAND;

    private final Vector2 adjustedVelocity = new Vector2();
    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPRING.playSourced(state, startPosition, 0.3f);
        SoundEffect.FROG_CROAK.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE) {

            private boolean flip;
            @Override
            public void render(SpriteBatch batch, Vector2 entityLocation) {
                if (!alive) { return; }

                float direction = getLinearVelocity().x;
                if (direction != 0.0f) {
                    flip = getLinearVelocity().x > 0.0f;
                }

                batch.draw(projectileSprite.getKeyFrame(animationTime, false),
                        (flip ? 0 : spriteSize.x) + entityLocation.x - spriteSize.x / 2,
                        entityLocation.y - spriteSize.y / 2 + spriteOffset.y,
                        spriteSize.x / 2,
                        (flip ? 1 : -1) * spriteSize.y / 2,
                        (flip ? 1 : -1) * spriteSize.x, spriteSize.y, 1, 1, 0);
            }
        };
        hbox.setGravity(7);
        hbox.setFriction(1.0f);
        hbox.setSpriteSize(SPRITE_SIZE);

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
                DamageTag.WHACKING, DamageTag.RANGED)
                .setRepeatable(true));
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
            private float aerialCount;
            private float jumpDuration, landDuration;
            private boolean jumpStart, jumping, landStart;
            @Override
            public void controller(float delta) {
                if (feetData.getNumContacts() > 0) {
                    aerialCount = 0.0f;

                    if (jumpStart && jumpDuration >= LEAP_DELAY) {
                        SoundEffect.SPRING.playSourced(state, startPosition, 0.3f);
                        SoundEffect.FROG_CROAK.playSourced(state, startPosition, 0.5f);
                        hbox.setLinearVelocity(adjustedVelocity);
                        groundedCount = 0;
                        leapAmount++;

                        jumping = true;
                        jumpStart = false;
                    }

                    if (jumping && jumpDuration >= LEAP_DURATION) {
                        hbox.setSprite(Sprite.FROG_LAND);
                        hbox.setSpriteSize(SPRITE_SIZE);
                        landDuration = 0.0f;

                        landStart = true;
                        jumping = false;
                    }

                    if (landStart && landDuration >= LAND_DELAY) {
                        hbox.setSprite(Sprite.FROG_STAND);
                        hbox.setSpriteSize(SPRITE_SIZE);
                        landStart = false;
                    }

                    groundedCount += delta;
                    if (groundedCount > LEAP_COUNT) {
                        if (leapAmount > LEAP_AMOUNT) {
                            hbox.die();
                        } else if (!jumpStart) {
                            jumpStart = true;
                            hbox.setSprite(Sprite.FROG_JUMP);
                            hbox.setSpriteSize(SPRITE_SIZE);
                            jumpDuration = 0.0f;
                        }
                    }
                } else {
                    groundedCount = 0;

                    //safeguard for projectiles that never touch the ground
                    aerialCount += delta;
                    if (aerialCount >= MAX_LEAP_DURATION) {
                        hbox.die();
                    }
                }

                if (jumping || jumpStart) {
                    jumpDuration += delta;
                }

                if (landStart) {
                    landDuration += delta;
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