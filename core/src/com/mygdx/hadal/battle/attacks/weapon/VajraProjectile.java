package com.mygdx.hadal.battle.attacks.weapon;

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

public class VajraProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(70, 24);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 44.0f;
    private static final float RECOIL = 4.0f;
    private static final float KNOCKBACK = 5.0f;

    public static final float CHAIN_DAMAGE = 20.0f;
    public static final int CHAIN_AMOUNT = 5;
    private static final int CHAIN_RADIUS = 20;

    private static final Sprite PROJ_SPRITE = Sprite.LIGHTNING;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.THUNDER.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitShock(state, hbox, user.getBodyData(), CHAIN_DAMAGE, CHAIN_RADIUS, CHAIN_AMOUNT,
                user.getHitboxFilter(), DamageSource.VAJRA));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.VAJRA,
                DamageTag.LIGHTNING, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIGHTNING_CHARGE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}