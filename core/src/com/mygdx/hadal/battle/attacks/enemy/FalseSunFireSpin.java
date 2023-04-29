package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
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

public class FalseSunFireSpin extends SyncedAttacker {

    private static final int BASE_DAMAGE = 6;
    private static final int KNOCKBACK = 10;
    private static final float LIFESPAN = 2.0f;
    private static final int BURN_DAMAGE = 3;
    private static final float BURN_DURATION = 4.0f;

    private static final Vector2 PROJ_SIZE = new Vector2(80, 80);
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJ_SIZE.x), PROJ_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), true, false, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitBurn(state, hbox, user.getBodyData(), BURN_DURATION, BURN_DAMAGE, DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED, DamageTag.FIRE));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, LINGER)
                .setParticleSize(36.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}
