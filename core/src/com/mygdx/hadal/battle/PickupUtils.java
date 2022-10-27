package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.TrickOrTreating;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class PickupUtils {

    public static final Vector2 PICKUP_SIZE = new Vector2(40, 40);
    public static final float PICKUP_DURATION = 10.0f;
    private static final float FLASH_LIFESPAN = 1.0f;

    public static Hitbox createPickup(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {

        final int type = extraFields.length >= 1 ? (int) extraFields[0] : 0;
        final float power = extraFields.length >= 2 ? extraFields[1] : 0;
        Sprite sprite = Sprite.NOTHING;
        Particle particle = Particle.NOTHING;
        SoundEffect sound = SoundEffect.MAGIC21_HEAL;
        float volume = 0.3f;
        if (Constants.PICKUP_HEALTH == type) {
            sprite = Sprite.MEDPAK;
            particle = Particle.PICKUP_HEALTH;
        }
        if (Constants.PICKUP_FUEL == type) {
            sprite = Sprite.FUEL;
            particle = Particle.PICKUP_ENERGY;
            sound = SoundEffect.MAGIC2_FUEL;
        }
        if (Constants.PICKUP_AMMO == type) {
            sprite = Sprite.AMMO;
            particle = Particle.PICKUP_AMMO;
            sound = SoundEffect.LOCKANDLOAD;
            volume = 0.8f;
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, PICKUP_SIZE, PICKUP_DURATION, startVelocity,
                (short) 0, false, false, user, sprite);
        hbox.setGravity(1.0f);
        hbox.setFriction(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), particle).setParticleDuration(5.0f)
                .setIgnoreOnTimeout(true).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), sound, volume)
                .setIgnoreOnTimeout(true).setSynced(false));

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            //delay prevents spawned medpaks from being instantly consumed by the (dead) player that dropped them
            private float delay = 0.1f;

            @Override
            public void controller(float delta) {
                if (delay >= 0) {
                    delay -= delta;
                }
            }

            @Override
            public void onHit(HadalData fixB) {
                if (fixB instanceof PlayerBodyData bodyData && 0 >= delay) {
                    if (Constants.PICKUP_HEALTH == type) {
                        if (bodyData.getCurrentHp() < bodyData.getStat(Stats.MAX_HP)) {
                            bodyData.regainHp(power * bodyData.getStat(Stats.MAX_HP), bodyData, true, DamageTag.MEDPAK);
                            hbox.die();
                        }
                    }
                    if (Constants.PICKUP_FUEL == type) {
                        if (bodyData.getCurrentFuel() < bodyData.getStat(Stats.MAX_FUEL)) {
                            bodyData.fuelGain(power);
                            hbox.die();
                        }
                    }
                    if (Constants.PICKUP_AMMO == type) {
                        if (bodyData.getCurrentTool().getClipLeft() < bodyData.getCurrentTool().getClipSize()) {
                            bodyData.getCurrentTool().gainAmmo(power);
                            hbox.die();
                        }
                    }
                }
            }
        });
        if (Constants.PICKUP_HEALTH == type) {
            hbox.setBotHealthPickup(true);
        }
        return hbox;
    }

    /**
     * This spawns some amount of scrap events as currency for the player
     *
     * @param statCheck: do we take into account the player's bonus scrap drop?
     * @param score: does picking up the screp increment the player's score?
     */
    public static void spawnScrap(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                  int amount, boolean statCheck, boolean score) {

        float countScore = score ? 1.0f : 0.0f;

        int modifiedAmount;
        if (statCheck && null != state.getPlayer().getPlayerData()) {
            if (1.0f > state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount
                    && 0 < state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP)) {
                modifiedAmount = amount + 1;
            } else {
                modifiedAmount = (int) (amount * (1 + state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP)));
            }
        } else {
            modifiedAmount = amount;
        }

        Vector2[] positions = new Vector2[modifiedAmount];
        Vector2[] velocities = new Vector2[modifiedAmount];
        for (int i = 0; i < modifiedAmount; i++) {
            positions[i] = startPosition;
            velocities[i] = startVelocity.nor().scl(EGGPLANT_VELO);
        }
        SyncedAttack.EGGPLANT.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, countScore);
    }

    private static final Vector2 EGGPLANT_SIZE = new Vector2(32, 32);
    private static final float EGGPLANT_LIFESPAN = 9.0f;
    private static final int EGGPLANT_SPREAD = 90;
    private static final float EGGPLANT_VELO = 7.5f;

    public static Hitbox[] createScrap(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];
        boolean score = extraFields.length >= 1 && extraFields[0] == 1.0f;

        if (0 != startPosition.length) {
            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], EGGPLANT_SIZE, EGGPLANT_LIFESPAN, startVelocity[i],
                        (short) 0, false, false, user, Sprite.NASU);
                hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_SENSOR | Constants.BIT_PICKUP_RADIUS));
                hbox.setBotModePickup(true);
                hbox.setGravity(1.0f);
                hbox.setFriction(1.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARKLE, 0.0f, 0.0f)
                        .setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKLE)
                        .setIgnoreOnTimeout(true).setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.COIN3, 0.8f)
                        .setIgnoreOnTimeout(true).setSynced(false));
                hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), EGGPLANT_SPREAD));
                hbox.addStrategy(new PickupVacuum(state, hbox, user.getBodyData()));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    @Override
                    public void onPickup(HadalData picker) {

                        if (picker instanceof PlayerBodyData playerBodyData) {
                            //in single player, scrap gives the player 1 unit of currency
                            if (GameStateManager.currentMode == GameStateManager.Mode.SINGLE) {
                                state.getGsm().getRecord().incrementScrap(1);
                            } else if (score) {

                                //in eggplant mode, we increase the players score by 1
                                state.getMode().processPlayerScoreChange(state, playerBodyData.getPlayer(), 1);
                            }

                            state.getUiExtra().syncUIText(UITag.uiType.SCRAP);

                            //activate effects that activate upon picking up scrap
                            playerBodyData.statusProcTime(new ProcTime.ScrapPickup());
                        }

                        hbox.die();
                    }
                });
                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }

    private static final float CANDY_VELO = 7.5f;
    public static void spawnCandy(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, int amount) {
        Vector2[] positions = new Vector2[amount];
        Vector2[] velocities = new Vector2[amount];
        for (int i = 0; i < amount; i++) {
            positions[i] = startPosition;
            velocities[i] = startVelocity.nor().scl(CANDY_VELO);
        }
        for (Hitbox hbox : SyncedAttack.CANDY.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities)) {
            hbox.addStrategy(new PickupVacuum(state, hbox, user.getBodyData()));
        }
    }

    public static final Vector2 CANDY_SIZE = new Vector2(60, 60);
    public static final float CANDY_DURATION = 20.0f;
    public static final int CANDY_SPREAD = 45;
    private static final Sprite[] CANDY_SPRITES = {Sprite.CANDY_A, Sprite.CANDY_B, Sprite.CANDY_C, Sprite.CANDY_D};
    public static Hitbox[] createCandy(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (0 != startPosition.length) {
            for (int i = 0; i < startPosition.length; i++) {
                int randomIndex = MathUtils.random(CANDY_SPRITES.length - 1);
                Hitbox hbox = new RangedHitbox(state, startPosition[i], CANDY_SIZE, CANDY_DURATION, startVelocity[i],
                        (short) 0, false, false, user, CANDY_SPRITES[randomIndex]);
                hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_SENSOR | Constants.BIT_PICKUP_RADIUS));
                hbox.setLayer(PlayState.ObjectLayer.STANDARD);
                hbox.setBotModePickup(true);
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
                                state.getMode().processPlayerScoreChange(state, playerBodyData.getPlayer(), 1);
                            }
                            state.getMode().processTeamScoreChange(state, bucket.getTeamIndex(), 1);
                        }

                        hbox.die();
                    }
                });

                hboxes[i] = hbox;
            }
        }

        return hboxes;
    }
}
