package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.melee.MorningStar;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class MorningStarProjectile extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(75, 75);
    private static final Vector2 CHAIN_SIZE = new Vector2(20, 20);

    public static final float BASE_DAMAGE = 50.0f;
    private static final float KNOCKBACK = 60.0f;
    private static final float FLASH_LIFESPAN = 1.0f;

    public static final int CHAIN_NUM = 4;
    public static final float CHAIN_LENGTH = 1.2f;
    private static final float HOME_POWER = 120.0f;

    private static final Sprite CHAIN_SPRITE = Sprite.ORB_ORANGE;
    private static final Sprite PROJ_SPRITE = Sprite.FLAIL;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] links = new Hitbox[CHAIN_NUM];
        Hitbox[] hboxes = new Hitbox[2];

        //the base is connected to the player and links to the rest of the flail weapon
        Hitbox base = new Hitbox(state, user.getPixelPosition(), CHAIN_SIZE, 0, new Vector2(), user.getHitboxFilter(),
                true, false, user, CHAIN_SPRITE);
        base.setDensity(1.0f);
        base.makeUnreflectable();
        base.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));
        base.setSynced(true);
        base.setSyncedDelete(true);

        base.addStrategy(new HitboxStrategy(state, base, user.getBodyData()) {

            private boolean linked;
            @Override
            public void controller(float delta) {

                if (!linked) {
                    if (user.getBody() != null && base.getBody() != null) {
                        linked = true;
                        RevoluteJointDef joint1 = new RevoluteJointDef();
                        joint1.bodyA = user.getBody();
                        joint1.bodyB = base.getBody();
                        joint1.collideConnected = false;

                        joint1.localAnchorA.set(0, 0);
                        joint1.localAnchorB.set(CHAIN_LENGTH, 0);

                        state.getWorld().createJoint(joint1);
                    }
                }
            }

            @Override
            public void die() {
                for (int i = 0; i < CHAIN_NUM; i++) {
                    if (links[i] != null) {
                        links[i].setLifeSpan(2.0f);
                        links[i].addStrategy(new ControllerDefault(state, links[i], user.getBodyData()));
                        links[i].addStrategy(new FlashNearDeath(state, links[i], user.getBodyData(), FLASH_LIFESPAN));
                    }
                }
                hboxes[1].setLifeSpan(2.0f);
                hboxes[1].addStrategy(new ControllerDefault(state, hboxes[1], user.getBodyData()));
                hboxes[1].addStrategy(new FlashNearDeath(state, hboxes[1], user.getBodyData(), FLASH_LIFESPAN));
                hbox.queueDeletion();
            }
        });

        //create several linked hboxes
        for (int i = 0; i < CHAIN_NUM; i++) {
            final int currentI = i;
            links[i] = new Hitbox(state, user.getPixelPosition(), CHAIN_SIZE, 0, new Vector2(),user.getHitboxFilter(),
                    true, false, user, CHAIN_SPRITE);
            links[i].setDensity(1.0f);
            links[i].makeUnreflectable();
            links[i].setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

            links[i].addStrategy(new HitboxStrategy(state, links[i], user.getBodyData()) {

                private boolean linked;
                @Override
                public void controller(float delta) {
                    if (!linked) {
                        if (currentI == 0) {
                            if (base.getBody() != null && hbox.getBody() != null) {
                                linked = true;
                                RevoluteJointDef joint1 = new RevoluteJointDef();
                                joint1.bodyA = base.getBody();
                                joint1.bodyB = hbox.getBody();
                                joint1.collideConnected = false;

                                joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
                                joint1.localAnchorB.set(CHAIN_LENGTH, 0);

                                state.getWorld().createJoint(joint1);
                            }
                        } else {
                            if (links[currentI - 1].getBody() != null && hbox.getBody() != null) {
                                linked = true;

                                RevoluteJointDef joint1 = new RevoluteJointDef();
                                joint1.bodyA = links[currentI - 1].getBody();
                                joint1.bodyB = hbox.getBody();
                                joint1.collideConnected = false;
                                joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
                                joint1.localAnchorB.set(CHAIN_LENGTH, 0);

                                state.getWorld().createJoint(joint1);
                            }
                        }
                    }
                }

                @Override
                public void die() {
                    hbox.queueDeletion();
                }
            });

            if (!state.isServer()) {
                ((ClientState) state).addEntity(links[i].getEntityID(), links[i], false, ClientState.ObjectLayer.HBOX);
            }
        }

        //the star hbox damages people and has weight
        Hitbox star = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, 0, new Vector2(),
                user.getHitboxFilter(), false, true, user, PROJ_SPRITE);

        star.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));
        star.setGravity(1.0f);
        star.setDensity(0.1f);
        star.makeUnreflectable();
        star.setSynced(true);
        star.setSyncedDelete(true);

        star.addStrategy(new DamageStandard(state, star, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MORNING_STAR,
                DamageTag.WHACKING, DamageTag.MELEE).setRepeatable(true));
        star.addStrategy(new ContactWallSound(state, star, user.getBodyData(), SoundEffect.WALL_HIT1, 0.25f));
        star.addStrategy(new ContactUnitSound(state, star, user.getBodyData(), SoundEffect.SLAP, 0.25f, true)
                .setPitch(0.5f).setSynced(false));
        star.addStrategy(new HomingMouse(state, star, user.getBodyData(), HOME_POWER));

        star.addStrategy(new HitboxStrategy(state, star, user.getBodyData()) {

            private boolean linked;
            @Override
            public void controller(float delta) {

                if (!linked) {
                    if (links[CHAIN_NUM - 1].getBody() != null && hbox.getBody() != null) {
                        linked = true;

                        RevoluteJointDef joint1 = new RevoluteJointDef();
                        joint1.bodyA = links[CHAIN_NUM - 1].getBody();
                        joint1.bodyB = hbox.getBody();
                        joint1.collideConnected = false;
                        joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
                        joint1.localAnchorB.set(CHAIN_LENGTH, 0);

                        state.getWorld().createJoint(joint1);
                    }
                }
            }

            @Override
            public void die() {
                hbox.queueDeletion();
            }
        });
        hboxes[0] = base;
        hboxes[1] = star;
        return hboxes;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        if (user instanceof Player player && state.isServer()) {
            MorningStar.createMorningStar(state, player, startPosition);
        }
    }
}