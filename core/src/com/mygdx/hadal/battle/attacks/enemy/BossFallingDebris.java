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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class BossFallingDebris extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);

    private static final float BASE_DAMAGE = 9.0f;
    private static final float KNOCKBACK = 15.0f;
    private static final float LIFESPAN = 3.0f;

    private static final Sprite[] DEBRIS_SPRITES = {Sprite.METEOR_A, Sprite.METEOR_B, Sprite.METEOR_C, Sprite.METEOR_D, Sprite.METEOR_E, Sprite.METEOR_F};

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Sprite projSprite = DEBRIS_SPRITES[MathUtils.random(DEBRIS_SPRITES.length - 1)];
        Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(), user.getHitboxFilter(), true, true, user, projSprite);
        hbox.setDurability(2);
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true).setSynced(false));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.4f).setSynced(false));

        return hbox;
    }
}
