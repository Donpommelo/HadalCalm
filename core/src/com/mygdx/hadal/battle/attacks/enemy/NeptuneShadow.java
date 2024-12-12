package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class NeptuneShadow extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(30, 30);
    private static final int SHADOW_NUM = 12;
    private static final float LIFESPAN = 5.0f;
    private static final float BASE_DAMAGE = 22.0f;
    private static final float KNOCKBACK = 15.0f;
    private static final float DELAY = 2.5f;
    private static final float SPEED = 25.0f;
    private static final float INTERVAL = 0.05f;

    final Vector2 position = new Vector2();
    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        position.set(user.getPixelPosition());

        int shadowNum = 0;
        Vector2 savedVelo = new Vector2();
        if (extraFields.length > 2) {
            shadowNum = (int) extraFields[0];
            savedVelo.set(extraFields[1], extraFields[2]);
        }
        final int finalShadowNum = shadowNum;

        if (0 == finalShadowNum) {
            SoundManager.play(state, new SoundLoad(SoundEffect.DARKNESS2)
                    .setVolume(0.4f)
                    .setPitch(0.6f)
                    .setPosition(position));
        }

        RangedHitbox hbox = new RangedHitbox(state, position, PROJ_SIZE, LIFESPAN, new Vector2(),
                user.getHitboxFilter(), true, false, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));

        //if boss is not moving, hbox does nothing
        if (!savedVelo.isZero()) {
            hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                    DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
            hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
            hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.POLLEN_FIRE));
            hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
            hbox.addStrategy((new HitboxStrategy(state, hbox, user.getBodyData()) {

                private float controller;
                private boolean activated;
                private final Vector2 delayVelo = new Vector2();
                @Override
                public void controller(float delta) {
                    controller += delta;

                    //upon activation, projectile moves perpendicular to saved velocity
                    if ((controller > DELAY - SHADOW_NUM * INTERVAL) && !activated) {
                        activated = true;
                        delayVelo.set(savedVelo).rotate90(finalShadowNum % 2 == 1 ? 1 : -1).nor().scl(SPEED);
                        hbox.setLinearVelocity(delayVelo);

                        if (finalShadowNum == SHADOW_NUM / 2) {
                            SoundManager.play(state, new SoundLoad(SoundEffect.DARKNESS1)
                                    .setVolume(0.4f)
                                    .setPitch(0.6f)
                                    .setPosition(hbox.getPixelPosition()));
                        }
                    }
                }
            }));
        }

        return hbox;
    }
}
