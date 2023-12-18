package com.mygdx.hadal.battle.attacks.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.TrickOrTreating;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Candy extends SyncedAttacker {

    public static final Vector2 CANDY_SIZE = new Vector2(60, 60);
    public static final float CANDY_DURATION = 20.0f;
    public static final int CANDY_SPREAD = 45;
    private static final Sprite[] CANDY_SPRITES = {Sprite.CANDY_A, Sprite.CANDY_B, Sprite.CANDY_C, Sprite.CANDY_D};
    private static final float FLASH_LIFESPAN = 1.0f;

    @Override
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (0 != startPosition.length) {
            for (int i = 0; i < startPosition.length; i++) {
                int randomIndex = MathUtils.random(CANDY_SPRITES.length - 1);
                Hitbox hbox = new RangedHitbox(state, startPosition[i], CANDY_SIZE, CANDY_DURATION, startVelocity[i],
                        (short) 0, false, false, user, CANDY_SPRITES[randomIndex]);
                hbox.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR | BodyConstants.BIT_PICKUP_RADIUS));
                hbox.setLayer(PlayState.ObjectLayer.STANDARD);
                hbox.setBotModePickup(true);
                hbox.setSynced(true);
                hbox.setSyncedDelete(true);
                hbox.setGravity(1.0f);
                hbox.setFriction(1.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARKLE, 0.0f, 0.0f)
                        .setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKLE)
                        .setIgnoreOnTimeout(true).setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.COIN3, 1.1f)
                        .setIgnoreOnTimeout(true).setSynced(false));
                hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), CANDY_SPREAD));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    @Override
                    public void onPickup(HadalData picker) {
                        if (picker instanceof PlayerBodyData playerBodyData) {
                            Status candyStatus = playerBodyData.getStatus(TrickOrTreating.class);
                            if (null != candyStatus) {
                                if (candyStatus instanceof TrickOrTreating trickOrTreating) {
                                    trickOrTreating.incrementCandyCount(1);
                                }
                            }
                        }
                        if (picker.getEntity() instanceof TrickorTreatBucket bucket) {
                            if (creator instanceof PlayerBodyData playerBodyData) {
                                bucket.getEventData().preActivate(null, playerBodyData.getPlayer());
                            }
                        }

                        hbox.die();
                    }
                });

                hboxes[i] = hbox;
            }
        }

        return hboxes;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        return performSyncedAttackMulti(state, user, new Vector2(), new Vector2[]{startPosition},
                new Vector2[]{startVelocity}, extraFields)[0];
    }
}