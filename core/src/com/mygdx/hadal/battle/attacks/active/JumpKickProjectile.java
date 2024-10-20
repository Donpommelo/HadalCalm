package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
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
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.TextUtil;

public class JumpKickProjectile extends SyncedAttacker {

    public static final float BASE_DAMAGE = 20.0f;

    private static final Vector2 HITBOX_SIZE = new Vector2(90, 120);
    private static final float LIFESPAN = 0.5f;
    private static final float PARTICLE_LIFESPAN = 0.6f;
    private static final float RECOIL = 150.0f;
    private static final float KNOCKBACK = 90.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.WOOSH.playSourced(state, startPosition, 1.0f);

        boolean right = startVelocity.x > 0;
        Particle particle = Particle.MOREAU_LEFT;
        if (right) {
            particle = Particle.MOREAU_RIGHT;
        }

        if (user instanceof Player player) {
            ParticleEntity particles = new ParticleEntity(user.getState(), user, particle, 1.5f, 1.0f,
                    true, SyncType.NOSYNC)
                    .setScale(0.5f).setPrematureOff(PARTICLE_LIFESPAN)
                    .setColor(TextUtil.getPlayerColor(player));
            if (!state.isServer()) {
                ((ClientState) state).addEntity(particles.getEntityID(), particles, false, ObjectLayer.EFFECT);
            }
        }

        user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 7.5f, user.getBodyData(), user.getBodyData()));
        Vector2 push = new Vector2(startVelocity).nor().scl(RECOIL);
        user.pushMomentumMitigation(push.x, push.y);

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.JUMP_KICK,
                DamageTag.MELEE).setStaticKnockback(true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true).setSynced(false));
        hbox.addStrategy(new ContactUnitKnockbackDamage(state, hbox, user.getBodyData(), DamageSource.JUMP_KICK));

        return hbox;
    }
}