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
import com.mygdx.hadal.strategies.hitbox.*;

public class KingKamabokoSlodge extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    private static final int BASE_DAMAGE = 6;
    private static final int KNOCKBACK = 10;
    private static final float LIFESPAN = 2.5f;
    private static final float SLODGE_SLOW = 0.8f;
    private static final float SLODGE_DURATION = 3.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), false, true, user, Sprite.NOTHING);
        hbox.setRestitution(0.5f);
        hbox.setGravity(3.0f);
        hbox.setDurability(3);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SLODGE).setParticleSize(90));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SLODGE_STATUS));
        hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), SLODGE_DURATION, SLODGE_SLOW, Particle.SLODGE_STATUS));

        return hbox;
    }
}
