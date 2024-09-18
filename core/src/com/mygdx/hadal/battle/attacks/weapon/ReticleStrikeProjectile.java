package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class ReticleStrikeProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(10, 10);
    public static final float LIFESPAN = 1.2f;
    private static final float RECOIL = 16.0f;

    public static final float RETICLE_LIFESPAN = 0.6f;
    private static final float RETICLE_SIZE = 80.0f;
    private static final float RETICLE_SPACING = 110.0f;
    private static final float RETICLE_SIZE_SQUARED = 12100;

    public static final float EXPLOSION_DAMAGE = 35.0f;
    private static final int EXPLOSION_RADIUS = 100;
    private static final float EXPLOSION_KNOCKBACK = 20.0f;

    private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LOCKANDLOAD.playSourced(state, startPosition, 0.8f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 lastPosition = new Vector2(startPosition);
            @Override
            public void controller(float delta) {
                if (lastPosition.dst2(hbox.getPixelPosition()) > RETICLE_SIZE_SQUARED) {
                    lastPosition.add(new Vector2(hbox.getPixelPosition()).sub(lastPosition).nor().scl(RETICLE_SPACING));

                    Hitbox reticle = new RangedHitbox(state, lastPosition, new Vector2(RETICLE_SIZE, RETICLE_SIZE), RETICLE_LIFESPAN,
                            new Vector2(), user.getHitboxFilter(), true, true, user, Sprite.CROSSHAIR);
                    reticle.setEffectsMovement(false);
                    reticle.setEffectsHit(false);
                    reticle.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

                    reticle.addStrategy(new ControllerDefault(state, reticle, user.getBodyData()));
                    reticle.addStrategy(new CreateParticles(state, reticle, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f)
                            .setParticleSize(40.0f).setParticleColor(HadalColor.HOT_PINK).setSyncType(SyncType.NOSYNC));
                    reticle.addStrategy(new DieExplode(state, reticle, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE,
                            EXPLOSION_KNOCKBACK, user.getHitboxFilter(), false, DamageSource.RETICLE_STRIKE));
                    reticle.addStrategy(new DieSound(state, reticle, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f)
                            .setSynced(false));
                    reticle.addStrategy(new Static(state, reticle, user.getBodyData()));

                    if (!state.isServer()) {
                        ((PlayStateClient) state).addEntity(reticle.getEntityID(), reticle, false, PlayStateClient.ObjectLayer.HBOX);
                    }
                }
            }
        });

        return hbox;
    }
}