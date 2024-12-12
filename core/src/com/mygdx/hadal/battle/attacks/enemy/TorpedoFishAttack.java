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

public class TorpedoFishAttack extends SyncedAttacker {

    private static final float BASE_DAMAGE = 5.0f;
    private static final float KNOCKBACK = 0.5f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(56, 22);
    private static final float LIFESPAN = 5.0f;

    private static final int EXPLOSION_RADIUS = 100;
    private static final float EXPLOSION_DAMAGE = 15.0f;
    private static final float EXPLOSION_KNOCKBACK = 35.0f;

    private static final Sprite PROJ_SPRITE = Sprite.MISSILE_A;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.SPIT)
                .setPitch(0.75f)
                .setPosition(startPosition));

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x),
                PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE,
                EXPLOSION_KNOCKBACK, (short) 0, true, DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setPitch(1.2f));

        return hbox;
    }
}
