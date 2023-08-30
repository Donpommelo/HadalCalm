package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class TrickShot extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(71, 61);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 60.0f;
    private static final float RECOIL = 16.0f;
    private static final float KNOCKBACK = 20.0f;
    private static final float PROJECTILE_SPEED_AFTER = 60.0f;

    private static final Sprite PROJ_SPRITE = Sprite.TRICKBULLET;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.FUTURE_GUN17.playSourced(state, startPosition, 0.6f);
        user.recoil(startVelocity, RECOIL);

        boolean firstClicked = true;
        Vector2 pos1 = new Vector2();
        Vector2 pos2 = new Vector2();
        if (extraFields.length > 4) {
            firstClicked = extraFields[0] == 1.0f;
            pos1.set(extraFields[1], extraFields[2]);
            pos2.set(extraFields[3], extraFields[4]);
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.TRICK, 0.0f, 1.0f)
                .setRotate(true).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.TRICK_GUN,	DamageTag.RANGED));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.6f, true)
                .setSynced(false));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.DIATOM_IMPACT_SMALL));

        //This extra check of firstClicked makes sure effects that autofire this gun work (like muddling cup)
        if (firstClicked) {

            //when hbox reaches location of mouse click, it moves towards location of mouse release
            hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                private boolean firstReached;
                private final Vector2 startLocation = new Vector2();
                private float distance;
                private final Vector2 target = new Vector2();
                @Override
                public void create() {
                    this.startLocation.set(hbox.getPixelPosition());
                    this.distance = startLocation.dst2(pos1);
                }

                @Override
                public void controller(float delta) {
                    if (!firstReached) {
                        if (startLocation.dst2(hbox.getPixelPosition()) >= distance) {

                            if (!pos2.equals(pos1)) {
                                target.set(pos2).sub(hbox.getPixelPosition());
                            } else {
                                target.set(hbox.getLinearVelocity());
                            }

                            hbox.setLinearVelocity(target.nor().scl(PROJECTILE_SPEED_AFTER));
                            SoundEffect.FUTURE_GUN17.playSourced(state, startPosition, 0.8f);

                            firstReached = true;
                        }
                    }
                }
            });
        }

        return hbox;
    }
}