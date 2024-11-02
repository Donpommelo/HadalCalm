package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Static;

import static com.mygdx.hadal.constants.Constants.PPM;

public class TeslaActivation extends SyncedAttacker {

    public static final float PULSE_DAMAGE = 40.0f;
    private static final Vector2 PULSE_SIZE = new Vector2(75, 75);
    private static final float PULSE_DURATION = 0.5f;
    private static final float PULSE_KNOCKBACK = 20.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.ZAP)
                .setVolume(0.4f)
                .setPosition(startPosition));

        //draw a path of hitboxes between the 2 activated coils that damage enemies that pass through
        Vector2 pulsePosition = new Vector2(startPosition);
        Vector2 otherPosition = new Vector2();
        Vector2 pulsePath = new Vector2();
        if (extraFields.length >= 2) {
            otherPosition.set(extraFields[0], extraFields[1]);
            pulsePath.set(otherPosition).sub(pulsePosition);
        }

        float dist = pulsePath.len();
        for (int i = 0; i < dist - PULSE_SIZE.x; i += (int) PULSE_SIZE.x) {
            pulsePosition.add(pulsePath.nor().scl(PULSE_SIZE));

            Hitbox pulse = new RangedHitbox(state, pulsePosition, PULSE_SIZE, PULSE_DURATION, new Vector2(), user.getHitboxFilter(),
                    true, true, user, Sprite.NOTHING);
            pulse.setPassability((short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));
            pulse.setSyncDefault(false);
            pulse.setEffectsHit(false);

            pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
            pulse.addStrategy(new CreateParticles(state, pulse, user.getBodyData(), Particle.LASER_PULSE)
                    .setParticleSize(50));

            if (!state.isServer()) {
                ((ClientState) state).addEntity(pulse.getEntityID(), pulse, false, ObjectLayer.HBOX);
            }
        }

        Hitbox hboxDamage = new RangedHitbox(state, startPosition, new Vector2(otherPosition.dst(startPosition), PULSE_SIZE.y),
                PULSE_DURATION, new Vector2(), user.getHitboxFilter(), true, true, user, Sprite.NOTHING) {

            private final Vector2 newPosition = new Vector2();
            @Override
            public void create() {
                super.create();

                //this makes the laser hbox's lifespan unmodifiable
                setLifeSpan(PULSE_DURATION);

                //Rotate hitbox to match angle of fire.
                newPosition.set(otherPosition).sub(startPosition);
                float newAngle = MathUtils.atan2(newPosition.y , newPosition.x);

                newPosition.set(startPosition).add(otherPosition).scl(0.5f);
                setTransform(newPosition.x / PPM, newPosition.y / PPM, newAngle);
            }
        };
        hboxDamage.setEffectsVisual(false);

        hboxDamage.addStrategy(new ControllerDefault(state, hboxDamage, user.getBodyData()));
        hboxDamage.addStrategy(new DamageStandard(state, hboxDamage, user.getBodyData(), PULSE_DAMAGE, PULSE_KNOCKBACK,
                DamageSource.TESLA_COIL, DamageTag.ENERGY, DamageTag.RANGED).setStaticKnockback(true));
        hboxDamage.addStrategy(new Static(state, hboxDamage, user.getBodyData()));

        return hboxDamage;
    }
}