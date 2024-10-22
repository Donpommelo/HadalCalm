package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class StutterLaser extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(80, 40);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 18.0f;
    private static final float RECOIL = 3.5f;
    private static final float KNOCKBACK = 5.0f;

    private static final float PITCH_SPREAD = 0.4f;
    private static final int SPREAD = 8;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_ORANGE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        float pitch = (MathUtils.random() - 0.5f) * PITCH_SPREAD;
        SoundEffect.LASER2.playSourced(state, user.getPixelPosition(), 0.5f, 1.0f + pitch);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(HadalColor.ORANGE));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(HadalColor.ORANGE));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true).setSynced(false));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.STUTTERGUN, DamageTag.ENERGY, DamageTag.RANGED));

        return hbox;
    }
}