package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class FlashbangProjectile extends SyncedAttacker {

    public static final float BASE_DAMAGE = 30.0f;
    public static final Vector2 PROJECTILE_SIZE = new Vector2(34, 64);
    public static final float BLIND_DURATION = 4.5f;

    private static final float LIFESPAN = 2.0f;
    private static final float KNOCKBACK = 0.0f;
    private static final int CURRENT_RADIUS = 200;
    private static final float FLASHBANG_ROTATION_SPEED = 8.0f;

    private static final Sprite PROJ_SPRITE = Sprite.FLASH_GRENADE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.LAUNCHER)
                .setVolume(0.35f)
                .setPosition(user.getPixelPosition()));

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, false, user, PROJ_SPRITE);
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), FLASHBANG_ROTATION_SPEED));

        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.FLASH_BANG, DamageTag.MAGIC));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void die() {
                SoundManager.play(state, new SoundLoad(SoundEffect.FLASHBANG)
                        .setVolume(1.5f)
                        .setPitch(1.8f)
                        .setPosition(this.hbox.getPixelPosition()));

                Hitbox hbox = new Hitbox(state, this.hbox.getPixelPosition(), new Vector2(CURRENT_RADIUS, CURRENT_RADIUS),
                        0.4f, new Vector2(0, 0), (short) 0, true, false, user, Sprite.NOTHING);
                hbox.setSyncDefault(false);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION)
                        .setParticleSize(25));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    @Override
                    public void onHit(HadalData fixB, Body body) {
                        if (fixB instanceof BodyData bodyData) {
                            bodyData.addStatus(new Blinded(state, BLIND_DURATION, creator, bodyData, true));
                        }
                    }
                });

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.EFFECT);
                }
            }
        });

        return hbox;

    }
}