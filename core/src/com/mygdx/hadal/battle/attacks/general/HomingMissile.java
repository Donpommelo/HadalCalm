package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class HomingMissile extends SyncedAttacker {

    public static final float TORPEDO_EXPLOSION_DAMAGE = 18.0f;
    private static final float TORPEDO_BASE_DAMAGE = 3.0f;
    private static final float TORPEDO_EXPLOSION_KNOCKBACK = 16.0f;
    private static final float TORPEDO_BASE_KNOCKBACK = 3.0f;
    private static final int TORPEDO_EXPLOSION_RADIUS = 150;
    private static final Vector2 TORPEDO_SIZE = new Vector2(60, 14);
    private static final float TORPEDO_LIFESPAN = 8.0f;
    private static final int TORPEDO_SPREAD = 30;
    private static final float TORPEDO_HOMING = 400;
    private static final int TORPEDO_HOMING_RADIUS = 100;
    private static final Sprite MISSILE_SPRITE = Sprite.MISSILE_B;

    private final DamageSource damageSource;

    public HomingMissile(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        if (extraFields.length > 0) {
            if (extraFields[0] == 1) {
                SoundManager.play(state, new SoundLoad(SoundEffect.DEFLATE)
                        .setPosition(startPosition));
            }
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, TORPEDO_SIZE, TORPEDO_LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, false, user, MISSILE_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), TORPEDO_BASE_DAMAGE, TORPEDO_BASE_KNOCKBACK,
                damageSource, DamageTag.EXPLOSIVE, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), TORPEDO_EXPLOSION_RADIUS, TORPEDO_EXPLOSION_DAMAGE,
                TORPEDO_EXPLOSION_KNOCKBACK, user.getHitboxFilter(), false, damageSource));
        hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), TORPEDO_HOMING, TORPEDO_HOMING_RADIUS));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), TORPEDO_SPREAD));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));

        return hbox;
    }
}