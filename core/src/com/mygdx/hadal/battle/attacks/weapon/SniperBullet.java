package com.mygdx.hadal.battle.attacks.weapon;

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

public class SniperBullet extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(120, 12);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 55.0f;
    public static final float BONUS_DAMAGE = 1.75f;
    private static final float RECOIL = 15.0f;
    private static final float KNOCKBACK = 45.0f;
    private static final float MAX_CHARGE = 0.22f;

    private static final Sprite PROJ_SPRITE = Sprite.BULLET;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.GUN1.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setRestitution(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageHeadshot(state, hbox, user.getBodyData(), BONUS_DAMAGE, MAX_CHARGE));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.SNIPER_RIFLE, DamageTag.BULLET, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BULLET_TRAIL, 0.0f, 0.5f)
                .setRotate(true).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_BODY_HIT, 0.5f, true).setSynced(false));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_CONCRETE_HIT, 0.5f).setSynced(false));

        return hbox;
    }
}