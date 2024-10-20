package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class SpiritBombProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final Vector2 SPRITE_SIZE = new Vector2(106, 120);
    public static final Vector2 SPRITE_OFFSET = new Vector2(-10, 20);
    public static final float LIFESPAN = 9.0f;
    public static final float BASE_DAMAGE = 20.0f;
    public static final float EXPLOSION_DAMAGE = 55.0f;
    private static final float RECOIL = 6.0f;

    private static final float PROJ_DAMPEN = 0.75f;

    private static final float FADE_DELAY = 1.5f;

    private static final int EXPLOSION_RADIUS = 280;
    private static final float EXPLOSION_KNOCKBACK = 20.0f;
    private static final float TARGET_CHECK_CD = 0.2f;
    private static final float TARGET_CHECK_RADIUS = 3.2f;
    private static final float WARNING_TIME = 0.6f;

    private static final float SPIRIT_HOMING = 60;
    private static final int HOME_RADIUS = 30;

    private static final Sprite PROJ_SPRITE = Sprite.SPIRIT_BOMB_IDLE;
    private static final Sprite PROJ_SPRITE_ACTIVATE = Sprite.SPIRIT_BOMB_ACTIVATION;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.MAGIC25_SPELL.playSourced(state, startPosition, 0.75f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE) {

            private float controllerCount;
            private boolean faded;
            @Override
            public void controller(float delta) {
                super.controller(delta);

                if (controllerCount < FADE_DELAY) {
                    controllerCount += delta;
                    if (controllerCount >= FADE_DELAY) {
                        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, getPixelPosition())
                                .setLifespan(1.0f)
                                .setScale(0.5f));
                        faded = true;
                    }
                }
            }

            private boolean flip;
            @Override
            public void render(SpriteBatch batch, Vector2 entityLocation) {
                if (!alive) { return; }

                if (creator instanceof Player player) {
                    if (faded && !HadalGame.usm.isOwnTeam(player.getUser())) {
                        return;
                    }
                }

                float direction = getLinearVelocity().x;
                if (direction != 0.0f) {
                    flip = getLinearVelocity().x > 0.0f;
                }

                batch.draw(projectileSprite.getKeyFrame(animationTime, true),
                        (flip ? entityLocation.x - spriteSize.x / 2 : entityLocation.x + spriteSize.x / 2) + (flip ? 1 : -1) * spriteOffset.x,
                        entityLocation.y - spriteSize.y / 2 + spriteOffset.y,
                        (flip ? 1 : -1) * spriteSize.x / 2,
                        spriteSize.y / 2,
                        (flip ? 1 : -1) * spriteSize.x, spriteSize.y, 1, 1,
                        MathUtils.radDeg * getAngle());
            }

            @Override
            public Shader getShaderStatic() {
                if (creator instanceof Player player) {
                    if (faded && HadalGame.usm.isOwnTeam(player.getUser())) {
                        return Shader.TRANSLUCENT;
                    }
                }

                return super.getShaderStatic();
            }
        };
        hbox.setSpriteSize(SPRITE_SIZE);
        hbox.setSpriteOffset(SPRITE_OFFSET);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                super.create();
                hbox.getBody().setLinearDamping(PROJ_DAMPEN);
            }

            private final Vector2 ghostLocation = new Vector2();
            private float targetCheckCount;
            private Schmuck target;
            @Override
            public void controller(float delta) {
                if (TARGET_CHECK_CD > targetCheckCount) {
                    targetCheckCount += delta;
                }
                if (targetCheckCount >= TARGET_CHECK_CD) {
                    targetCheckCount -= TARGET_CHECK_CD;
                    ghostLocation.set(hbox.getPosition());
                    state.getWorld().QueryAABB(fixture -> {
                                if (fixture.getUserData() instanceof BodyData bodyData) {
                                    if (bodyData.getSchmuck().getHitboxFilter() != user.getHitboxFilter()) {
                                        target = bodyData.getSchmuck();
                                        hbox.die();
                                    }
                                }
                                return true;
                            },
                            ghostLocation.x - TARGET_CHECK_RADIUS, ghostLocation.y - TARGET_CHECK_RADIUS,
                            ghostLocation.x + TARGET_CHECK_RADIUS, ghostLocation.y + TARGET_CHECK_RADIUS);
                }
            }

            private boolean looped;
            @Override
            public void die() {
                SoundEffect.PING.playSourced(state, hbox.getPixelPosition(), 0.6f, 1.5f);
                Hitbox explosion = getExplosion();
                explosion.setSpriteSize(SPRITE_SIZE);
                explosion.setSpriteOffset(SPRITE_OFFSET);

                explosion.addStrategy(new ControllerDefault(state, explosion, user.getBodyData()));
                explosion.addStrategy(new FlashShaderNearDeath(state, explosion, user.getBodyData(), WARNING_TIME));
                explosion.addStrategy(new DieSound(state, explosion, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setSynced(false));
                if (null != target) {
                    explosion.addStrategy(new HomingUnit(state, explosion, user.getBodyData(), SPIRIT_HOMING, HOME_RADIUS)
                            .setTarget(target).setSteering(false));
                }
                explosion.addStrategy(new HitboxStrategy(state, explosion, user.getBodyData()) {

                    @Override
                    public void die() {
                        Hitbox hbox = new Hitbox(state, explosion.getPixelPosition(), new Vector2(EXPLOSION_RADIUS, EXPLOSION_RADIUS),
                                0.4f, new Vector2(), user.getHitboxFilter(), true, false, user, Sprite.NOTHING);

                        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
                        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.STARBURST, 0.0f, 1.0f)
                                .setSyncType(SyncType.NOSYNC).setRotate(true).setParticleSize(60));
                        hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                                1.0f, DamageSource.SPIRIT_BOMB, DamageTag.EXPLOSIVE));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                        }
                    }
                });

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(explosion.getEntityID(), explosion, false, ObjectLayer.HBOX);
                }
            }

            private Hitbox getExplosion() {
                Hitbox explosion = new RangedHitbox(state, hbox.getPixelPosition(), PROJECTILE_SIZE, WARNING_TIME, new Vector2(),
                        user.getHitboxFilter(), true, false, user, PROJ_SPRITE_ACTIVATE) {

                    @Override
                    public void render(SpriteBatch batch, Vector2 entityLocation) {
                        if (!alive) { return; }

                        boolean flip = getLinearVelocity().x > 0.0f;

                        if (!looped) {
                            if (projectileSprite.isAnimationFinished(animationTime)) {
                                setSprite(Sprite.SPIRIT_BOMB_LOOP);
                                setSpriteSize(SPRITE_SIZE);
                                setSpriteOffset(SPRITE_OFFSET);
                                looped = true;
                            }
                        }

                        batch.draw(projectileSprite.getKeyFrame(animationTime, false),
                                (flip ? 0 : spriteSize.x) + entityLocation.x - spriteSize.x / 2,
                                entityLocation.y - spriteSize.y / 2,
                                spriteSize.x / 2,
                                (flip ? 1 : -1) * spriteSize.y / 2,
                                (flip ? 1 : -1) * spriteSize.x, spriteSize.y, 1, 1, 0);
                    }
                };
                explosion.makeUnreflectable();
                explosion.setSyncDefault(false);
                return explosion;
            }
        });

        return hbox;
    }
}