package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
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

public class WaveBeamProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 30);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 35.0f;
    private static final float RECOIL = 12.5f;
    private static final float KNOCKBACK = 28.0f;
    private static final float AMPLITUDE = 1.0f;
    private static final float FREQUENCY = 25.0f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_BLUE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SHOOT1.playSourced(state, startPosition, 0.6f);
        user.recoil(startVelocity, RECOIL);

        //we create an invisible hitbox that moves in a straight line.
        Hitbox center = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        center.setEffectsHit(false);
        center.setEffectsVisual(false);

        center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
        center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
        center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {

            @Override
            public void create() {
                createWaveBeam(90);
                createWaveBeam(-90);
            }

            private void createWaveBeam(float startAngle) {
                Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                        true, true, user, PROJ_SPRITE);
                hbox.setSyncDefault(false);
                hbox.setEffectsMovement(false);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.WAVE_BEAM, DamageTag.ENERGY, DamageTag.RANGED));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.BLUE));
                hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.BLUE));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.4f, true));
                hbox.addStrategy(new WaveEntity(state, hbox, user.getBodyData(), center, AMPLITUDE, FREQUENCY, startAngle));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                }
            }
        });

        return center;
    }
}