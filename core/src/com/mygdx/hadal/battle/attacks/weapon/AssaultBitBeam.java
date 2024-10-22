package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ranged.AssaultBits;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class AssaultBitBeam extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 20);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 15.0f;
    public static final float KNOCKBACK = 14.0f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_PURPLE;

    @Override
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];

        if (startPosition.length != 0) {
            SoundEffect.SHOOT2.playSourced(state, startPosition[0], 0.6f);
            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i],
                        user.getHitboxFilter(), true,true, user, PROJ_SPRITE);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.ASSAULT_BITS, DamageTag.BULLET, DamageTag.RANGED));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.VIOLET));
                hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.VIOLET));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true)
                        .setSynced(false));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));

                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        if (user instanceof Player player) {
            AssaultBits.fireAllBits(state, player, startPosition);
        }
    }
}
