package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
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

public class LaserGuidedRocketProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(80, 30);
    public static final float LIFESPAN = 6.0f;
    public static final float BASE_DAMAGE = 20.0f;
    private static final float RECOIL = 8.0f;
    private static final float KNOCKBACK = 0.0f;
    private static final float HOME_POWER = 400.0f;

    public static final float EXPLOSION_DAMAGE = 55.0f;
    private static final int EXPLOSION_RADIUS = 200;
    private static final float EXPLOSION_KNOCKBACK = 35.0f;

    private static final Sprite PROJ_SPRITE = Sprite.MISSILE_C;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ROLLING_ROCKET.playSourced(state, startPosition, 0.4f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.LASER_GUIDED_ROCKET, DamageTag.EXPLOSIVE, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                (short) 0, false, DamageSource.LASER_GUIDED_ROCKET));
        hbox.addStrategy(new HomingMouse(state, hbox, user.getBodyData(), HOME_POWER));
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION9, 0.6f).setSynced(false));

        return hbox;
    }
}