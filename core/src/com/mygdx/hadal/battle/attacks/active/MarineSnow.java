package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class MarineSnow extends SyncedAttacker {

    public static final float DURATION = 0.5f;
    public static final float PROJECTILE_DAMAGE = 24.0f;
    public static final float SLOW_DURATION = 5.0f;
    public static final float SLOW_SLOW = 0.75f;

    private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
    private static final float PROJECTILE_KB = 15.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.FREEZE_IMPACT.playSourced(state, startPosition, 0.9f, 0.5f);

        Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, DURATION, new Vector2(), user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), PROJECTILE_DAMAGE, PROJECTILE_KB,
                DamageSource.MARINE_SNOWGLOBE, DamageTag.RANGED));
        hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), SLOW_DURATION, SLOW_SLOW, Particle.ICE_CLOUD));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, 2.0f)
                .setParticleSize(25.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}