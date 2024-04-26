package com.mygdx.hadal.battle.attacks.enemy;

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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunBell extends SyncedAttacker {

    private static final Vector2 BELL_SIZE = new Vector2(225, 225);
    private static final Vector2 BELL_SPRITE_SIZE = new Vector2(300, 300);
    private static final float BASE_DAMAGE = 4.5f;
    private static final float HOMING_SPEED = 30.0f;
    private static final int HOMING_RADIUS = 120;
    private static final float KNOCKBACK = 1.0f;
    private static final float LIFESPAN = 12.0f;

    private static final float BELL_INTERVAL = 0.06f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_YELLOW;
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.DOORBELL.playSourced(state, startPosition, 1.0f, 0.6f);

        Hitbox hbox = new Hitbox(state, startPosition, BELL_SIZE, LIFESPAN, new Vector2(),
                user.getHitboxFilter(), false, false, user, PROJ_SPRITE);
        hbox.setSpriteSize(BELL_SPRITE_SIZE);
        hbox.setRestitution(0.2f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIGHTNING, 0.0f, LINGER)
                .setParticleSize(30.0f).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), HOMING_SPEED, HOMING_RADIUS).setSteering(false));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.ZAP, 0.6f, true)
                .setSynced(false));
        hbox.addStrategy(new DamagePulse(state, hbox, user.getBodyData(), hbox.getSize(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setInterval(BELL_INTERVAL));

        return hbox;
    }
}
