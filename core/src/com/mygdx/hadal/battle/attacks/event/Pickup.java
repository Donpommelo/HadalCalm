package com.mygdx.hadal.battle.attacks.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Pickup extends SyncedAttacker {

    public static final Vector2 PICKUP_SIZE = new Vector2(40, 40);
    public static final float PICKUP_DURATION = 10.0f;
    private static final float FLASH_LIFESPAN = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
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
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), particle)
                .setParticleDuration(5.0f)
                .setIgnoreOnTimeout(true));
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
            public void onHit(HadalData fixB, Body body) {
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
                        Player player = bodyData.getPlayer();
                        if (player.getEquipHelper().getCurrentTool().getClipLeft() < player.getEquipHelper().getCurrentTool().getClipSize()) {
                            player.getEquipHelper().getCurrentTool().gainAmmo(power);
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
}