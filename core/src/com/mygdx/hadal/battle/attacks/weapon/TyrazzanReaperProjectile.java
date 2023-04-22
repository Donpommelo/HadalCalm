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

public class TyrazzanReaperProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(45, 15);

    public static final float PROJECTILE_SPEED_MAX = 60.0f;
    private static final float PROJECTILE_SPEED_MIN = 30.0f;

    public static final float LIFESPAN_MAX = 0.8f;
    private static final float LIFESPAN_MIN = 0.4f;

    public static final float BASE_DAMAGE_MAX = 55.0f;
    public static final float BASE_DAMAGE_MIN = 25.0f;

    private static final float RECOIL = 4.5f;

    private static final float KNOCKBACK_MAX = 19.0f;
    private static final float KNOCKBACK_MIN = 6.0f;

    private static final float SIZE_MAX = 2.5f;
    private static final float SIZE_MIN = 1.0f;

    private static final float SPREAD_MAX = 25.0f;

    private static final float PARTICLE_SIZE_MAX = 90.0f;
    private static final float PARTICLE_SIZE_MIN = 60.0f;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_SHOT_B;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.MAGIC3_BURST.playSourced(state, startPosition, 0.5f, 0.75f);
        user.recoil(startVelocity, RECOIL);

        float effectiveRange = 0.0f;
        if (extraFields.length > 0) {
            effectiveRange = extraFields[0];
        }
        float size = effectiveRange * (SIZE_MAX - SIZE_MIN) + SIZE_MIN;
        float velocity = effectiveRange * (PROJECTILE_SPEED_MAX - PROJECTILE_SPEED_MIN) + PROJECTILE_SPEED_MIN;
        float damage = effectiveRange * (BASE_DAMAGE_MAX - BASE_DAMAGE_MIN) + BASE_DAMAGE_MIN;
        float knockback = effectiveRange * (KNOCKBACK_MAX - KNOCKBACK_MIN) + KNOCKBACK_MIN;
        float lifespan = effectiveRange * (LIFESPAN_MAX - LIFESPAN_MIN) + LIFESPAN_MIN;
        float particleSize = effectiveRange * (PARTICLE_SIZE_MAX - PARTICLE_SIZE_MIN) + PARTICLE_SIZE_MIN;
        int spread = (int) ((1 - effectiveRange) * SPREAD_MAX);

        Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(PROJECTILE_SIZE).scl(size), lifespan,
                startVelocity.nor().scl(velocity), user.getHitboxFilter(), true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.DIATOM_IMPACT_SMALL)
                .setParticleSize(particleSize).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, knockback, DamageSource.TYRRAZZAN_REAPER,
                DamageTag.BULLET, DamageTag.RANGED));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.5f).setSynced(false));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

        return hbox;
    }
}