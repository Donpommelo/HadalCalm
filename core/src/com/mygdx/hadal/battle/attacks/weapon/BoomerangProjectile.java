package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class BoomerangProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    public static final float LIFESPAN = 2.0f;
    public static final float BASE_DAMAGE = 35.0f;
    private static final float KNOCKBACK = 30.0f;
    private static final float RETURN_AMP = 5.0f;
    private static final float BOOMERANG_ROTATION_SPEED = 10.0f;

    private static final Sprite PROJ_SPRITE = Sprite.BOOMERANG;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.BOOMERANG_WHIZ)
                .setPosition(startPosition));

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setRestitution(0.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.BOOMERANG,
                DamageTag.WHACKING, DamageTag.RANGED).setRepeatable(true));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), hbox.getStartVelo().len() * RETURN_AMP));
        hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.WOOSH, 0.5f, true));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.75f, true));
        hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), BOOMERANG_ROTATION_SPEED));

        return hbox;
    }
}