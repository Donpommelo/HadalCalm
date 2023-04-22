package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

public class OurGetAlongShirtActivate extends SyncedAttacker {

    public static final int LINK_NUMBER = 6;
    private static final Vector2 chainSize = new Vector2(20, 20);
    private static final Sprite chainSprite = Sprite.ORB_BLUE;

    private static final float chainLength = 1.2f;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[LINK_NUMBER];

        User inflicter = null;
        if (extraFields.length > 0) {
            if (state.isServer()) {
                inflicter = HadalGame.server.getUsers().get((int) extraFields[0]);
            } else {
                inflicter = HadalGame.client.getUsers().get((int) extraFields[0]);
            }
        }

        if (null != inflicter) {
            final Player partner = inflicter.getPlayer();
            if (null != partner) {
                for (int i = 0; i < hboxes.length; i++) {
                    final int currentI = i;
                    hboxes[i] = new Hitbox(state, user.getPixelPosition(), chainSize, 0, new Vector2(),
                            user.getHitboxFilter(), true, false, user, chainSprite);

                    hboxes[i].setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

                    hboxes[i].setDensity(1.0f);
                    hboxes[i].makeUnreflectable();

                    hboxes[i].addStrategy(new HitboxStrategy(state, hboxes[i], user.getBodyData()) {

                        private boolean linked;
                        @Override
                        public void controller(float delta) {

                            if (!partner.isAlive() || !user.isAlive()) {
                                hbox.setLifeSpan(2.0f);
                                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                            }

                            if (!linked) {
                                if (currentI == 0) {
                                    if (user.getBody() != null && hbox.getBody() != null) {
                                        linked = true;
                                        RevoluteJointDef joint1 = new RevoluteJointDef();
                                        joint1.bodyA = user.getBody();
                                        joint1.bodyB = hbox.getBody();
                                        joint1.collideConnected = false;

                                        joint1.localAnchorA.set(0, 0);
                                        joint1.localAnchorB.set(chainLength, 0);

                                        state.getWorld().createJoint(joint1);
                                    }
                                } else {
                                    if (hboxes[currentI - 1].getBody() != null && hbox.getBody() != null) {
                                        linked = true;

                                        RevoluteJointDef joint1 = new RevoluteJointDef();
                                        joint1.bodyA = hboxes[currentI - 1].getBody();
                                        joint1.bodyB = hbox.getBody();
                                        joint1.collideConnected = false;
                                        joint1.localAnchorA.set(-chainLength, 0);
                                        joint1.localAnchorB.set(chainLength, 0);

                                        state.getWorld().createJoint(joint1);
                                    }
                                }

                                if (currentI == hboxes.length - 1) {
                                    if (partner.getBody() != null && hbox.getBody() != null) {
                                        linked = true;

                                        RevoluteJointDef joint1 = new RevoluteJointDef();
                                        joint1.bodyA = hbox.getBody();
                                        joint1.bodyB = partner.getBody();
                                        joint1.collideConnected = false;
                                        joint1.localAnchorA.set(-chainLength, 0);
                                        joint1.localAnchorB.set(0, 0);

                                        state.getWorld().createJoint(joint1);
                                    }
                                }
                            }
                        }

                        @Override
                        public void die() {
                            if (hbox.getState().isServer()) {
                                hbox.queueDeletion();
                            } else {
                                hbox.setAlive(false);
                                ((ClientState) state).removeEntity(hbox.getEntityID());
                            }
                        }
                    });
                }
            }
        }

        return hboxes;
    }
}