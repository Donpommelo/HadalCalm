package com.mygdx.hadal.battle.attacks.weapon;

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
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Pepper extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 20);
    public static final float LIFESPAN = 2.0f;
    public static final float BASE_DAMAGE = 19.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 10.0f;

    private static final float PITCH_CHANGE = 0.02f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_GREEN;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        user.recoil(startVelocity, RECOIL);

        float spread = 0.0f;
        if (extraFields.length > 0) {
            spread = extraFields[0];
        }
        float finalSpread = spread;
        float pitch = 1.0f + finalSpread * PITCH_CHANGE;
        SoundEffect.FUTURE_GUN23.playSourced(state, user.getPixelPosition(), 0.4f, pitch);

        RangedHitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.PEPPERGRINDER,
                DamageTag.ENERGY, DamageTag.RANGED));
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(HadalColor.PALE_GREEN));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(HadalColor.PALE_GREEN));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.25f, true));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                float newDegrees = hbox.getStartVelo().angleDeg() + finalSpread;
                hbox.setLinearVelocity(hbox.getLinearVelocity().setAngleDeg(newDegrees));
            }
        });

        return hbox;
    }
}