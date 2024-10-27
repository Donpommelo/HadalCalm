package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.*;
import com.mygdx.hadal.constants.BodyConstants;
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

public class MagicBean extends SyncedAttacker {

    public static final Vector2 SEED_SIZE = new Vector2(45, 30);
    public static final float LIFESPAN = 5.0f;
    public static final float BASE_DAMAGE = 28.0f;
    private static final float KNOCKBACK = 10.0f;

    private static final float VINE_SPEED = 27.0f;
    private static final int MIN_VINE_NUM = 4;
    private static final int MAX_VINE_NUM = 7;

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
        hbox.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_SENSOR | BodyConstants.BIT_DROPTHROUGHWALL));
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MAGIC_BEANSTALKER,
                DamageTag.RANGED));

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void die() {
                if (!state.isServer()) { return;}
                Vector2 finalVelo = new Vector2(hbox.getLinearVelocity()).nor().scl(VINE_SPEED);
                WeaponUtils.createVine(state, user, hbox.getPixelPosition(), finalVelo, finalVineNum, 1, SyncedAttack.VINE);
            }

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    if (fixB.getEntity().getMainFixture() != null) {
                        if (fixB.getEntity().getMainFixture().getFilterData().categoryBits == BodyConstants.BIT_DROPTHROUGHWALL) {
                            hbox.die();
                        }
                    }
                }
            }
        });

        return hbox;
    }
}