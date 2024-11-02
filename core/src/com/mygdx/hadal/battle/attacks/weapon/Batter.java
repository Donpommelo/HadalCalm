package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.TextUtil;

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
        SoundManager.play(state, new SoundLoad(SoundEffect.WOOSH)
                .setPosition(startPosition));

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
            EffectEntityManager.getParticle(state, new ParticleCreate(particle, user)
                    .setLifespan(particleLifespan)
                    .setScale(0.5f)
                    .setColor(TextUtil.getPlayerColor((Player) user)));
        }

        //velocity scales with charge percentage
        float velocity = chargeAmount * (MAX_RECOIL - MIN_RECOIL) + MIN_RECOIL;
        float damage = chargeAmount * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;

        user.getBodyData().addStatus(new StatusComposite(state, LIFESPAN, false, user.getBodyData(), user.getBodyData(),
                new StatChangeStatus(state, Stats.AIR_DRAG, 6.5f, user.getBodyData()),
                new StatChangeStatus(state, Stats.KNOCKBACK_RES, 0.8f, user.getBodyData()),
                new StatChangeStatus(state, Stats.DAMAGE_RES, 0.5f, user.getBodyData())));

        Vector2 push = new Vector2(startVelocity).nor().scl(velocity);
        user.pushMomentumMitigation(push.x, push.y);

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, KNOCKBACK,
                DamageSource.BATTERING_RAM, DamageTag.MELEE)
                .setConstantKnockback(true, startVelocity));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true));

        return hbox;
    }
}