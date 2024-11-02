package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class IcebergProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(48, 48);
    public static final float LIFESPAN = 3.0f;
    public static final float BASE_DAMAGE = 45.0f;
    private static final float RECOIL = 15.0f;
    private static final float KNOCKBACK = 30.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ICEBERG;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.ICE_IMPACT)
                .setVolume(0.9f)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setGravity(5);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.ICEBERG,
                DamageTag.WHACKING, DamageTag.RANGED).setRepeatable(true));
        hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.CHILL_HIT, 0.6f, true));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            float lastX;
            @Override
            public void controller(float delta) {

                //when we hit a wall, we reverse momentum instead of staying still.
                //This is necessary b/c we cannot turn restitution up without having the projectile bounce instead of slide,
                if (hbox.getLinearVelocity().x == 0) {
                    hbox.setLinearVelocity(-lastX, hbox.getLinearVelocity().y);
                }

                lastX = hbox.getLinearVelocity().x;
            }
        });
        return hbox;
    }
}