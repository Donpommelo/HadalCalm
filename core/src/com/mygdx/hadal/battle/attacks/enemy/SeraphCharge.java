package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class SeraphCharge extends SyncedAttacker {

    private static final float BASE_DAMAGE = 35.0f;
    private static final int KNOCKBACK = 25;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Vector2 hboxSize = new Vector2();
        float duration = 0.0f;
        if (extraFields.length > 2) {
            duration = extraFields[0];
            hboxSize.set(extraFields[1], extraFields[2]);
        }

        Hitbox hbox = new Hitbox(state, user.getPixelPosition(), hboxSize, duration, new Vector2(), user.getHitboxFilter(),
                true, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.MELEE)
                .setStaticKnockback(true).setRepeatable(true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()).setRotate(true));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true).setSynced(false));

        return hbox;
    }
}
