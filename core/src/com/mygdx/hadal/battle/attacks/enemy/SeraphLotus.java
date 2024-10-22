package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class SeraphLotus extends SyncedAttacker {

    private static final float LIFESPAN = 4.0f;
    private static final Vector2 SPAWNER_SIZE = new Vector2(150, 150);
    private static final Vector2 PILLAR_SIZE = new Vector2(40, 40);
    private static final float DELAY = 1.0f;
    private static final float INTERVAL = 0.05f;
    private static final float SPEED = 60.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float KNOCKBACK = 5.0f;
    private static final int SPREAD = 60;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_C;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        boolean direction = false;
        if (extraFields.length > 0) {
            direction = extraFields[0] == 0.0f;
        }
        boolean finalDirection = direction;

        Hitbox spawner = new Hitbox(state, startPosition, SPAWNER_SIZE, LIFESPAN, new Vector2(), user.getHitboxFilter(),
                true, false, user, PROJ_SPRITE);

        spawner.addStrategy(new ControllerDefault(state, spawner, user.getBodyData()));
        spawner.addStrategy(new HitboxStrategy(state, spawner, user.getBodyData()) {

            private float controllerCount;
            private boolean activated;
            @Override
            public void controller(float delta) {
                controllerCount += delta;

                if (controllerCount > DELAY) {

                    if (!activated) {
                        activated = true;
                    }

                    //after a delay, each cloud shoots a stream of ice outwards
                    while (controllerCount >= DELAY + INTERVAL) {
                        controllerCount -= INTERVAL;

                        Vector2 positionOffset = new Vector2(spawner.getPixelPosition())
                                .add(MathUtils.random(-SPREAD, SPREAD + 1),	MathUtils.random(-SPREAD, SPREAD + 1));

                        Hitbox hbox = new RangedHitbox(state, positionOffset, PILLAR_SIZE, LIFESPAN,
                                new Vector2(0, SPEED).setAngleDeg(finalDirection ? 270 : 0), user.getHitboxFilter(),
                                true, false, user, Sprite.SPORE);

                        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
                        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                        }
                    }
                }
            }
        });

        return spawner;
    }
}
