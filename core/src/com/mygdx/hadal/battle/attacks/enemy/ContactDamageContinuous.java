package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class ContactDamageContinuous extends SyncedAttacker {

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        float duration = 0.0f;
        float damage = 0.0f;
        float knockback = 0.0f;
        float interval = 0.0f;
        if (extraFields.length > 3) {
            duration = extraFields[0];
            damage = extraFields[1];
            knockback = extraFields[2];
            interval = extraFields[3];
        }

        final float damageFinal = damage;
        final float knockbackFinal = knockback;
        final float intervalFinal = interval;

        Vector2 hboxSize = new Vector2(startPosition);

        Hitbox hbox = new Hitbox(state, startPosition, hboxSize, duration, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        if (0.0f != duration) {
            hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        }

        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, knockback,
                DamageSource.ENEMY_ATTACK, DamageTag.MELEE).setStaticKnockback(true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()).setRotate(true));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                .setSynced(false));
        hbox.addStrategy((new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float controllerCount;
            @Override
            public void controller(float delta) {
                if (!user.isAlive()) {
                    if (hbox.getState().isServer()) {
                        hbox.queueDeletion();
                    } else {
                        hbox.setAlive(false);
                        ((PlayStateClient) state).removeEntity(hbox.getEntityID());
                    }
                }

                controllerCount += delta;

                while (controllerCount >= intervalFinal) {
                    controllerCount -= intervalFinal;

                    Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hboxSize, intervalFinal, new Vector2(),
                            user.getHitboxFilter(), true, true, user, Sprite.NOTHING);
                    pulse.makeUnreflectable();

                    pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
                    pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), damageFinal, knockbackFinal,
                            DamageSource.ENEMY_ATTACK, DamageTag.MELEE).setStaticKnockback(true));
                    pulse.addStrategy(new FixedToEntity(state, pulse, user.getBodyData(), new Vector2(), new Vector2()).setRotate(true));

                    if (!state.isServer()) {
                        ((PlayStateClient) state).addEntity(pulse.getEntityID(), pulse, false, PlayStateClient.ObjectLayer.HBOX);
                    }
                }
            }
        }));
        return hbox;
    }
}
