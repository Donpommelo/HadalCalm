package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class SeraphBomb extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(120, 120);
    private static final Vector2 WAVE_SIZE = new Vector2(20, 20);
    private static final float LIFESPAN = 6.0f;
    private static final float SPEED = 50.0f;
    private static final float BASE_DAMAGE = 22.0f;
    private static final float KNOCKBACK = 15.0f;

    private static final float GRID_DISTANCE = 224;
    private static final float WAVE_LIFESPAN = 10.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NAVAL_MINE;
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox bomb = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN, new Vector2(), user.getHitboxFilter(),
                true, false, user, PROJ_SPRITE);
        bomb.makeUnreflectable();

        bomb.addStrategy(new ControllerDefault(state, bomb, user.getBodyData()));
        bomb.addStrategy(new CreateParticles(state, bomb, user.getBodyData(), Particle.RING, 0.0f, LINGER)
                .setSyncType(SyncType.NOSYNC));
        bomb.addStrategy(new FlashShaderNearDeath(state, bomb, user.getBodyData(), 1.0f));
        bomb.addStrategy(new HitboxStrategy(state, bomb, user.getBodyData()) {

            @Override
            public void die() {
                SoundEffect.EXPLOSION9.playSourced(state, hbox.getPixelPosition(), 0.5f, 0.5f);

                WeaponUtils.createExplosion(state, hbox.getPixelPosition(), GRID_DISTANCE, creator.getSchmuck(),
                        BASE_DAMAGE, KNOCKBACK, creator.getSchmuck().getHitboxFilter(), false, DamageSource.ENEMY_ATTACK);
                explode(0);
                explode(90);
                explode(180);
                explode(270);
            }

            private void explode(float startAngle) {
                Hitbox wave = new RangedHitbox(state, new Vector2(bomb.getPixelPosition()).add(new Vector2(0, GRID_DISTANCE).setAngleDeg(startAngle)),
                        WAVE_SIZE, WAVE_LIFESPAN, new Vector2(0, SPEED).setAngleDeg(startAngle),
                        user.getHitboxFilter(),true, false, creator.getSchmuck(), Sprite.NOTHING);
                wave.makeUnreflectable();

                wave.addStrategy(new ControllerDefault(state, wave, user.getBodyData()));
                wave.addStrategy(new ContactWallDie(state, wave, user.getBodyData()));
                wave.addStrategy(new TravelDistanceDie(state, wave, user.getBodyData(), 3 * GRID_DISTANCE / PPM / 2));
                wave.addStrategy(new HitboxStrategy(state, wave, user.getBodyData()) {

                    private final Vector2 lastPosition = new Vector2();
                    private final Vector2 entityLocation = new Vector2();

                    @Override
                    public void controller(float delta) {
                        entityLocation.set(hbox.getPixelPosition());
                        if (lastPosition.dst2(entityLocation) > GRID_DISTANCE * GRID_DISTANCE) {
                            lastPosition.set(entityLocation);
                            WeaponUtils.createExplosion(state, hbox.getPixelPosition(), GRID_DISTANCE,
                                    creator.getSchmuck(), BASE_DAMAGE, KNOCKBACK, creator.getSchmuck().getHitboxFilter(),
                                    false, DamageSource.ENEMY_ATTACK);
                        }
                    }
                });

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(wave.getEntityID(), wave, false, ClientState.ObjectLayer.HBOX);
                }
            }
        });
        return bomb;
    }
}
