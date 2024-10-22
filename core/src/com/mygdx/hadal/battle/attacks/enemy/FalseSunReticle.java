package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunReticle extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(150, 150);
    private static final float LIFESPAN = 1.5f;

    private static final int EXPLOSION_RADIUS = 225;
    private static final float EXPLOSION_DAMAGE = 20.0f;
    private static final float EXPLOSION_KNOCKBACK = 20.0f;

    private static final Sprite PROJ_SPRITE = Sprite.CROSSHAIR;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN,
                new Vector2(), user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
        hbox.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO)
                .setParticleSize(40.0f)
                .setParticleColor(HadalColor.HOT_PINK));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                user.getHitboxFilter(), true, DamageSource.ENEMY_ATTACK));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));

        return hbox;
    }
}
