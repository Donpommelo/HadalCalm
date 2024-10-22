package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class BossBouncyBall extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);

    private static final float BASE_DAMAGE = 12.0f;
    private static final float KNOCKBACK = 12.0f;
    private static final float LIFESPAN = 7.5f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_RED;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        SoundEffect.SPRING.playUniversal(state, startPosition, 0.5f, 0.8f, false);

        Hitbox hbox = new Hitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), false, true, user, PROJ_SPRITE);
        hbox.setGravity(10.0f);
        hbox.setRestitution(1);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SPRING, 0.1f).setSynced(false));

        return hbox;
    }
}
