package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class SpiritBombProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    public static final float LIFESPAN = 9.0f;
    public static final float BASE_DAMAGE = 20.0f;
    public static final float EXPLOSION_DAMAGE = 55.0f;
    private static final float RECOIL = 6.0f;

    private static final float PROJ_DAMPEN = 0.75f;

    private static final float FADE_DELAY = 0.2f;
    private static final float FADE_DURATION = 0.75f;

    private static final int EXPLOSION_RADIUS = 240;
    private static final float EXPLOSION_KNOCKBACK = 20.0f;
    private static final float TARGET_CHECK_CD = 0.2f;
    private static final float TARGET_CHECK_RADIUS = 3.2f;
    private static final float WARNING_TIME = 0.5f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_BLUE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.MAGIC25_SPELL.playSourced(state, startPosition, 0.75f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                super.create();
                hbox.getBody().setLinearDamping(PROJ_DAMPEN);
            }

            private final Vector2 ghostLocation = new Vector2();
            private float controllerCount, targetCheckCount;
            private boolean fading;
            private boolean faded;
            @Override
            public void controller(float delta) {
                if (controllerCount < FADE_DELAY && !fading) {
                    controllerCount += delta;
                    if (controllerCount >= FADE_DELAY) {
                        fading = true;
                    }
                } else if (controllerCount < FADE_DURATION && !faded) {
                    controllerCount += delta;
                    if (controllerCount >= FADE_DURATION) {
                        if (state.getPlayer().getHitboxFilter() == hbox.getFilter()) {
                            hbox.setSprite(Sprite.ORB_PINK);
                        } else {
                            hbox.setSprite(Sprite.NOTHING);
                        }
                        hbox.setFlashCount(0);
                        fading = false;
                        faded = true;
                    }
                }

                if (fading) {
                    hbox.setFlashCount(hbox.getFlashCount() - delta);
                    if (hbox.getFlashCount() < -Constants.FLASH) {
                        hbox.setFlashCount(Constants.FLASH);
                    }
                }

                if (TARGET_CHECK_CD > targetCheckCount) {
                    targetCheckCount += delta;
                }
                if (targetCheckCount >= TARGET_CHECK_CD) {
                    targetCheckCount -= TARGET_CHECK_CD;
                    ghostLocation.set(hbox.getPosition());
                    state.getWorld().QueryAABB(fixture -> {
                                if (fixture.getUserData() instanceof BodyData bodyData) {
                                    if (bodyData.getSchmuck().getHitboxFilter() != user.getHitboxFilter())
                                    hbox.die();
                                }
                                return true;
                            },
                            ghostLocation.x - TARGET_CHECK_RADIUS, ghostLocation.y - TARGET_CHECK_RADIUS,
                            ghostLocation.x + TARGET_CHECK_RADIUS, ghostLocation.y + TARGET_CHECK_RADIUS);
                }
            }

            @Override
            public void die() {
                SoundEffect.PING.playSourced(state, hbox.getPixelPosition(), 0.6f, 1.5f);
                Hitbox explosion = new RangedHitbox(state, hbox.getPixelPosition(), PROJECTILE_SIZE, WARNING_TIME, new Vector2(),
                        (short) 0, true, false, user, Sprite.ORB_BLUE);
                explosion.makeUnreflectable();
                explosion.setSyncDefault(false);

                explosion.addStrategy(new ControllerDefault(state, explosion, user.getBodyData()));
                explosion.addStrategy(new Static(state, explosion, user.getBodyData()));
                explosion.addStrategy(new FlashShaderNearDeath(state, explosion, user.getBodyData(), WARNING_TIME));
                explosion.addStrategy(new DieExplode(state, explosion, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE,
                        EXPLOSION_KNOCKBACK, (short) 0, false, DamageSource.MISC));
                explosion.addStrategy(new DieSound(state, explosion, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setSynced(false));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(explosion.getEntityID(), explosion, false, ClientState.ObjectLayer.HBOX);
                }
            }
        });

        return hbox;
    }
}