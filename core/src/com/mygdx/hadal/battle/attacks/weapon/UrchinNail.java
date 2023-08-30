package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class UrchinNail extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 18);
    public static final float LIFESPAN = 2.5f;
    public static final float LIFESPAN_STUCK = 20.0f;
    public static final float BASE_DAMAGE = 16.0f;
    private static final float RECOIL = 0.9f;
    private static final float KNOCKBACK = 1.0f;
    private static final int SPREAD = 1;

    private static final Vector2 STICKY_SIZE = new Vector2(12, 12);
    private static final float FLASH_LIFESPAN = 1.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NAIL;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.NAILGUN.playSourced(state, startPosition, 0.7f, 1.2f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, STICKY_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setSpriteSize(PROJECTILE_SIZE);
        hbox.setLayer(PlayState.ObjectLayer.EFFECT);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.NEMATOCYDEARM,
                DamageTag.POKING, DamageTag.RANGED));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.6f, true).setSynced(false));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.NAIL_TRAIL, 0.0f, 0.5f)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.NAIL_IMPACT)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.NAIL_BURST)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), false, true) {

            private final Vector2 currentVelo = new Vector2();
            @Override
            public void controller(float delta) {
                super.controller(delta);
                if (!stuckToTarget) {
                    currentVelo.set(hbox.getLinearVelocity());
                    if (hbox.getAngle() != currentVelo.angleDeg()) {
                        hbox.setTransform(hbox.getPosition(), MathUtils.atan2(currentVelo.y , currentVelo.x));
                    }
                }
            }

            @Override
            protected void onStick(HadalEntity target, Body body) {
                super.onStick(target, body);
                Filter filter = hbox.getMainFixture().getFilterData();
                filter.maskBits = (short) 0;
                hbox.getMainFixture().setFilterData(filter);
                hbox.setSprite(Sprite.NAIL_STUCK);
                hbox.setSpriteSize(PROJECTILE_SIZE);
            }

            @Override
            protected void onUnstick() {
                super.onUnstick();
                hbox.setLinearVelocity(currentVelo.setAngleRad(hbox.getAngle()).scl(-1));

                Filter filter = hbox.getMainFixture().getFilterData();
                filter.maskBits = (short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR);
                hbox.getMainFixture().setFilterData(filter);
                hbox.setSprite(Sprite.NAIL);
                hbox.setSpriteSize(PROJECTILE_SIZE);
            }
        }.setStuckLifespan(LIFESPAN_STUCK));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));

        return hbox;
    }
}