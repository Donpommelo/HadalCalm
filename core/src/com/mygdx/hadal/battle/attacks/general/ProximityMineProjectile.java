package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class ProximityMineProjectile extends SyncedAttacker {

    public static final float PRIME_TIME = 1.0f;
    private static final Vector2 MINE_SIZE = new Vector2(75, 30);
    private static final float WARNING_TIME = 0.5f;
    private static final float MINE_SPEED = 60.0f;
    private static final float MINE_LIFESPAN = 18.0f;
    private static final int MINE_EXPLOSION_RADIUS = 270;
    private static final float MINE_DEFAULT_DAMAGE = 100.0f;
    private static final float MINE_EXPLOSION_KNOCKBACK = 50.0f;
    private static final float MINE_TARGET_CHECK_CD = 0.2f;
    private static final float MINE_TARGET_CHECK_RADIUS = 3.6f;

    private final DamageSource damageSource;

    public ProximityMineProjectile(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        final float mineDamage = extraFields.length > 0 ? extraFields[0] : MINE_DEFAULT_DAMAGE;

        if (damageSource.equals(DamageSource.BOOK_OF_BURIAL)) {
            ((Player) user).getArtifactIconHelper().addArtifactFlash(UnlockArtifact.BOOK_OF_BURIAL);
        }

        final boolean[] primed = new boolean[] { false };
        Hitbox hbox = new RangedHitbox(state, startPosition, MINE_SIZE, MINE_LIFESPAN, new Vector2(0, -MINE_SPEED),
                (short) 0, false, false, user, Sprite.LAND_MINE) {

            @Override
            public void render(SpriteBatch batch, Vector2 entityLocation) {
                if (!alive) { return; }
                if (primed[0]) { return; }
                super.render(batch, entityLocation);
            }
        };
        hbox.setPassability((short) (BodyConstants.BIT_WALL | BodyConstants.BIT_DROPTHROUGHWALL | BodyConstants.BIT_PLAYER));
        hbox.makeUnreflectable();
        hbox.setGravity(3.0f);
        hbox.setSyncDefault(false);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private HadalEntity floor;
            private boolean planted, set;
            private float primeCount;
            private float targetCheckCount;
            private final Vector2 mineLocation = new Vector2();
            @Override
            public void onHit(HadalData fixB, Body body) {
                if (null != fixB) {
                    if (UserDataType.WALL.equals(fixB.getType()) || UserDataType.EVENT.equals(fixB.getType())) {
                        floor = fixB.getEntity();
                        if (null != floor) {
                            if (null != floor.getBody()) {
                                planted = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void controller(float delta) {
                if (planted && null != floor.getBody() && null != hbox.getBody()) {
                    planted = false;
                    RevoluteJointDef joint = new RevoluteJointDef();
                    joint.bodyA = floor.getBody();
                    joint.bodyB = hbox.getBody();
                    joint.localAnchorA.set(new Vector2(hbox.getPosition()).sub(floor.getPosition()));
                    joint.localAnchorB.set(0, 0);
                    state.getWorld().createJoint(joint);

                    SoundEffect.SLAP.playSourced(state, hbox.getPixelPosition(), 0.6f);
                    set = true;
                }
                if (set && !primed[0]) {
                    primeCount += delta;
                    if (PRIME_TIME <= primeCount) {
                        SoundEffect.MAGIC27_EVIL.playSourced(state, hbox.getPixelPosition(), 1.0f);
                        primed[0] = true;
                        hbox.setLifeSpan(MINE_LIFESPAN);

                        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, hbox.getPixelPosition())
                                        .setLifespan(1.0f)
                                        .setScale(0.5f));
                    }
                }
                if (primed[0]) {
                    if (MINE_TARGET_CHECK_CD > targetCheckCount) {
                        targetCheckCount += delta;
                    }
                    if (targetCheckCount >= MINE_TARGET_CHECK_CD) {
                        targetCheckCount -= MINE_TARGET_CHECK_CD;
                        mineLocation.set(hbox.getPosition());
                        state.getWorld().QueryAABB(fixture -> {
                                    if (fixture.getUserData() instanceof BodyData) {
                                        hbox.die();
                                    }
                                    return true;
                                },
                                mineLocation.x - MINE_TARGET_CHECK_RADIUS, mineLocation.y - MINE_TARGET_CHECK_RADIUS,
                                mineLocation.x + MINE_TARGET_CHECK_RADIUS, mineLocation.y + MINE_TARGET_CHECK_RADIUS);
                    }
                }
            }

            @Override
            public void die() {
                SoundEffect.PING.playSourced(state, hbox.getPixelPosition(), 0.6f, 1.5f);
                Hitbox explosion = new RangedHitbox(state, hbox.getPixelPosition(), MINE_SIZE, WARNING_TIME,  new Vector2(),
                        (short) 0, true, false, user, Sprite.LAND_MINE);
                explosion.makeUnreflectable();
                explosion.setSyncDefault(false);

                explosion.addStrategy(new ControllerDefault(state, explosion, user.getBodyData()));
                explosion.addStrategy(new Static(state, explosion, user.getBodyData()));
                explosion.addStrategy(new FlashShaderNearDeath(state, explosion, user.getBodyData(), WARNING_TIME));
                explosion.addStrategy(new DieExplode(state, explosion, user.getBodyData(), MINE_EXPLOSION_RADIUS, mineDamage,
                        MINE_EXPLOSION_KNOCKBACK, (short) 0, false, damageSource));
                explosion.addStrategy(new DieSound(state, explosion, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setSynced(false));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(explosion.getEntityID(), explosion, false, ObjectLayer.HBOX);
                }
            }
        });

        return hbox;
    }
}