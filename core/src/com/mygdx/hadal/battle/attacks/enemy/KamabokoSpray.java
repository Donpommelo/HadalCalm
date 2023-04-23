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
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class KamabokoSpray extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    private static final float LIFESPAN = 1.25f;

    private static final int BASE_DAMAGE = 10;
    private static final int KNOCKBACK = 6;
    private static final float LINGER = 1.0f;

    private static final float TOTAL_DURATION = 2.4f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        RangedHitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, user.getSize().x),
                PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, LINGER));

        if (extraFields.length > 0) {
            if (extraFields[0] == 0) {
                SoundEntity sound = new SoundEntity(state, user, SoundEffect.OOZE, TOTAL_DURATION, 0.6f, 2.0f,
                        true, true, SyncType.NOSYNC);
                if (!state.isServer()) {
                    ((ClientState) state).addEntity(sound.getEntityID(), sound, false, ClientState.ObjectLayer.EFFECT);
                }
            }
        }

        return hbox;
    }
}
