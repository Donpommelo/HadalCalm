package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class SeraphCross extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(165, 165);
    private static final float LIFESPAN = 10.0f;
    private static final float SPEED = 60.0f;
    private static final float BASE_DAMAGE = 9.0f;
    private static final float KNOCKBACK = 12.0f;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_B;

    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());
        if (extraFields.length > 0) {
            if (0 == extraFields[0]) {
                SoundEffect.ROLLING_ROCKET.playSourced(state, user.getPixelPosition(), 0.5f, 2.5f);
            }
        }

        Hitbox[] hboxes = new Hitbox[4];
        hboxes[0] = singleBeam(state, user, startPosition[0], 0);
        hboxes[1] = singleBeam(state, user, startPosition[0], 90);
        hboxes[2] = singleBeam(state, user, startPosition[0], 180);
        hboxes[3] = singleBeam(state, user, startPosition[0], 270);

        return hboxes;
    }

    private Hitbox singleBeam(PlayState state, Schmuck user, Vector2 startPosition, float startAngle) {
        Hitbox laser = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN,
                new Vector2(0, SPEED).setAngleDeg(startAngle), user.getHitboxFilter(), true, false,
                user, PROJ_SPRITE);

        laser.addStrategy(new ControllerDefault(state, laser, user.getBodyData()));
        laser.addStrategy(new AdjustAngle(state, laser, user.getBodyData()));
        laser.addStrategy(new ContactWallDie(state, laser, user.getBodyData()));
        laser.addStrategy(new DieParticles(state, laser, user.getBodyData(), Particle.LASER_IMPACT)
                .setParticleColor(HadalColor.BLUE).setSyncType(SyncType.NOSYNC));
        laser.addStrategy(new DamageStandard(state, laser, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,	DamageSource.ENEMY_ATTACK,
                DamageTag.RANGED, DamageTag.ENERGY));
        laser.addStrategy(new ContactUnitSound(state, laser, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                .setSynced(false));

        return laser;
    }
}