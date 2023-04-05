package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.FlashNearDeath;

public class TeslaCoilProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(45, 45);
    public static final float LIFESPAN = 4.5f;
    private static final float FLASH_LIFESPAN = 1.0f;

    public static final float PULSE_INTERVAL = 1.0f;
    private static final float RADIUS = 25.0f;

    private static final Sprite PROJ_SPRITE = Sprite.PYLON;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LAUNCHER.playSourced(state, startPosition, 0.25f);

        Vector2 pos1 = new Vector2();
        if (extraFields.length > 1) {
            pos1.set(extraFields[0], extraFields[1]);
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true,
                true, user, PROJ_SPRITE);

        final Vector2 endLocation = new Vector2(pos1);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 startLocation = new Vector2();
            private float distance;
            private boolean firstPlanted = false;
            private boolean planted = false;
            private boolean activated = false;
            private float controllerCount;
            @Override
            public void create() {
                //keep track of the coil's travel distance
                this.startLocation.set(hbox.getPixelPosition());
                this.distance = startLocation.dst(endLocation) - PROJECTILE_SIZE.x;
            }

            private final Vector2 entityLocation = new Vector2();
            @Override
            public void controller(float delta) {

                //planted coils stop and activates
                if (firstPlanted) {
                    firstPlanted = false;
                    planted = true;

                    if (hbox.getBody() != null) {
                        hbox.setLinearVelocity(0, 0);
                        hbox.getBody().setType(BodyDef.BodyType.StaticBody);
                    }

                    SoundEffect.METAL_IMPACT_1.playSourced(state, startPosition, 0.5f);
                }

                //activated coils periodically check world for nearby coils
                //this is only processed by the server
                if (planted && hbox.getState().isServer()) {

                    controllerCount += delta;

                    while (controllerCount >= PULSE_INTERVAL) {
                        controllerCount -= PULSE_INTERVAL;

                        activated = false;
                        entityLocation.set(hbox.getPosition());
                        hbox.getWorld().QueryAABB(fixture -> {
                            if (fixture.getUserData() instanceof HitboxData hitboxData) {
                                if (user instanceof Player player) {
                                    if (player.getSpecialWeaponHelper().getTeslaCoils().contains(hitboxData.getHbox(), false)) {
                                        if (!fixture.getUserData().equals(hbox.getHadalData())) {
                                            if (hitboxData.getHbox().getLinearVelocity().isZero()) {
                                                coilPairActivated(state, hitboxData.getHbox());
                                            }
                                        }
                                    }
                                }

                            }
                            return true;
                        }, entityLocation.x - RADIUS, entityLocation.y - RADIUS, entityLocation.x + RADIUS, entityLocation.y + RADIUS);
                    }
                    return;
                }

                //After reaching the location clicked, the coil is marked as planted
                if (startLocation.dst2(hbox.getPixelPosition()) >= distance * distance) {
                    firstPlanted = true;
                    controllerCount = PULSE_INTERVAL;
                }
            }

            @Override
            public void onHit(HadalData fixB) {

                //activated coils do nothing when hit.
                if (planted) {
                    return;
                }

                //unactivated coils should stop and plant when they hit a wall
                if (fixB == null) {
                    firstPlanted = true;
                } else if (UserDataType.WALL.equals(fixB.getType())){
                    firstPlanted = true;
                }
            }

            @Override
            public void die() {
                //remove dead coils from list
                if (user instanceof Player player) {
                    player.getSpecialWeaponHelper().getTeslaCoils().removeValue(hbox, false);
                }
            }

            /**
             * This activates when a coil performs its periodic check of nearby coils and finds one
             * @param state: playstate
             * @param hboxOther: the other coil to connect to
             */
            public void coilPairActivated(PlayState state, Hitbox hboxOther) {
                if (!activated) {
                    activated = true;
                    Vector2 otherPosition = new Vector2(hboxOther.getPixelPosition());
                    SyncedAttack.TESLA_ACTIVATION.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(),
                            startVelocity, otherPosition.x, otherPosition.y);
                }
            }
        });

        if (user instanceof Player player) {
            player.getSpecialWeaponHelper().getTeslaCoils().add(hbox);
        }

        return hbox;
    }
}
