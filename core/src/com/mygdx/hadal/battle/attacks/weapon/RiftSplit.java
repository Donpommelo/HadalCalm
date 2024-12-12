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
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class RiftSplit extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(30, 120);
    public static final float LIFESPAN = 0.5f;
    public static final float BASE_DAMAGE = 30.0f;
    private static final float KNOCKBACK = 15.0f;

    public static final float SHOCKWAVE_DAMAGE = 17.0f;
    private static final Vector2 SHOCKWAVE_SIZE = new Vector2(56, 64);
    private static final float SHOCKWAVE_LIFESPAN = 0.4f;
    private static final float SHOCKWAVE_INTERVAL = 0.1f;
    private static final float SHOCKWAVE_SPEED = 15.0f;

    private static final Sprite PROJ_SPRITE = Sprite.SPLITTER_A;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.METAL_IMPACT_1)
                .setVolume(0.4f)
                .setPosition(startPosition));

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setRestitution(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.RIFTSPLITTER,
                DamageTag.MELEE, DamageTag.CUTTING));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
                .setParticleColor(HadalColor.TURQUOISE));
        hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
                .setParticleColor(HadalColor.TURQUOISE));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPLITTER_MAIN).setRotate(true));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float controllerCount = SHOCKWAVE_INTERVAL;
            @Override
            public void controller(float delta) {
                controllerCount += delta;

                //projectile repeatedly creates perpendicular projectiles as it moves in a straight line
                while (controllerCount >= SHOCKWAVE_INTERVAL) {
                    controllerCount -= SHOCKWAVE_INTERVAL;
                    createShockwave(0);
                    createShockwave(-1);
                }
            }

            private void createShockwave(int rotate) {
                Hitbox shockwave = new RangedHitbox(state, hbox.getPixelPosition(), SHOCKWAVE_SIZE, SHOCKWAVE_LIFESPAN,
                        new Vector2(hbox.getLinearVelocity()).rotate90(rotate).nor().scl(SHOCKWAVE_SPEED), user.getHitboxFilter(),
                        true, true, user, Sprite.SPLITTER_B);
                shockwave.setSyncDefault(false);

                shockwave.addStrategy(new ControllerDefault(state, shockwave, user.getBodyData()));
                shockwave.addStrategy(new AdjustAngle(state, shockwave, user.getBodyData()));
                shockwave.addStrategy(new ContactWallDie(state, shockwave, user.getBodyData()));
                shockwave.addStrategy(new DamageStandard(state, shockwave, user.getBodyData(), SHOCKWAVE_DAMAGE, KNOCKBACK,
                        DamageSource.RIFTSPLITTER, DamageTag.MELEE, DamageTag.CUTTING));
                shockwave.addStrategy(new ContactWallParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.TURQUOISE));
                shockwave.addStrategy(new ContactUnitParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true)
                        .setParticleColor(HadalColor.TURQUOISE));
                shockwave.addStrategy(new CreateParticles(state, shockwave, user.getBodyData(), Particle.SPLITTER_TRAIL).setRotate(true));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(shockwave.getEntityID(), shockwave, false, ObjectLayer.HBOX);
                }
            }
        });

        return hbox;
    }
}