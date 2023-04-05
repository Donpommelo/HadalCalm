package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.*;

public class Batter extends SyncedAttacker {

    public static final Vector2 HITBOX_SIZE = new Vector2(90, 120);
    public static final float LIFESPAN = 0.5f;
    public static final float MIN_DAMAGE = 20.0f;
    public static final float MAX_DAMAGE = 70.0f;
    private static final float MIN_RECOIL = 25.0f;
    private static final float MAX_RECOIL = 175.0f;
    private static final float KNOCKBACK = 40.0f;

    public static final float DAMAGE_REDUCTION = 0.5f;
    private static final float MIN_PARTICLE_TERMINATION = 0.9f;
    private static final float MAX_PARTICLE_TERMINATION = 0.6f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.WOOSH.playSourced(state, startPosition, 1.0f);

        float chargeAmount = 0.0f;
        if (extraFields.length > 0) {
            chargeAmount = extraFields[0];
        }

        boolean right = startVelocity.x > 0;

        Particle particle = Particle.MOREAU_LEFT;
        if (right) {
            particle = Particle.MOREAU_RIGHT;
        }

        float particleLifespan = (1 - chargeAmount) * (MIN_PARTICLE_TERMINATION - MAX_PARTICLE_TERMINATION) + MAX_PARTICLE_TERMINATION;

        if (user instanceof Player) {
            ParticleEntity particles = new ParticleEntity(user.getState(), user, particle, 1.5f, 1.0f, true, SyncType.NOSYNC)
                    .setScale(0.5f).setPrematureOff(particleLifespan)
                    .setColor(WeaponUtils.getPlayerColor((Player) user));
            if (!state.isServer()) {
                ((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.EFFECT);
            }
        }

        //velocity scales with charge percentage
        float velocity = chargeAmount * (MAX_RECOIL - MIN_RECOIL) + MIN_RECOIL;
        float damage = chargeAmount * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;

        user.getBodyData().addStatus(new StatusComposite(state, LIFESPAN, false, user.getBodyData(), user.getBodyData(),
                new StatChangeStatus(state, Stats.AIR_DRAG, 6.5f, user.getBodyData()),
                new StatChangeStatus(state, Stats.DAMAGE_RES, 0.5f, user.getBodyData())).setClientIndependent(true));

        Vector2 push = new Vector2(startVelocity).nor().scl(velocity);
        user.pushMomentumMitigation(push.x, push.y);

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, KNOCKBACK,
                DamageSource.BATTERING_RAM, DamageTag.MELEE)
                .setConstantKnockback(true, startVelocity));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true).setSynced(false));

        return hbox;
    }
}