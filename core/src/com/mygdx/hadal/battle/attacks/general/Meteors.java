package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Meteors extends SyncedAttacker {

    private static final Vector2 METEOR_SIZE = new Vector2(75, 75);
    private static final float METEOR_SPEED = 50.0f;
    private static final float RANGE = 1500.0f;
    private static final float LIFESPAN = 5.0f;

    private static final Sprite[] PROJ_SPRITES = {Sprite.METEOR_A, Sprite.METEOR_B, Sprite.METEOR_C, Sprite.METEOR_D,
            Sprite.METEOR_E, Sprite.METEOR_F};

    private final DamageSource damageSource;

    public Meteors(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        float interval = 0.0f;
        float damage = 0.0f;
        int meteorNum = 0;

        if (extraFields.length > 2) {
            interval = extraFields[0];
            damage = extraFields[1];
            meteorNum = (int) extraFields[2];
        }

        float finalInterval = interval;
        float finalDamage = damage;

        float meteorDuration = (1 + meteorNum) * interval;

        ParticleEntity particle = new ParticleEntity(state, user, Particle.RING, 1.0f, meteorDuration, true,
                SyncType.NOSYNC).setScale(0.4f);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.HBOX);
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(1, 1), meteorDuration, new Vector2(),
                (short) 0, false, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float shortestFraction;
            private final Vector2 originPt = new Vector2();
            private final Vector2 endPt = new Vector2();

            private float procCdCount;
            private int meteorCount;
            @Override
            public void controller(float delta) {
                procCdCount += delta;

                if (procCdCount >= finalInterval) {
                    procCdCount -= finalInterval;

                    if (extraFields.length > meteorCount + 3) {
                        originPt.set(startPosition).add(extraFields[meteorCount + 3], 0);
                        endPt.set(originPt).add(0, -RANGE);
                        shortestFraction = 1.0f;

                        if (WorldUtil.preRaycastCheck(originPt, endPt)) {
                            state.getWorld().rayCast((fixture, point, normal, fraction) -> {
                                if (Constants.BIT_WALL == fixture.getFilterData().categoryBits && fraction < shortestFraction) {
                                    shortestFraction = fraction;
                                    return fraction;
                                }
                                return -1.0f;
                            }, originPt, endPt);
                        }

                        endPt.set(originPt).add(0, -RANGE * shortestFraction).scl(PPM);
                        originPt.set(endPt).add(0, RANGE);

                        int randomIndex = MathUtils.random(PROJ_SPRITES.length - 1);
                        Sprite projSprite = PROJ_SPRITES[randomIndex];

                        Hitbox hbox = new Hitbox(state, new Vector2(originPt), METEOR_SIZE, LIFESPAN, new Vector2(0, -METEOR_SPEED), user.getHitboxFilter(), true, false, user, projSprite);
                        hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

                        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), finalDamage, 0.0f,
                                damageSource, DamageTag.FIRE, DamageTag.MAGIC));
                        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                            private final Vector2 floor = new Vector2(endPt);
                            @Override
                            public void controller(float delta) {
                                if (hbox.getPixelPosition().y - hbox.getSize().y / 2 <= floor.y) {
                                    hbox.setLinearVelocity(0, 0);
                                    hbox.die();
                                }
                            }
                        });

                        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, 1.0f)
                        .setSyncType(SyncType.NOSYNC));
                        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.BOULDER_BREAK).setParticleSize(90)
                        .setSyncType(SyncType.NOSYNC));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
                        }
                    }
                    meteorCount++;

                    if (0 == meteorCount % 3) {
                        hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.FALLING, 0.5f, false)
                                .setSyncType(SyncType.NOSYNC));
                    }
                }
            }
        });

        return hbox;
    }
}