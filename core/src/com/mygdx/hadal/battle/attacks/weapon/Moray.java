package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Moray extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(30, 30);
    public static final float LIFESPAN = 2.0f;
    public static final float BASE_DAMAGE = 13.0f;
    public static final float MOVE_INTERVAL = 0.023f;
    private static final float RECOIL = 12.0f;
    private static final float KNOCKBACK = 5.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_PINK;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (startPosition.length != 0) {
            SoundEffect.LASERSHOT.playSourced(state, startPosition[0], 0.9f);
            user.recoil(weaponVelocity, RECOIL);

            final int numX = extraFields.length >= 2 ? (int) extraFields[0] : 0;
            final int numY = extraFields.length >= 2 ? (int) extraFields[1] : 0;

            //create a set number of hboxes that die when hitting enemies or walls.
            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, new Vector2(), user.getHitboxFilter(),
                        true, true, user, PROJ_SPRITE);
                hbox.setSyncDefault(false);

                final int num = i;

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MORAYGUN,
                        DamageTag.ENERGY, DamageTag.RANGED).setStaticKnockback(true));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true));
                hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ORB_SWIRL));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    private float controllerCount;
                    private float numMoves;
                    @Override
                    public void controller(float delta) {
                        controllerCount += delta;

                        //Each hbox moves at set intervals. Each movement moves the hbox vertical x times followed by horizontal y times to make a snake-like movement
                        while (controllerCount >= MOVE_INTERVAL) {
                            controllerCount -= MOVE_INTERVAL;

                            if (numMoves >= num) {
                                if ((numMoves - num) % (Math.abs(numX) + Math.abs(numY)) < Math.abs(numX)) {
                                    hbox.setTransform(hbox.getPosition().add(PROJECTILE_SIZE.x / PPM * Math.signum(numX), 0), 0);
                                } else {
                                    hbox.setTransform(hbox.getPosition().add(0, PROJECTILE_SIZE.y / PPM * Math.signum(numY)), 0);
                                }
                            }
                            numMoves++;
                        }
                    }
                });
                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}