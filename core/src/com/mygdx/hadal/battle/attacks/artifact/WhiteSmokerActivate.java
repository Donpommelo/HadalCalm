package com.mygdx.hadal.battle.attacks.artifact;

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

public class WhiteSmokerActivate extends SyncedAttacker {

    private static final float BASE_DAMAGE = 4.0f;
    private static final float KNOCKBACK = 2.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    private static final float LIFESPAN = 0.25f;

    public static final float FIRE_DURATION = 4.0f;
    public static final float FIRE_DAMAGE = 3.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        RangedHitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity,
                user.getHitboxFilter(), false, true, user, Sprite.NOTHING);
        hbox.setDurability(3);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitBurn(state, hbox, user.getBodyData(), FIRE_DURATION, FIRE_DAMAGE, DamageSource.WHITE_SMOKER));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.WHITE_SMOKER, DamageTag.FIRE, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE));

        return hbox;
    }
}