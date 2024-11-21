package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.CreateSound;

import static com.mygdx.hadal.constants.Constants.PPM;

public class MaelstromProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(20, 20);
    public static final float LIFESPAN = 2.0f;
    public static final float BASE_DAMAGE = 12.0f;
    private static final float RECOIL = 6.0f;
    private static final float KNOCKBACK = -8.0f;

    public static final float EXPLOSION_INTERVAL = 0.06f;
    private static final int EXPLOSION_MAX_SIZE = 250;
    private static final float EXPLOSION_GROWTH = 8.0f;

    private static final Sprite projSprite = Sprite.HURRICANE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.WIND2)
                .setVolume(0.8f)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        final Vector2 explosionSize = new Vector2(PROJECTILE_SIZE);
        Hitbox storm = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, projSprite) {

            @Override
            public void render(SpriteBatch batch, Vector2 entityLocation) {
                if (!alive) { return; }

                if (projectileSprite != null) {
                    batch.draw(projectileSprite.getKeyFrame(animationTime, false),
                            entityLocation.x - explosionSize.x / 2 * getScale(),
                            entityLocation.y - explosionSize.y / 2 * getScale(),
                            explosionSize.x / 2 * getScale(), explosionSize.y / 2 * getScale(),
                            explosionSize.x * getScale(), explosionSize.y * getScale(), -1, 1,
                            MathUtils.radDeg * getAngle());
                }
            }
        };
        storm.setEffectsHit(false);
        storm.setEffectsVisual(true);
        storm.setRestitution(0.5f);

        storm.addStrategy(new ControllerDefault(state, storm, user.getBodyData()));
        storm.addStrategy(new CreateParticles(state, storm, user.getBodyData(), Particle.STORM));
        storm.addStrategy(new CreateSound(state, storm, user.getBodyData(), SoundEffect.WIND3, 0.6f, true));
        storm.addStrategy(new HitboxStrategy(state, storm, user.getBodyData()) {

            private float controllerCount;
            private final Vector2 hboxPosition = new Vector2();
            private final Vector2 kb = new Vector2();
            @Override
            public void create() {

                //Set hurricane to have constant angular velocity for visual effect.
                hbox.setAngularVelocity(5);
            }

            @Override
            public void controller(float delta) {

                controllerCount += delta;

                //This hbox periodically spawns hboxes on top of itself.
                while (controllerCount >= EXPLOSION_INTERVAL) {
                    controllerCount -= EXPLOSION_INTERVAL;

                    hboxPosition.set(hbox.getPosition());

                    state.getWorld().QueryAABB(fixture -> {
                                if (fixture.getUserData() instanceof BodyData bodyData) {
                                    if (bodyData.getSchmuck().getHitboxFilter() != creator.getSchmuck().getHitboxFilter()) {
                                        kb.set(bodyData.getEntity().getPosition()).sub(hboxPosition).nor().scl(KNOCKBACK);
                                        bodyData.receiveDamage(BASE_DAMAGE, kb, creator, true, storm,
                                                DamageSource.MAELSTROM, DamageTag.EXPLOSIVE, DamageTag.RANGED);
                                    }
                                }
                                return true;
                            },
                            hboxPosition.x - explosionSize.x / 2 / PPM, hboxPosition.y - explosionSize.y / 2 / PPM,
                            hboxPosition.x + explosionSize.x / 2 / PPM, hboxPosition.y + explosionSize.y / 2 / PPM);

                    //spawned hboxes get larger as hbox moves
                    if (explosionSize.x <= EXPLOSION_MAX_SIZE) {
                        explosionSize.add(EXPLOSION_GROWTH, EXPLOSION_GROWTH);
                    }
                }
            }
        });
        return storm;
    }
}