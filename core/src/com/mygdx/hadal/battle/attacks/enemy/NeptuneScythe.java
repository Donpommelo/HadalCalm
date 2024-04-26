package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class NeptuneScythe extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(240, 80);
    private static final float LIFESPAN = 3.2f;
    private static final float BASE_DAMAGE = 5.5f;
    private static final float KNOCKBACK = 2.5f;
    private static final float SPIN_SPEED = 0.25f;
    private static final float AMPLITUDE = 30.0f;
    private static final float FREQUENCY = 1.2f;
    private static final float SPREAD = 90.0f;
    private static final float ANGLE_FREQUENCY = 0.8f;
    private static final float PULSE_INTERVAL = 0.06f;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_SHOT_B;

    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundEffect.SLASH.playSourced(state, position, 1.2f, 0.5f);

        Hitbox[] hboxes = new Hitbox[4];
        hboxes[0] = scytheSingle(state, user, position, 0);
        hboxes[1] = scytheSingle(state, user, position, 90);
        hboxes[2] = scytheSingle(state, user, position, 180);
        hboxes[3] = scytheSingle(state, user, position, 270);

        return hboxes;
    }

    private Hitbox scytheSingle(PlayState state, Schmuck user, Vector2 startPosition, float startAngle) {
        RangedHitbox scythe = new RangedHitbox(state, startPosition, PROJ_SIZE,
                LIFESPAN, new Vector2(), user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
        scythe.makeUnreflectable();

        scythe.addStrategy(new ControllerDefault(state, scythe, user.getBodyData()));
        scythe.addStrategy(new CreateSound(state, scythe, user.getBodyData(), SoundEffect.WOOSH, 0.4f, true)
                .setPitch(0.6f).setSyncType(SyncType.NOSYNC));
        scythe.addStrategy(new DieParticles(state, scythe, user.getBodyData(), Particle.DIATOM_IMPACT_SMALL)
                .setSyncType(SyncType.NOSYNC));
        scythe.addStrategy(new ContactUnitSound(state, scythe, user.getBodyData(), SoundEffect.ZAP, 0.6f, true)
                .setSynced(false));
        scythe.addStrategy(new DamagePulse(state, scythe, user.getBodyData(), scythe.getSize(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setInterval(PULSE_INTERVAL));

        scythe.addStrategy(new HitboxStrategy(state, scythe, user.getBodyData()) {

            private float delayCount, controllerCount;
            private float timer;
            private float angle;
            private static final float delay = 0.5f;
            private static final float pushInterval = 1 / 60f;
            private final Vector2 offset = new Vector2();
            private final Vector2 centerPos = new Vector2();
            @Override
            public void controller(float delta) {

                //scythes delay some before moving outward
                if (delayCount < delay) {
                    delayCount += delta;
                } else {
                    timer += delta;
                }
                controllerCount += delta;
                while (controllerCount >= pushInterval) {
                    controllerCount -= pushInterval;

                    //this makes scythes rotate
                    angle += SPIN_SPEED;
                    if (user.getBody() != null && user.isAlive()) {

                        //scythes move along a sin wave in arc from user
                        offset.set(0, AMPLITUDE * MathUtils.sin(timer * FREQUENCY))
                                .setAngleDeg(startAngle + SPREAD * MathUtils.sin(timer * ANGLE_FREQUENCY));
                        centerPos.set(user.getPosition()).add(offset);
                        hbox.setTransform(centerPos, angle);
                    } else {
                        hbox.die();
                    }
                }
            }
        });

        return scythe;
    }
}