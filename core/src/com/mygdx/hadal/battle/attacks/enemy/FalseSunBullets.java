package com.mygdx.hadal.battle.attacks.enemy;

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

public class FalseSunBullets extends SyncedAttacker {

    private static final float LIFESPAN = 10.0f;
    private static final float BASE_DAMAGE = 10.0f;
    private static final float KNOCKBACK = 15.0f;
    private static final Vector2 PROJ_SIZE = new Vector2(70, 35);
    private static final Vector2 SPRITE_SIZE = new Vector2(100, 50);

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Sprite projSprite;
        HadalColor projColor;
        if (startVelocity.x >= 0) {
            projSprite = Sprite.LASER_TURQUOISE;
            projColor = HadalColor.TURQUOISE;
        } else {
            projSprite = Sprite.LASER_GREEN;
            projColor = HadalColor.PALE_GREEN;
        }

        RangedHitbox hbox = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN, startVelocity,
                user.getHitboxFilter(), true, false, user, projSprite);
        hbox.setSpriteSize(SPRITE_SIZE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(projColor));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LASER_TRAIL)
                .setParticleColor(projColor));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                .setSynced(false));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));

        return hbox;
    }
}
