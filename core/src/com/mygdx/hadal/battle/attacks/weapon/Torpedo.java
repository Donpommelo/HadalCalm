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

public class Torpedo extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 18);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 15.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 0.0f;

    public static final float EXPLOSION_DAMAGE = 40.0f;
    private static final int EXPLOSION_RADIUS = 150;
    private static final float EXPLOSION_KNOCKBACK = 25.0f;

    private static final Sprite PROJ_SPRITE = Sprite.TORPEDO;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ROCKET.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.TORPEDO_LAUNCHER, DamageTag.EXPLOSIVE, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                (short) 0, false, DamageSource.TORPEDO_LAUNCHER));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.5f).setSynced(false));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));

        return hbox;
    }
}