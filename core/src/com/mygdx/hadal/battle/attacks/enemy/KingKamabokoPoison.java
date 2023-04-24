package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class KingKamabokoPoison extends SyncedAttacker {

    private static final int BASE_DAMAGE = 5;
    private static final int KNOCKBACK = 5;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(70, 70);
    private static final float LIFESPAN = 2.5f;

    private static final int POISON_RADIUS = 150;
    private static final float POISON_DAMAGE = 0.4f;
    private static final float POISON_DURATION = 4.0f;

    private static final Sprite PROJ_SPRITE = Sprite.FUGU;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        SoundEffect.LAUNCHER4.playSourced(state, startPosition, 0.4f, 0.8f);

        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), false, true, user, PROJ_SPRITE);
        hbox.setGravity(3.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.POISON, DamageTag.RANGED));
        hbox.addStrategy(new DiePoison(state, hbox, user.getBodyData(), POISON_RADIUS, POISON_DAMAGE, POISON_DURATION,
                user.getHitboxFilter(), DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DieRagdoll(state, hbox, user.getBodyData(), false));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DEFLATE, 0.25f).setSynced(false));

        return hbox;
    }
}
