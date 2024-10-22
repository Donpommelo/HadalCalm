package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Flounder extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(36, 30);
    public static final float LIFESPAN = 2.0f;
    public static final float BASE_DAMAGE = 15.0f;
    private static final float RECOIL = 30.0f;
    private static final float KNOCKBACK = 12.0f;

    private static final float PITCH_SPREAD = 0.4f;
    private static final int SPREAD = 20;

    private static final Sprite[] PROJ_SPRITES = {Sprite.FLOUNDER_A, Sprite.FLOUNDER_B};

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (startPosition.length != 0) {
            SoundEffect.SHOTGUN.playSourced(state, startPosition[0], 0.75f, 0.75f);
            user.recoil(weaponVelocity, RECOIL);

            for (int i = 0; i < startPosition.length; i++) {

                int randomIndex = MathUtils.random(PROJ_SPRITES.length - 1);
                Sprite projSprite = PROJ_SPRITES[randomIndex];

                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i],
                        user.getHitboxFilter(), true, true, user, projSprite);
                hbox.setGravity(1.5f);
                hbox.setDurability(2);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.FLOUNDERBUSS, DamageTag.RANGED));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.25f, true)
                        .setPitchSpread(PITCH_SPREAD).setSynced(false));
                hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WET_SPLAT, 0.25f)
                        .setPitchSpread(PITCH_SPREAD).setSynced(false));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}