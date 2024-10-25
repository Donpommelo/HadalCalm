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

public class DeepSmelt extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 15);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 15.0f;
    private static final float RECOIL = 4.0f;
    private static final float KNOCKBACK = 22.0f;

    private static final float PITCH_SPREAD = 0.4f;

    private static final Sprite PROJ_SPRITE = Sprite.SLAG;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (startPosition.length != 0) {
            float pitch = (MathUtils.random() - 0.5f) * PITCH_SPREAD;
            SoundEffect.METAL_IMPACT_1.playSourced(state, startPosition[0], 0.5f, 1.0f + pitch);
            user.recoil(startVelocity[0], RECOIL);

            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i],
                        user.getHitboxFilter(), true, true, user, PROJ_SPRITE);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.DEEP_SEA_SMELTER, DamageTag.RANGED));
                hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.METAL_IMPACT_2, 0.3f));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.2f, true));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.YELLOW));
                hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.YELLOW));

                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}