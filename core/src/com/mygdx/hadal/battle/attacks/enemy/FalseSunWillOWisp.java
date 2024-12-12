package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunWillOWisp extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(25, 25);
    private static final float LIFESPAN = 10.0f;
    private static final float BASE_DAMAGE = 11.0f;
    private static final float KNOCKBACK = 12.0f;
    private static final float HOMING = 50.0f;
    private static final int HOMING_RADIUS = 120;
    private static final int SPREAD = 30;
    private static final float PITCH_SPREAD = 0.25f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        float pitch = (MathUtils.random() - 0.5f) * PITCH_SPREAD;
        SoundManager.play(state, new SoundLoad(SoundEffect.BOTTLE_ROCKET)
                .setVolume(0.25f)
                .setPitch(0.5f + pitch)
                .setPosition(startPosition));

        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJ_SIZE.x), PROJ_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BRIGHT)
                .setParticleColor(HadalColor.RANDOM));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
        hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), HOMING, HOMING_RADIUS).setSteering(false));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

        return hbox;
    }
}
