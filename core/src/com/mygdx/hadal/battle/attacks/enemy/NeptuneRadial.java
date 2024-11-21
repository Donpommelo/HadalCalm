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
import com.mygdx.hadal.strategies.hitbox.*;

public class NeptuneRadial extends SyncedAttacker {

    private static final int NUM_SHOTS = 15;

    private static final float BASE_DAMAGE = 22.0f;
    private static final float LIFESPAN = 8.0f;
    private static final float KNOCKBACK = 20.0f;
    private static final float SPEED = 6.0f;

    private static final Vector2 PROJ_SIZE = new Vector2(40, 40);
    private static final Vector2 PROJ_SPRITE_SIZE = new Vector2(60, 60);

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_SHOT_A;

    final Vector2 angle = new Vector2(1, 0);
    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC3_BURST)
                .setVolume(1.1f)
                .setPitch(0.75f)
                .setPosition(position));

        Hitbox[] hboxes = new Hitbox[NUM_SHOTS];

        for (int i = 0; i < NUM_SHOTS; i++) {
            angle.setAngleDeg(angle.angleDeg() + 360.0f / NUM_SHOTS);

            Vector2 startVelo = new Vector2(SPEED, 0).setAngleDeg(angle.angleDeg());
            RangedHitbox hbox = new RangedHitbox(state, position, PROJ_SIZE, LIFESPAN, startVelo,
                    user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
            hbox.setSpriteSize(PROJ_SPRITE_SIZE);
            hbox.setAdjustAngle(true);

            hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
            hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                    DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE));
            hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.DIATOM_IMPACT_SMALL));
            hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
            hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
            hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));

            hboxes[i] = hbox;
        }

        return hboxes;
    }
}