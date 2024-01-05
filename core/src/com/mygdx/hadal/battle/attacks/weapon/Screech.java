package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Screech extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(120, 120);
    public static final int RANGE = 24;
    public static final float LIFESPAN = 0.3f;
    public static final float BASE_DAMAGE = 12.0f;
    private static final float RECOIL = 1.5f;
    private static final float KNOCKBACK = 6.0f;

    private static final Vector2 TRAIL_SIZE = new Vector2(30, 30);
    private static final float TRAIL_SPEED = 120.0f;
    private static final float TRAIL_LIFESPAN = 3.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        user.recoil(startVelocity, RECOIL);

        float distance = RANGE;
        if (extraFields.length >= 1) {
            distance = extraFields[0];
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.POLYGON, 0.0f, 1.0f).setParticleColor(
                HadalColor.RANDOM).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.SCREECHER,
                DamageTag.SOUND, DamageTag.RANGED)
                .setConstantKnockback(true, startVelocity));

        //the trail creates particles along the projectile's length
        Hitbox trail = new RangedHitbox(state, user.getPixelPosition(), TRAIL_SIZE, TRAIL_LIFESPAN, startVelocity.nor().scl(TRAIL_SPEED),
                user.getHitboxFilter(), true, true, user, Sprite.NOTHING);
        trail.setSyncDefault(false);
        trail.setEffectsHit(false);
        trail.setEffectsMovement(false);
        trail.makeUnreflectable();

        trail.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

        trail.addStrategy(new ControllerDefault(state, trail, user.getBodyData()));
        trail.addStrategy(new TravelDistanceDie(state, trail, user.getBodyData(), distance));
        trail.addStrategy(new CreateParticles(state, trail, user.getBodyData(), Particle.POLYGON, 0.0f, 1.0f)
                .setParticleColor(HadalColor.RANDOM).setParticleSize(60).setSyncType(SyncType.NOSYNC));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(trail.getEntityID(), trail, false, ClientState.ObjectLayer.EFFECT);
        }
        return hbox;
    }
}