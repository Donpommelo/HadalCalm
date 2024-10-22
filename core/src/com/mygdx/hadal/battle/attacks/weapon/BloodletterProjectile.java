package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.constants.Constants.PPM;

public class BloodletterProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(500, 400);
    public static final float LIFESPAN = 0.5f;
    public static final float BASE_DAMAGE = 17.5f;
    public static final float HEAL_MULTIPLIER = 0.25f;
    public static final float HEAL_MULTIPLIER_ENEMY = 0.08f;
    private static final float KNOCKBACK = -9.0f;

    public static final Vector2 CANDY_SIZE = new Vector2(30, 30);
    public static final float CANDY_DURATION = 20.0f;
    public static final int CANDY_SPREAD = 45;
    private static final Sprite CANDY_SPRITE = Sprite.COLA;

    public final Vector2 projectileOffset = new Vector2(80, 0);

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        projectileOffset.setAngleRad(startVelocity.angleRad());

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(startVelocity),
                startVelocity.nor().scl(PROJECTILE_SIZE.x / 2 / PPM)));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIFE_STEAL)
                .setParticleVelocity(startVelocity.angleRad())
                .setParticleSize(70)
                .setOffset(projectileOffset.x, projectileOffset.y));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 targetLocation = new Vector2();
            private final Vector2 entityLocation = new Vector2();
            boolean wallDetected;
            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    if (fixB.getEntity() instanceof Schmuck schmuck) {

                        wallDetected = false;
                        entityLocation.set(user.getPosition());
                        targetLocation.set(fixB.getEntity().getPosition());
                        if (WorldUtil.preRaycastCheck(entityLocation, targetLocation)) {
                            state.getWorld().rayCast((fixture, point, normal, fraction) -> {
                                if (fixture.getFilterData().categoryBits == BodyConstants.BIT_WALL) {
                                    wallDetected = true;
                                }
                                return -1.0f;
                            }, entityLocation, targetLocation);
                        }

                        if (!wallDetected) {
                            float baseHealMultiplier;
                            if (fixB.getEntity() instanceof Enemy) {
                                baseHealMultiplier = HEAL_MULTIPLIER_ENEMY;
                            } else {
                                baseHealMultiplier = HEAL_MULTIPLIER;
                            }
                            if (schmuck.isAlive()) {
                                float modifiedDamage = fixB.receiveDamage(BASE_DAMAGE * hbox.getDamageMultiplier(),
                                        startVelocity.nor().scl(KNOCKBACK), creator, true, hbox, DamageSource.PEARL_REVOLVER);

                                createBlood(schmuck, baseHealMultiplier * modifiedDamage);

                                SoundEffect.SLURP.playSourced(state, startPosition, 0.75f);
                                EffectEntityManager.getParticle(state, new ParticleCreate(Particle.VAMPIRE, schmuck)
                                        .setLifespan(2.0f));
                            }
                        }

                    } else {
                        fixB.receiveDamage(BASE_DAMAGE * hbox.getDamageMultiplier(), startVelocity.nor().scl(KNOCKBACK),
                                creator, true, hbox, DamageSource.PEARL_REVOLVER);
                    }
                }
            }

            private void createBlood(Schmuck target, float heal) {
                Hitbox hbox = new RangedHitbox(state, target.getPixelPosition(), CANDY_SIZE, CANDY_DURATION, new Vector2(),
                        (short) 0, false, false, user, CANDY_SPRITE);
                hbox.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR | BodyConstants.BIT_PICKUP_RADIUS));
                hbox.setLayer(ObjectLayer.STANDARD);
                hbox.setGravity(1.0f);
                hbox.setFriction(1.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARKLE));
                hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKLE).setIgnoreOnTimeout(true));
                hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.COIN3, 1.1f)
                        .setIgnoreOnTimeout(true).setSynced(false));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), CANDY_SPREAD));

                PickupVacuum pickup = new PickupVacuum(state, hbox, user.getBodyData());
                pickup.startVacuum(user);
                hbox.addStrategy(pickup);

                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    @Override
                    public void onPickup(HadalData picker) {
                        ((BodyData) picker).regainHp(heal, creator, true);

                        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.KAMABOKO_IMPACT, hbox.getPixelPosition())
                                .setLifespan(1.0f)
                                .setColor(HadalColor.RED));

                        hbox.die();
                    }
                });
                if (!state.isServer()) {
                    ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                }
            }
        });

        user.getBodyData().statusProcTime(new ProcTime.Airblast(startVelocity));

        return hbox;
    }
}