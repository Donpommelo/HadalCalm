package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunSigh extends SyncedAttacker {

    private static final float LINGER = 1.0f;
    private static final Vector2 CLOUD_SIZE = new Vector2(120, 120);
    private static final float CLOUD_LIFESPAN = 0.75f;
    private static final float DELAY = 1.0f;
    private static final float INTERVAL = 0.1f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float KNOCKBACK = 5.0f;

    private static final float SIGH_LIFESPAN = 3.0f;
    private static final float SLOW_DURATION = 9.0f;
    private static final float SLOW_SLOW = 0.8f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox cloud = new Hitbox(state, startPosition, CLOUD_SIZE, SIGH_LIFESPAN, startVelocity,
                user.getHitboxFilter(), true, false, user, Sprite.NOTHING);

        cloud.addStrategy(new ControllerDefault(state, cloud, user.getBodyData()));
        cloud.addStrategy(new CreateParticles(state, cloud, user.getBodyData(), Particle.OVERCHARGE, 0.0f, LINGER)
                .setParticleColor(HadalColor.BLUE).setParticleSize(60.0f).setSyncType(SyncType.NOSYNC));
        cloud.addStrategy(new Static(state, cloud, user.getBodyData()));
        cloud.addStrategy(new HitboxStrategy(state, cloud, user.getBodyData()) {

            private float controllerCount;
            private boolean activated;
            @Override
            public void controller(float delta) {
                controllerCount += delta;

                if (controllerCount > DELAY) {

                    if (!activated) {
                        activated = true;
                        SoundEffect.ICE_IMPACT.playSourced(state, cloud.getPixelPosition(), 0.9f, 0.5f);
                    }

                    //after a delay, each cloud shoots a stream of ice outwards
                    while (controllerCount >= DELAY + INTERVAL) {
                        controllerCount -= INTERVAL;
                        Hitbox hbox = new RangedHitbox(state, cloud.getPixelPosition(), CLOUD_SIZE, CLOUD_LIFESPAN, startVelocity,
                                user.getHitboxFilter(), true, false, user, Sprite.NOTHING);

                        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, LINGER)
                                .setParticleSize(40.0f).setSyncType(SyncType.NOSYNC));
                        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
                        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), SLOW_DURATION, SLOW_SLOW, Particle.ICE_CLOUD));
                        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                            .setSynced(false));

                        if (!state.isServer()) {
                            ((PlayStateClient) state).addEntity(hbox.getEntityID(), hbox, false, PlayStateClient.ObjectLayer.HBOX);
                        }
                    }
                }
            }
        });

        return cloud;
    }
}
