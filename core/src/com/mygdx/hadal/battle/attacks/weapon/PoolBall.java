package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FlashNearDeath;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.constants.Constants.PPM;

public class PoolBall extends SyncedAttacker {

    public static final float PROJECTILE_SPEED = 10.0f;
    public static final float PROJECTILE_MAX_SPEED = 60.0f;

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    public static final float LIFESPAN = 6.0f;
    public static final float BASE_DAMAGE = 20.0f;
    private static final float RECOIL = 5.0f;
    private static final float KNOCKBACK = 15.0f;
    private static final float FLASH_LIFESPAN = 0.5f;
    private static final float PROJ_DAMPEN = 1.5f;

    private static final float MIN_SPEED_THRESHOLD = 1000.0f;
    private static final float MAX_SPEED_THRESHOLD = 3000.0f;

    public static final float MIN_DAMAGE_MULTIPLIER = 1.0f;
    public static final float MAX_DAMAGE_MULTIPLIER = 3.0f;

    private static final Sprite PROJ_SPRITE = Sprite.CANNONBALL;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.BILLIARD_HIT.playSourced(state, startPosition, 0.8f);
        user.recoil(startVelocity, RECOIL);

        float chargeAmount = 0.0f;
        if (extraFields.length > 0) {
            chargeAmount = extraFields[0];
        }
        float velocity = chargeAmount * (PROJECTILE_MAX_SPEED - PROJECTILE_SPEED) + PROJECTILE_SPEED;

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(startVelocity).nor().scl(velocity),
                user.getHitboxFilter(),true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MIDNIGHT_POOL_CUE,
                DamageTag.WHACKING, DamageTag.RANGED) {

            @Override
            public void inflictDamage(HadalData fixB) {
                if (fixB instanceof BodyData) {
                    float speed = hbox.getLinearVelocity().len2();
                    speed = Math.max(Math.min(speed, MAX_SPEED_THRESHOLD), MIN_SPEED_THRESHOLD);
                    float multiplier = MIN_DAMAGE_MULTIPLIER + (MAX_DAMAGE_MULTIPLIER - MIN_DAMAGE_MULTIPLIER)
                            * (speed - MIN_SPEED_THRESHOLD) / (MAX_SPEED_THRESHOLD - MIN_SPEED_THRESHOLD);
                    hbox.setDamageMultiplier(multiplier);
                }
                super.inflictDamage(fixB);
            }

        }.setRepeatable(true));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.4f).setSynced(false));
        hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                if (hbox.getBody() != null) {
                    hbox.getBody().setLinearDamping(PROJ_DAMPEN);
                    FixtureBuilder.createFixtureDefCircle(hbox.getBody(), new Vector2(), PROJECTILE_SIZE.x / PPM / 2.0f,
                            false, 1.0f, 1.0f, 1.0f,
                            Constants.BIT_PROJECTILE, (short) (Constants.BIT_WALL | Constants.BIT_PROJECTILE), (short) 0)
                            .setUserData(hbox.getHadalData());
                }
            }

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    if (UserDataType.HITBOX.equals(fixB.getType()) || UserDataType.WALL.equals(fixB.getType())) {
                        SoundEffect.BILLIARD_HIT.playSourced(state, startPosition, 0.3f);
                    }
                }
            }
        });
        return hbox;
    }
}