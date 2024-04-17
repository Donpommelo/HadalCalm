package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class StickGrenade extends SyncedAttacker {

    public static final float STICK_GRENADE_EXPLOSION_DAMAGE = 28.0f;
    private static final Vector2 STICK_GRENADE_SIZE = new Vector2(19, 70);
    private static final float STICK_GRENADE_LIFESPAN = 3.0f;
    private static final float STICK_GRENADE_BASE_DAMAGE = 8.0f;
    private static final float STICK_GRENADE_BASE_KNOCKBACK = 3.0f;
    private static final float STICK_GRENADE_EXPLOSION_KNOCKBACK = 12.0f;
    private static final int STICK_GRENADE_EXPLOSION_RADIUS = 100;
    private static final float GRENADE_ROTATION_SPEED = 8.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 0.5f);

        Hitbox hbox = new RangedHitbox(state, startPosition, STICK_GRENADE_SIZE, STICK_GRENADE_LIFESPAN, startVelocity,
                user.getHitboxFilter(), false, false, user, Sprite.CABER);

        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), GRENADE_ROTATION_SPEED));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), STICK_GRENADE_BASE_DAMAGE, STICK_GRENADE_BASE_KNOCKBACK,
                DamageSource.CRIME_DISCOURAGEMENT_STICK, DamageTag.EXPLOSIVE));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), STICK_GRENADE_EXPLOSION_RADIUS, STICK_GRENADE_EXPLOSION_DAMAGE,
                STICK_GRENADE_EXPLOSION_KNOCKBACK, user.getHitboxFilter(), false, DamageSource.CRIME_DISCOURAGEMENT_STICK));
        return hbox;
    }
}