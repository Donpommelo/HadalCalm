package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class ChargeBeamProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(35, 35);
    public static final float LIFESPAN = 0.6f;
    public static final float BASE_DAMAGE = 18.0f;
    public static final float MAX_DAMAGE_MULTIPLIER = 5.0f;
    private static final float RECOIL = 7.5f;
    private static final float KNOCKBACK = 10.0f;

    private static final Sprite PROJ_SPRITE = Sprite.CHARGE_BEAM;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LASERHARPOON.playSourced(state, startPosition, 0.8f);
        user.recoil(startVelocity, RECOIL);

        float chargeAmount = 0.0f;
        if (extraFields.length > 0) {
            chargeAmount = extraFields[0];
        }
        int chargeStage;

        //power of hitbox scales to the amount charged
        if (chargeAmount >= 1.0f) {
            chargeStage = 2;
        } else if (chargeAmount >= 0.5f) {
            chargeStage = 1;
        } else {
            chargeStage = 0;
        }

        float sizeMultiplier = 1.0f;
        float damageMultiplier = 1.5f;
        float kbMultiplier = 1;

        switch (chargeStage) {
            case 2 -> {
                sizeMultiplier = 2.0f;
                damageMultiplier = MAX_DAMAGE_MULTIPLIER;
                kbMultiplier = 3.0f;
            }
            case 1 -> {
                sizeMultiplier = 1.2f;
                damageMultiplier = 2.5f;
                kbMultiplier = 2.0f;
            }
        }

        final float damageMultiplier2 = damageMultiplier;
        final float kbMultiplier2 = kbMultiplier;

        Hitbox wallCollider = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        wallCollider.setEffectsHit(false);
        wallCollider.setEffectsMovement(false);
        wallCollider.setEffectsVisual(false);

        wallCollider.addStrategy(new ControllerDefault(state, wallCollider, user.getBodyData()));
        wallCollider.addStrategy(new ContactWallDie(state, wallCollider, user.getBodyData()));

        Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(PROJECTILE_SIZE).scl(sizeMultiplier), LIFESPAN, startVelocity,
                user.getHitboxFilter(), true, true, user, PROJ_SPRITE);
        hbox.setSyncDefault(false);
        hbox.setDurability(3);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.6f, true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), wallCollider, new Vector2(), new Vector2()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    fixB.receiveDamage(BASE_DAMAGE * damageMultiplier2, this.hbox.getLinearVelocity().nor().scl(KNOCKBACK * kbMultiplier2),
                            user.getBodyData(), true, hbox, DamageSource.CHARGE_BEAM, DamageTag.ENERGY, DamageTag.RANGED);
                }
            }
        });

        if (chargeStage == 2) {
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE).setParticleSize(70));
            hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.OVERCHARGE).setParticleDuration(0.4f));
        }

        if (!state.isServer()) {
            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
        }
        return wallCollider;
    }
}