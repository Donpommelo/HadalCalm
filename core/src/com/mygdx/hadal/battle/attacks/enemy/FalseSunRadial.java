package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunRadial extends SyncedAttacker {

    private static final int NUM_SHOTS = 12;

    private static final float BASE_DAMAGE = 24.0f;
    private static final float LIFESPAN = 5.0f;
    private static final float KNOCKBACK = 20.0f;
    private static final float SHOT_1_SPEED = 12.0f;
    private static final float RETURN_AMP = 90.0f;
    private static final float PUSH_INTERVAL = 1.5f;

    private static final Vector2 PROJ_SIZE = new Vector2(80, 40);
    private static final Vector2 PROJ_SPRITE_SIZE = new Vector2(120, 60);

    private static final Sprite PROJ_SPRITE = Sprite.LASER_PURPLE;

    final Vector2 angle = new Vector2(1, 0);
    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundEffect.MAGIC3_BURST.playSourced(state, position, 0.9f, 0.75f);

        Hitbox[] hboxes = new Hitbox[NUM_SHOTS];

        for (int i = 0; i < NUM_SHOTS; i++) {
            angle.setAngleDeg(angle.angleDeg() + 360.0f / NUM_SHOTS);

            Vector2 startVelo = new Vector2(SHOT_1_SPEED, 0).setAngleDeg(angle.angleDeg());
            RangedHitbox hbox = new RangedHitbox(state, position, PROJ_SIZE, LIFESPAN, startVelo, user.getHitboxFilter(),
                    true, false, user, PROJ_SPRITE);
            hbox.setSpriteSize(PROJ_SPRITE_SIZE);
            hbox.setAdjustAngle(true);

            hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
            hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                    DamageSource.ENEMY_ATTACK, DamageTag.RANGED));

            hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), RETURN_AMP));
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LASER_TRAIL)
                    .setParticleColor(HadalColor.VIOLET));
            hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                    .setOffset(true)
                    .setParticleColor(HadalColor.VIOLET));
            hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
            hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
            hbox.addStrategy((new HitboxStrategy(state, hbox, user.getBodyData()) {

                private float controllerCount = PUSH_INTERVAL;
                private final Vector2 push = new Vector2(startVelo);
                @Override
                public void controller(float delta) {

                    controllerCount += delta;

                    //radial shots continuously move towards the player while being periodically pushed outwards to make it pulsate.
                    while (controllerCount >= PUSH_INTERVAL) {
                        controllerCount -= PUSH_INTERVAL;
                        hbox.setLinearVelocity(push.scl(1.5f));
                    }
                }
            }));

            //need to set as not-synced due to return to user strategy setting it as synced, making the angles off
            hbox.setSynced(false);
            hbox.setSyncedDelete(false);

            hboxes[i] = hbox;
        }

        return hboxes;
    }
}