package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class LoveArrow extends SyncedAttacker {

    public static final float PROJECTILE_SPEED = 15.0f;
    private static final float PROJECTILE_MAX_SPEED = 65.0f;

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 21);
    public static final float LIFESPAN = 2.0f;
    public static final float MIN_DAMAGE = 34.0f;
    public static final float MAX_DAMAGE = 69.0f;
    public static final float MIN_HEAL = 15.0f;
    public static final float MAX_HEAL = 35.0f;
    private static final float KNOCKBACK = 30.0f;
    private static final float RECOIL = 5.0f;
    private static final float SELF_HIT_DELAY = 0.1f;

    private static final Sprite PROJ_SPRITE = Sprite.ARROW;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.BOW_SHOOT.playSourced(state, startPosition, 0.6f);
        user.recoil(startVelocity, RECOIL);

        float chargeAmount = 0.0f;
        if (extraFields.length > 0) {
            chargeAmount = extraFields[0];
        }

        //velocity scales to the charge percent
        float velocity = chargeAmount * (PROJECTILE_MAX_SPEED - PROJECTILE_SPEED) + PROJECTILE_SPEED;
        float damage = chargeAmount * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;
        float heal = chargeAmount * (MAX_HEAL - MIN_HEAL) + MIN_HEAL;

        Hitbox hurtbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(startVelocity).nor().scl(velocity),
                user.getHitboxFilter(), false, true, user, PROJ_SPRITE);
        hurtbox.setGravity(1.0f);

        hurtbox.addStrategy(new ControllerDefault(state, hurtbox, user.getBodyData()));
        hurtbox.addStrategy(new AdjustAngle(state, hurtbox, user.getBodyData()));
        hurtbox.addStrategy(new ContactWallDie(state, hurtbox, user.getBodyData()));
        hurtbox.addStrategy(new ContactUnitLoseDurability(state, hurtbox, user.getBodyData()));
        hurtbox.addStrategy(new DieParticles(state, hurtbox, user.getBodyData(), Particle.ARROW_BREAK).setSyncType(SyncType.NOSYNC));
        hurtbox.addStrategy(new DamageStandard(state, hurtbox, user.getBodyData(), damage, KNOCKBACK, DamageSource.LOVE_BOW,
                DamageTag.POKING, DamageTag.RANGED));
        hurtbox.addStrategy(new ContactUnitSound(state, hurtbox, user.getBodyData(), SoundEffect.SLASH, 0.4f, true).setSynced(false));
        hurtbox.addStrategy(new ContactWallSound(state, hurtbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.8f).setSynced(false));
        hurtbox.addStrategy(new CreateParticles(state, hurtbox, user.getBodyData(), Particle.BOW_TRAIL, 0.0f, 1.0f)
                .setRotate(true).setSyncType(SyncType.NOSYNC));

        Hitbox healbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(startVelocity).nor().scl(velocity),
                (short) 0, false, false, user, Sprite.NOTHING);
        healbox.setSyncDefault(false);

        healbox.addStrategy(new ControllerDefault(state, healbox, user.getBodyData()));
        healbox.addStrategy(new FixedToEntity(state, healbox, user.getBodyData(), hurtbox, new Vector2(), new Vector2()).setRotate(true));
        healbox.addStrategy(new HitboxStrategy(state, healbox, user.getBodyData()) {

            //delay exists so the projectile doesn't immediately contact the shooter
            private float delay = SELF_HIT_DELAY;
            @Override
            public void controller(float delta) {
                if (delay >= 0) {
                    delay -= delta;
                }
            }

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    //if shooting self after delay or any ally, the arrow will heal. Otherwise, damage is inflicted
                    if (UserDataType.BODY.equals(fixB.getType())) {

                        if ((fixB == user.getBodyData() && delay <= 0) || (fixB != user.getBodyData() && ((BodyData) fixB).getSchmuck().getHitboxFilter() == user.getHitboxFilter())) {
                            ((BodyData) fixB).regainHp(heal, creator, true);
                            SoundEffect.COIN3.playSourced(state, hbox.getPixelPosition(), 0.5f);
                            ParticleEntity heal = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.BOW_HEAL, 1.0f,
                                    true, SyncType.NOSYNC);
                            if (!state.isServer()) {
                                ((PlayStateClient) state).addEntity(heal.getEntityID(), heal, false, PlayStateClient.ObjectLayer.HBOX);
                            }
                            hurtbox.die();
                        } else if (((BodyData) fixB).getSchmuck().getHitboxFilter() != user.getHitboxFilter()) {
                            ParticleEntity hurt = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.BOW_HURT, 1.0f,
                                    true, SyncType.NOSYNC);
                            if (!state.isServer()) {
                                ((PlayStateClient) state).addEntity(hurt.getEntityID(), hurt, false, PlayStateClient.ObjectLayer.HBOX);
                            }
                        }
                    }
                }
            }
        });
        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(healbox.getEntityID(), healbox, false, PlayStateClient.ObjectLayer.HBOX);
        }
        return hurtbox;
    }
}