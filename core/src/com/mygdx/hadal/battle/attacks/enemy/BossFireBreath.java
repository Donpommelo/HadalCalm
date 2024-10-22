package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class BossFireBreath extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    private static final int BASE_DAMAGE = 4;
    private static final int KNOCKBACK = 10;
    private static final float BURN_DURATION = 4.0f;
    private static final int BURN_DAMAGE = 3;

    private static final float LIFESPAN1 = 1.4f;
    private static final float LIFESPAN2 = 1.7f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        float lifespan = 0.0f;
        if (extraFields.length > 0) {
            lifespan = extraFields[0] == 1 ? LIFESPAN1 : LIFESPAN2;
        }

        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
                lifespan, startVelocity, user.getHitboxFilter(), false, true, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitBurn(state, hbox, user.getBodyData(), BURN_DURATION, BURN_DAMAGE, DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED, DamageTag.FIRE));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE));

        return hbox;
    }
}
