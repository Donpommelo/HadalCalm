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

public class Slodge extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final float LIFESPAN_MIN = 0.05f;
    public static final float LIFESPAN_MID = 0.25f;
    public static final float LIFESPAN_MAX = 1.5f;
    public static final float BASE_DAMAGE = 7.5f;
    public static final int SHOT_NUMBER = 19;
    private static final float RECOIL = 2.1f;
    private static final float KNOCKBACK = 5.0f;
    private static final int SHOT_SPEED_CAP = 5;

    private static final float SLOW_DURA = 4.0f;
    private static final float SLOW = 0.6f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        int shotNum = 0;
        if (extraFields.length > 0) {
            shotNum = (int) extraFields[0];
            if (extraFields[0] == 1) {
                SoundEffect.DARKNESS1.playSourced(state, user.getPixelPosition(), 0.9f);
            }
        }

        float shotPercent;
        float adjustedLifespan;
        if (shotNum < SHOT_SPEED_CAP) {
            shotPercent = Math.min(1.0f, shotNum / (float) SHOT_SPEED_CAP);
            adjustedLifespan = LIFESPAN_MIN + shotPercent * (LIFESPAN_MID - LIFESPAN_MIN);
        } else {
            shotPercent = Math.min(1.0f, (shotNum - SHOT_SPEED_CAP) / ((float) SHOT_NUMBER - SHOT_SPEED_CAP));
            adjustedLifespan = LIFESPAN_MID + shotPercent * (LIFESPAN_MAX - LIFESPAN_MID);
        }
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, adjustedLifespan, startVelocity, user.getHitboxFilter(),
                false, true, user, Sprite.NOTHING);
        hbox.setGravity(3.0f);
        hbox.setDurability(3);
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.SLODGE_NOZZLE, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SLODGE, 0.0f, 1.0f)
                .setParticleSize(90).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SLODGE_STATUS).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), SLOW_DURA, SLOW, Particle.SLODGE_STATUS));

        return hbox;
    }
}