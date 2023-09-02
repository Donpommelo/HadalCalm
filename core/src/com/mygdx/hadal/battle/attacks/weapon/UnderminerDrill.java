package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.WorldUtil;

public class UnderminerDrill extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(36, 30);
    public static final float LIFESPAN = 4.0f;
    public static final float BASE_DAMAGE = 40.0f;
    private static final float KNOCKBACK = 10.0f;

    public static final float FRAG_DAMAGE = 15.0f;
    private static final Vector2 FRAG_SIZE = new Vector2(36, 30);
    private static final float FRAG_LIFESPAN = 2.0f;
    private static final float FRAG_KNOCKBACK = 25.0f;
    private static final float FRAG_SPEED = 4.0f;

    private static final float RAYCAST_RANGE = 8.0f;
    private static final int NUM_DRILLS = 6;
    private static final float DRILL_SPEED = 2.5f;
    private static final float DRILL_DURATION = 1.0f;

    private static final float BOMB_SPEED = 20.0f;
    private static final float BOMB_LIFESPAN = 1.5f;
    private static final int NUM_BOMBS = 3;
    private static final int SPREAD = 30;

    public static final float EXPLOSION_DAMAGE = 25.0f;
    private static final int EXPLOSION_RADIUS = 100;
    private static final float EXPLOSION_KNOCKBACK = 18.0f;

    private static final Vector2 PROJECTILE_SPRITE_SIZE = new Vector2(54, 45);
    private static final Sprite PROJ_SPRITE = Sprite.DRILL;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.FIRE10.playSourced(state, startPosition, 0.8f);

        Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));
        hbox.setSpriteSize(PROJECTILE_SPRITE_SIZE);
        hbox.setGravity(3.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.UNDERMINER, DamageTag.RANGED));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private boolean drilling;
            private boolean activated;
            private float invuln;
            private float drillCount;
            private final Vector2 angle = new Vector2();
            private final Vector2 raycast = new Vector2(0, RAYCAST_RANGE);
            private final Vector2 entityLocation = new Vector2();
            boolean wallDetected;

            @Override
            public void controller(float delta) {

                if (!activated) {
                    hbox.setTransform(hbox.getPosition(), MathUtils.atan2(hbox.getLinearVelocity().y, hbox.getLinearVelocity().x));
                }

                if (drilling && invuln <= 0) {
                    drillCount += delta;

                    if (drillCount >= DRILL_DURATION && !activated) {
                        drilling = false;
                        activate();
                    }
                }

                //invulnerability is incremented to prevent hbox from detonating immediately upon hitting a corner
                if (invuln > 0) {
                    invuln -= delta;
                }

                if (activated) {
                    if (state.isServer()) {
                        Array<Vector2> drillVelocities = new Array<>();
                        for (int i = 0; i < NUM_DRILLS; i++) {
                            float angleOffset = hbox.getAngle() + MathUtils.degRad * i / NUM_DRILLS * 360;
                            entityLocation.set(hbox.getPosition());
                            angle.set(entityLocation).add(raycast.setAngleRad(angleOffset));
                            wallDetected = false;

                            if (WorldUtil.preRaycastCheck(entityLocation, angle)) {
                                state.getWorld().rayCast((fixture, point, normal, fraction) -> {
                                    if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
                                        wallDetected = true;
                                    }
                                    return -1.0f;
                                }, entityLocation, angle);
                            }

                            if (wallDetected) {
                                drillVelocities.add(new Vector2(0, 1).setAngleRad(angleOffset).scl(FRAG_SPEED));
                            }
                        }

                        Vector2[] positions = new Vector2[drillVelocities.size];
                        Vector2[] velocities = new Vector2[drillVelocities.size];
                        float[] fragVelocities = new float[drillVelocities.size * NUM_BOMBS];
                        entityLocation.set(hbox.getPixelPosition());
                        for (int i = 0; i < drillVelocities.size; i++) {
                            positions[i] = entityLocation;
                            velocities[i] = drillVelocities.get(i);
                            for (int j = 0; j < NUM_BOMBS; j++) {
                                fragVelocities[NUM_BOMBS * i + j] = MathUtils.random(-SPREAD, SPREAD);
                            }
                        }
                        SyncedAttack.UNDERMINER_DRILL.initiateSyncedAttackMulti(state, user, new Vector2(), positions,
                                velocities, fragVelocities);
                    }

                    hbox.die();
                }
            }

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    if (UserDataType.WALL.equals(fixB.getType())) {

                        //upon hitting a wall, hbox activates and begins drilling in a straight line
                        if (!drilling) {
                            drilling = true;
                            hbox.setLinearVelocity(hbox.getLinearVelocity().nor().scl(DRILL_SPEED));
                            hbox.setGravityScale(0);
                            invuln = 0.1f;
                        } else {
                            //if already activated (i.e drilling to other side of wall), hbox explodes
                            if (invuln <= 0) {
                                drilling = false;
                                activate();
                            }
                        }
                    }
                }
            }

            private void activate() {
                if (!activated) {
                    activated = true;
                }
            }
        });

        return hbox;
    }

    @Override
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (startPosition.length != 0) {
            for (int i = 0; i < startPosition.length; i++) {
                final int drillNum = i;
                Hitbox frag = new Hitbox(state, startPosition[i], FRAG_SIZE, FRAG_LIFESPAN, startVelocity[i], user.getHitboxFilter(),
                        true, true, user, PROJ_SPRITE);
                frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                frag.addStrategy(new AdjustAngle(state, frag, user.getBodyData()));
                frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), FRAG_DAMAGE, FRAG_KNOCKBACK,
                        DamageSource.UNDERMINER, DamageTag.RANGED));
                frag.addStrategy(new HitboxStrategy(state, frag, user.getBodyData()) {

                    @Override
                    public void onHit(HadalData fixB, Body body) {
                        if (fixB != null) {
                            if (UserDataType.WALL.equals(fixB.getType())) {
                                hbox.die();
                            }
                        }
                    }

                    @Override
                    public void die() {
                        for (int i = 0; i < NUM_BOMBS; i++) {
                            if (extraFields.length > drillNum * NUM_BOMBS + i) {
                                Hitbox bomb = new Hitbox(state, hbox.getPixelPosition(), FRAG_SIZE, BOMB_LIFESPAN,
                                        new Vector2(startVelocity[drillNum]).setAngleDeg(startVelocity[drillNum].angleDeg() +
                                                extraFields[drillNum * NUM_BOMBS + i]).nor().scl(BOMB_SPEED),
                                        user.getHitboxFilter(), true, true, user, PROJ_SPRITE);
                                bomb.setSyncDefault(false);
                                bomb.setGravity(3.0f);
                                bomb.setDurability(2);

                                bomb.addStrategy(new ControllerDefault(state, bomb, user.getBodyData()));
                                bomb.addStrategy(new AdjustAngle(state, bomb, user.getBodyData()));
                                bomb.addStrategy(new DamageStandard(state, bomb, user.getBodyData(), FRAG_DAMAGE, FRAG_KNOCKBACK,
                                        DamageSource.UNDERMINER, DamageTag.RANGED));
                                bomb.addStrategy(new ContactWallDie(state, bomb, user.getBodyData()).setDelay(0.1f));
                                bomb.addStrategy(new DieExplode(state, bomb, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE,
                                        EXPLOSION_KNOCKBACK, user.getHitboxFilter(), false, DamageSource.UNDERMINER));
                                bomb.addStrategy(new DieSound(state, bomb, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f).setSynced(false));
                                bomb.addStrategy(new FlashShaderNearDeath(state, bomb, user.getBodyData(), 1.0f));

                                if (!state.isServer()) {
                                    ((ClientState) state).addEntity(bomb.getEntityID(), bomb, false, ClientState.ObjectLayer.HBOX);
                                }
                            }
                        }
                    }
                });
                hboxes[i] = frag;
            }
        }
        return hboxes;
    }
}
