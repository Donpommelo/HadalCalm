package com.mygdx.hadal.battle.attacks.enemy;

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
import com.mygdx.hadal.strategies.hitbox.*;

public class TorpedoFishAttack extends SyncedAttacker {

    private static final float baseDamage = 5.0f;
    private static final float knockback = 0.5f;
    private static final Vector2 projectileSize = new Vector2(56, 22);
    private static final float lifespan = 5.0f;

    private static final int explosionRadius = 100;
    private static final float explosionDamage = 15.0f;
    private static final float explosionKnockback = 35.0f;

    private static final Sprite projSprite = Sprite.MISSILE_A;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIT.playSourced(state, startPosition, 1.0f, 0.75f);

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, user.getSize().x),
                projectileSize, lifespan, startVelocity, user.getHitboxFilter(), true, true, user, projSprite);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f)
        .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage,
                explosionKnockback, (short) 0, true, DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setPitch(1.2f));

        return hbox;
    }
}
