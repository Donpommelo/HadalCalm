package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
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

public class FalseSunApocalypse extends SyncedAttacker {

    private static final Vector2 LASER_SPRITE_SIZE = new Vector2(180, 90);
    private static final Vector2 LASER_SIZE = new Vector2(120, 60);
    private static final float LIFESPAN = 10.0f;
    private static final float LASER_DAMAGE = 7.5f;
    private static final float LASER_KB = 12.0f;

    private static final Vector2 RUBBLE_SIZE = new Vector2(40, 40);
    private static final float RUBBLE_SPEED = 20.0f;
    private static final float RUBBLE_LIFESPAN = 5.0f;
    private static final float RUBBLE_DAMAGE = 8.0f;
    private static final float RUBBLE_KB = 10.0f;
    private static final int RUBBLE_SPREAD = 10;

    private static final float APOCALYPSE_LASERAMPLITUDE = 4.0f;
    private static final float APOCALYPSE_LASER_FREQUENCY = 25.0f;

    private static final Sprite PROJ_SPRITE = Sprite.LASER_BLUE;
    private static final Sprite[] DEBRIS_SPRITES = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox laser = new RangedHitbox(state, startPosition, LASER_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, false, user, PROJ_SPRITE);
        laser.setSpriteSize(LASER_SPRITE_SIZE);

        laser.addStrategy(new ControllerDefault(state, laser, user.getBodyData()));
        laser.addStrategy(new AdjustAngle(state, laser, user.getBodyData()));
        laser.addStrategy(new DieParticles(state, laser, user.getBodyData(), Particle.LASER_IMPACT)
                .setParticleColor(HadalColor.BLUE).setSyncType(SyncType.NOSYNC));
        laser.addStrategy(new ContactWallLoseDurability(state, laser, user.getBodyData()));
        laser.addStrategy(new DamageStandard(state, laser, user.getBodyData(), LASER_DAMAGE, LASER_KB,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED, DamageTag.ENERGY));
        laser.addStrategy(new ContactUnitSound(state, laser, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                .setSynced(false));

        laser.addStrategy(new HitboxStrategy(state, laser, user.getBodyData()) {

            @Override
            public void create() {
                createWaveBeam(90);
                createWaveBeam(-90);
            }

            //each laser kicks up debris that flies upwards
            @Override
            public void die() {
                int randomIndex = MathUtils.random(DEBRIS_SPRITES.length - 1);
                Sprite projSprite = DEBRIS_SPRITES[randomIndex];
                Hitbox frag = new Hitbox(state, new Vector2(hbox.getPixelPosition()), RUBBLE_SIZE, RUBBLE_LIFESPAN,
                        new Vector2(0, RUBBLE_SPEED), user.getHitboxFilter(), true, false, user, projSprite);
                frag.setGravity(1.0f);

                frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), RUBBLE_DAMAGE, RUBBLE_KB,
                        DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
                frag.addStrategy(new ContactWallDie(state, frag, user.getBodyData()));
                frag.addStrategy(new ContactWallParticles(state, frag, user.getBodyData(), Particle.SPARKS)
                        .setSyncType(SyncType.NOSYNC));
                frag.addStrategy(new ContactUnitSound(state, frag, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
                frag.addStrategy(new Spread(state, frag, user.getBodyData(), RUBBLE_SPREAD, true));
                frag.addStrategy(new ContactUnitSound(state, frag, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                        .setSynced(false));
                frag.addStrategy(new ContactUnitDie(state, frag, user.getBodyData()));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(frag.getEntityID(), frag, false, ObjectLayer.HBOX);
                }
            }

            private void createWaveBeam(float startAngle) {
                Hitbox hbox = new RangedHitbox(state, startPosition, LASER_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                        false, false, user, PROJ_SPRITE);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), LASER_DAMAGE, LASER_KB,
                        DamageSource.ENEMY_ATTACK, DamageTag.ENERGY, DamageTag.RANGED));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true).setParticleColor(HadalColor.BLUE).setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
                        .setOffset(true).setParticleColor(HadalColor.BLUE));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                        .setSynced(false));
                hbox.addStrategy(new WaveEntity(state, hbox, user.getBodyData(), laser, APOCALYPSE_LASERAMPLITUDE, APOCALYPSE_LASER_FREQUENCY, startAngle));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                }
            }
        });

        return laser;
    }
}
