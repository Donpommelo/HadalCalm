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
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Hex extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 25);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 27.0f;
    private static final float RECOIL = 4.5f;
    private static final float KNOCKBACK = 20.0f;

    private static final float PITCH_SPREAD = 0.4f;
    private static final int SPREAD = 14;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        float pitch = (MathUtils.random() - 0.5f) * PITCH_SPREAD;
        SoundManager.play(state, new SoundLoad(SoundEffect.BOTTLE_ROCKET)
                .setVolume(0.4f)
                .setPitch(1.0f + pitch)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        boolean supercharged = false;
        if (extraFields.length > 0) {
            if (extraFields[0] == 1.0f) {
                supercharged = true;
            }
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.setGravity(1.0f);

        if (supercharged) {
            hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.HEXENHOWITZER,
                    DamageTag.MAGIC, DamageTag.RANGED));
            hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));
        } else {
            //for clients, we don't do the charging so we add this to register kb and damage flashes
            if (!state.isServer()) {
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.HEXENHOWITZER,
                        DamageTag.MAGIC, DamageTag.RANGED));
            }
        }

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BRIGHT).setParticleColor(HadalColor.RANDOM));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));

        return hbox;
    }
}