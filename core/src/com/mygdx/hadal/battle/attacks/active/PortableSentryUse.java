package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.TurretFlak;
import com.mygdx.hadal.schmucks.entities.enemies.TurretVolley;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Summoned;
import com.mygdx.hadal.statuses.Temporary;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class PortableSentryUse extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(70, 70);
    private static final float LIFESPAN = 3.0f;
    private static final float PROJECTILE_SPEED = 60.0f;
    private static final float TURRET_LIFESPAN = 20.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_BLUE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        final boolean faceRight = startVelocity.x > 0;

        Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, LIFESPAN, new Vector2(0, -PROJECTILE_SPEED),
                user.getHitboxFilter(), false, false, user, PROJ_SPRITE);
        hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL));
        hbox.setGravity(3.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.CYBER2, 0.4f).setSynced(false));
        if (state.isServer()) {
            hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                private HadalEntity floor;
                @Override
                public void onHit(HadalData fixB) {
                    if (fixB != null) {
                        floor = fixB.getEntity();
                        hbox.die();
                    }
                }

                @Override
                public void die() {
                    if (MathUtils.random() >= 0.5f) {
                        new TurretFlak(state, hbox.getPixelPosition(), faceRight ? 0 : 180, hbox.getFilter()) {

                            @Override
                            public void create() {
                                super.create();
                                body.setType(BodyDef.BodyType.DynamicBody);
                                getBodyData().addStatus(new Temporary(state, TURRET_LIFESPAN, getBodyData(), getBodyData(), TURRET_LIFESPAN));

                                if (user instanceof Player player) {
                                    getBodyData().addStatus(new Summoned(state, getBodyData(), player));
                                }

                                if (floor != null) {
                                    if (floor.getBody() != null) {
                                        WeldJointDef joint = new WeldJointDef();
                                        joint.bodyA = floor.getBody();
                                        joint.bodyB = getBody();
                                        joint.localAnchorA.set(new Vector2(getPosition()).sub(floor.getPosition()));
                                        joint.localAnchorB.set(0, 0);
                                        state.getWorld().createJoint(joint);
                                    }
                                }
                            }
                        };
                    } else {
                        new TurretVolley(state, hbox.getPixelPosition(), faceRight ? 0 : 180, hbox.getFilter()) {

                            @Override
                            public void create() {
                                super.create();
                                body.setType(BodyDef.BodyType.DynamicBody);
                                getBodyData().addStatus(new Temporary(state, TURRET_LIFESPAN, getBodyData(), getBodyData(), TURRET_LIFESPAN));

                                if (user instanceof Player player) {
                                    getBodyData().addStatus(new Summoned(state, getBodyData(), player));
                                }

                                if (floor != null) {
                                    if (floor.getBody() != null) {
                                        WeldJointDef joint = new WeldJointDef();
                                        joint.bodyA = floor.getBody();
                                        joint.bodyB = getBody();
                                        joint.localAnchorA.set(new Vector2(getPosition()).sub(floor.getPosition()));
                                        joint.localAnchorB.set(0, 0);
                                        state.getWorld().createJoint(joint);
                                    }
                                }
                            }
                        };
                    }
                }
            });
        } else {
            hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        }
        hbox.setFriction(1.0f);

        return hbox;
    }
}