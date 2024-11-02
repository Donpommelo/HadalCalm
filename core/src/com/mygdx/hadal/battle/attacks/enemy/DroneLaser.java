package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class DroneLaser extends SyncedAttacker {

    private static final float BASE_DAMAGE = 8.0f;
    private static final float KNOCKBACK = 6.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(60, 30);
    private static final float LIFESPAN = 3.0f;
    private static final int SPREAD = 12;

    private static final Sprite PROJ_SPRITE = Sprite.LASER;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.LASER2)
                .setVolume(0.25f)
                .setPosition(startPosition));

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
                LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));

        return hbox;
    }
}
