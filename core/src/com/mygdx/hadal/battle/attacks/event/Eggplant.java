package com.mygdx.hadal.battle.attacks.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Eggplant extends SyncedAttacker {

    private static final Vector2 EGGPLANT_SIZE = new Vector2(32, 32);
    private static final float EGGPLANT_LIFESPAN = 9.0f;
    private static final int EGGPLANT_SPREAD = 90;
    private static final float FLASH_LIFESPAN = 1.0f;

    @Override
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];
        boolean score = extraFields.length >= 1 && extraFields[0] == 1.0f;

        if (0 != startPosition.length) {
            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], EGGPLANT_SIZE, EGGPLANT_LIFESPAN, startVelocity[i],
                        (short) 0, false, false, user, Sprite.NASU);
                hbox.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR | BodyConstants.BIT_PICKUP_RADIUS));
                hbox.setBotModePickup(true);
                hbox.setSynced(true);
                hbox.setSyncedDelete(true);
                hbox.setGravity(1.0f);
                hbox.setFriction(1.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARKLE));
                hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKLE).setIgnoreOnTimeout(true));
                hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.COIN3, 0.8f).setIgnoreOnTimeout(true));
                hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), EGGPLANT_SPREAD));
                hbox.addStrategy(new PickupVacuum(state, hbox, user.getBodyData()));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    @Override
                    public void onPickup(HadalData picker) {
                        if (picker instanceof PlayerBodyData playerBodyData) {
                            //in single player, scrap gives the player 1 unit of currency
                            if (StateManager.currentMode == StateManager.Mode.SINGLE) {
                                JSONManager.record.incrementScrap(1);
                            } else if (score) {

                                //in eggplant mode, we increase the players score by 1
                                state.getMode().processPlayerScoreChange(state, playerBodyData.getPlayer(), 1);
                            }

                            state.getUIManager().getUiExtra().syncUIText(UITagType.SCRAP);

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
}