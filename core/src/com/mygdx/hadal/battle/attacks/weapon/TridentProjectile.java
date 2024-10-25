package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class TridentProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(147, 45);
    public static final float LIFESPAN = 0.33f;
    public static final float LIFESPAN_SMALL = 0.33f;
    public static final float BASE_DAMAGE = 33.0f;
    private static final float RECOIL = 13.5f;
    private static final float KNOCKBACK = 19.0f;
    private static final float DISTANCE = 3.3f;
    private static final float SPEED_MULTIPLIER = 1.33f;

    private static final Sprite PROJ_SPRITE = Sprite.TRIDENT_M;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SHOOT1.playSourced(state, startPosition, 0.6f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.WAVE_BEAM, DamageTag.ENERGY, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.TRIDENT_TRAIL).setRotate(true));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 hboxPosition = new Vector2();
            private final Vector2 childVelocity = new Vector2();
            @Override
            public void die() {
                hboxPosition.set(hbox.getPixelPosition());
                childVelocity.set(startVelocity).scl(SPEED_MULTIPLIER);
                Hitbox center = new RangedHitbox(state, hboxPosition, PROJECTILE_SIZE, LIFESPAN_SMALL, childVelocity, user.getHitboxFilter(),
                        true, true, user, PROJ_SPRITE);
                center.setDurability(3);
                center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
                center.addStrategy(new AdjustAngle(state, center, user.getBodyData()));
                center.addStrategy(new ContactUnitLoseDurability(state, center, user.getBodyData()));
                center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));

                center.addStrategy(new DamageStandard(state, center, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.WAVE_BEAM, DamageTag.ENERGY, DamageTag.RANGED));
                center.addStrategy(new CreateParticles(state, center, user.getBodyData(), Particle.TRIDENT_TRAIL).setRotate(true));

                Hitbox right = new RangedHitbox(state, hboxPosition, PROJECTILE_SIZE, LIFESPAN_SMALL, childVelocity, user.getHitboxFilter(),
                        true, true, user, Sprite.TRIDENT_R);
                right.setDurability(3);

                right.addStrategy(new ControllerDefault(state, right, user.getBodyData()));
                right.addStrategy(new ContactUnitLoseDurability(state, right, user.getBodyData()));
                right.addStrategy(new ContactWallDie(state, right, user.getBodyData()));

                right.addStrategy(new DamageStandard(state, right, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.WAVE_BEAM, DamageTag.ENERGY, DamageTag.RANGED));
                right.addStrategy(new LerpEntity(state, right, user.getBodyData(), center, DISTANCE, LIFESPAN_SMALL, 90));
                right.addStrategy(new CreateParticles(state, right, user.getBodyData(), Particle.TRIDENT_TRAIL).setRotate(true));

                Hitbox left = new RangedHitbox(state, hboxPosition, PROJECTILE_SIZE, LIFESPAN_SMALL, childVelocity, user.getHitboxFilter(),
                        true, true, user, Sprite.TRIDENT_L);
                left.setDurability(3);

                left.addStrategy(new ControllerDefault(state, left, user.getBodyData()));
                left.addStrategy(new ContactUnitLoseDurability(state, left, user.getBodyData()));
                left.addStrategy(new ContactWallDie(state, left, user.getBodyData()));
                left.addStrategy(new DamageStandard(state, left, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.TRIDENT, DamageTag.ENERGY, DamageTag.RANGED));
                left.addStrategy(new LerpEntity(state, left, user.getBodyData(), center, DISTANCE, LIFESPAN_SMALL, -90));
                left.addStrategy(new CreateParticles(state, left, user.getBodyData(), Particle.TRIDENT_TRAIL).setRotate(true));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(center.getEntityID(), center, false, ObjectLayer.HBOX);
                    ((ClientState) state).addEntity(right.getEntityID(), right, false, ObjectLayer.HBOX);
                    ((ClientState) state).addEntity(left.getEntityID(), left, false, ObjectLayer.HBOX);
                }
            }
        });

        return hbox;
    }
}