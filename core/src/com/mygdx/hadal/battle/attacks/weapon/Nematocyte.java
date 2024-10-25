package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Nematocyte extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(91, 35);
    public static final float LIFESPAN = 3.0f;
    public static final float LIFESPAN_STUCK = 70.0f;
    public static final float BASE_DAMAGE = 33.0f;
    private static final float RECOIL = 2.0f;
    private static final float KNOCKBACK = 20.0f;
    private static final int SPREAD = 5;

    private static final Vector2 STICKY_SIZE = new Vector2(70, 25);
    private static final float FLASH_LIFESPAN = 1.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NEMATOCYTE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ATTACK1.playSourced(state, startPosition, 0.4f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, STICKY_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setSpriteSize(PROJECTILE_SIZE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.NEMATOCYDEARM,
                DamageTag.POKING, DamageTag.RANGED).setStaticKnockback(true));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DANGER_BLUE));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                .setOffset(true)
                .setParticleColor(HadalColor.SKY_BLUE));
        hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, false) {

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
        }.setStuckLifespan(LIFESPAN_STUCK));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));

        return hbox;
    }
}