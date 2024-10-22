package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.TextUtil;

public class HydraulicUppercutProjectile extends SyncedAttacker {

    public static final float BASE_DAMAGE = 60.0f;
    public static final float LIFESPAN = 0.5f;

    private static final Vector2 HITBOX_SIZE = new Vector2(150, 150);
    private static final float KNOCKBACK = 75.0f;
    private static final float RECOIL = 180.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.WOOSH.playSourced(state, user.getPixelPosition(), 1.0f);

        boolean right = startVelocity.x > 0;

        Particle particle = Particle.MOREAU_LEFT;
        if (right) {
            particle = Particle.MOREAU_RIGHT;
        }

        if (user instanceof Player player) {
            EffectEntityManager.getParticle(state, new ParticleCreate(particle, user)
                    .setLifespan(1.0f)
                    .setScale(0.5f)
                    .setColor(TextUtil.getPlayerColor(player)));

        }

        user.getBodyData().addStatus(new Invulnerability(state, 0.9f, user.getBodyData(), user.getBodyData()));
        user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 6.0f, user.getBodyData(),
                user.getBodyData()));

        user.pushMomentumMitigation(0, RECOIL);

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.IMPACT);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData() , Particle.SPARKS));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.HYDRAULIC_UPPERCUT, DamageTag.MELEE).setStaticKnockback(true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true));

        return hbox;
    }
}