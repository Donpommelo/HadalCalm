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

public class MinigunBullet extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 10);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 20.0f;
    private static final float RECOIL = 0.25f;
    private static final float KNOCKBACK = 6.0f;

    private static final float PITCH_SPREAD = 0.4f;
    private static final int SPREAD = 8;

    private static final Sprite PROJ_SPRITE = Sprite.BULLET;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_BODY_HIT, 0.5f, true)
                .setPitchSpread(PITCH_SPREAD));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_CONCRETE_HIT, 0.5f)
                .setPitchSpread(PITCH_SPREAD));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MINIGUN,
                DamageTag.BULLET, DamageTag.RANGED));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BULLET_TRAIL).setRotate(true));

        return hbox;
    }
}