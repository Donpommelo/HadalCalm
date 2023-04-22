package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class XBomb extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(80, 40);
    public static final float LIFESPAN = 0.5f;
    public static final float BASE_DAMAGE = 24.0f;
    private static final float RECOIL = 12.0f;
    private static final float KNOCKBACK = 30.0f;

    public static final float CROSS_DAMAGE = 24.0f;
    private static final Vector2 CROSS_SIZE = new Vector2(700, 40);
    private static final float CROSS_LIFESPAN = 0.25f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_TURQUOISE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.FIRE9.playSourced(state, startPosition, 0.25f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setGravity(2.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.X_BOMBER, DamageTag.ENERGY, DamageTag.RANGED));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.6f).setSynced(false));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setParticleColor(HadalColor.CYAN).setSyncType(SyncType.NOSYNC));

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void die() {

                //create 2 perpendicular projectiles
                createCross(1);
                createCross(-1);
            }

            private void createCross(int rotate) {
                Hitbox cross = new RangedHitbox(state, hbox.getPixelPosition(), CROSS_SIZE, CROSS_LIFESPAN, new Vector2(),
                        user.getHitboxFilter(), true, true, user, PROJ_SPRITE) {

                    @Override
                    public void create() {
                        super.create();
                        setTransform(getPosition().x, getPosition().y, MathUtils.PI / 4 * rotate);
                    }
                };
                cross.setSyncDefault(false);
                cross.makeUnreflectable();
                cross.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));

                cross.addStrategy(new ControllerDefault(state, cross, user.getBodyData()));
                cross.addStrategy(new DamageStandard(state, cross, user.getBodyData(), CROSS_DAMAGE, KNOCKBACK,
                        DamageSource.X_BOMBER, DamageTag.ENERGY, DamageTag.RANGED)
                        .setConstantKnockback(true, startVelocity));
                cross.addStrategy(new ContactUnitParticles(state, cross, user.getBodyData(), Particle.LASER_IMPACT).setParticleColor(
                        HadalColor.CYAN).setDrawOnSelf(false).setSyncType(SyncType.NOSYNC));
                cross.addStrategy(new Static(state, cross, user.getBodyData()));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(cross.getEntityID(), cross, false, ClientState.ObjectLayer.HBOX);
                }
            }
        });

        return hbox;
    }
}