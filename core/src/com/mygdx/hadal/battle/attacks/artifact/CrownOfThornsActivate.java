package com.mygdx.hadal.battle.attacks.artifact;

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

public class CrownOfThornsActivate extends SyncedAttacker {

    public static final int THORNS_NUMBER = 6;
    public static final float THORN_DAMAGE = 24.0f;
    private static final float THORN_DURATION = 0.35f;
    private static final float THORN_SPEED = 36.0f;
    private static final float THORN_KNOCKBACK = 15.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(56, 11);

    private static final Sprite PROJ_SPRITE = Sprite.SHRAPNEL;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        SoundEffect.SPIKE.playSourced(state, user.getPixelPosition(), 0.5f);

        Hitbox[] hboxes = new Hitbox[THORNS_NUMBER];

        Vector2 angle = new Vector2(1, 0);
        for (int i = 0; i < THORNS_NUMBER; i++) {
            angle.setAngleDeg(angle.angleDeg() + 60);
            Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, THORN_DURATION,
                    new Vector2(angle).nor().scl(THORN_SPEED), user.getHitboxFilter(),
                    true, false, user, PROJ_SPRITE);

            hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
            hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
            hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
            hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
            hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), THORN_DAMAGE, THORN_KNOCKBACK, DamageSource.CROWN_OF_THORNS,
                    DamageTag.POKING, DamageTag.RANGED));
            hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.25f, true).setSynced(false));

            hboxes[i] = hbox;
        }
        return hboxes;
    }
}