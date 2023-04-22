package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class VineSeed extends SyncedAttacker {

    public static final Vector2 SEED_SIZE = new Vector2(45, 30);
    public static final float LIFESPAN = 5.0f;
    public static final float BASE_DAMAGE = 28.0f;
    private static final float KNOCKBACK = 10.0f;

    private static final float VINE_SPEED = 27.0f;
    private static final int MIN_VINE_NUM = 4;
    private static final int MAX_VINE_NUM = 7;

    private static final int VINE_BEND_SPREAD_MIN = 15;
    private static final int VINE_BEND_SPREAD_MAX = 30;

    private static final Sprite PROJ_SPRITE = Sprite.SEED;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.WOOSH.playSourced(state, startPosition, 1.0f, 0.75f);

        float chargeAmount = 0.0f;
        if (extraFields.length > 0) {
            chargeAmount = extraFields[0];
        }

        final int finalVineNum = (int) (chargeAmount * (MAX_VINE_NUM - MIN_VINE_NUM) + MIN_VINE_NUM);

        RangedHitbox hbox = new RangedHitbox(state, startPosition, SEED_SIZE, LIFESPAN, new Vector2(startVelocity), user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.VINE_SOWER,
                DamageTag.RANGED));

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void die() {
                if (state.isServer()) {
                    Vector2 finalVelo = new Vector2(hbox.getLinearVelocity()).nor().scl(VINE_SPEED);
                    float[] extraFields = new float[8 + finalVineNum * 3];
                    extraFields[0] = finalVineNum;
                    extraFields[1] = 1;
                    extraFields[2] = 0;
                    for (int i = 2; i < 7 + finalVineNum * 3; i++) {
                        extraFields[i] = MathUtils.random(VINE_BEND_SPREAD_MIN, VINE_BEND_SPREAD_MAX);
                    }

                    SyncedAttack.VINE.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(), finalVelo, extraFields);
                }
            }

            @Override
            public void onHit(HadalData fixB) {
                if (fixB != null) {
                    if (fixB.getEntity().getMainFixture().getFilterData().categoryBits == Constants.BIT_DROPTHROUGHWALL) {
                        hbox.die();
                    }
                }
            }
        });

        return hbox;
    }
}