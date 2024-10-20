package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.PoisonTrail;

public class NeptunePoison extends SyncedAttacker {

    private static final float POISON_BREATH_SPEED = 10.0f;
    private static final float POISON_CLOUD_SPEED = 20.0f;
    private static final float POISON_1_LIFESPAN = 5.0f;
    private static final float POISON_2_LIFESPAN = 2.0f;
    private static final float POISON_BREATH_LIFESPAN = 1.5f;
    private static final float POISON_CLOUD_LIFESPAN = 6.0f;
    private static final float POISON_DAMAGE = 0.3f;
    private static final float POISON_PARTICLE_LIFESPAN = 3.0f;
    private static final float POISON_PARTICLE_INTERVAL = 512.0f;
    private static final Vector2 POISON_SIZE = new Vector2(250, 101);
    private static final Vector2 POISON_CLOUD_SIZE = new Vector2(101, 250);

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        RangedHitbox poison = new RangedHitbox(state, startPosition, POISON_SIZE, POISON_1_LIFESPAN,
                new Vector2(0, -POISON_BREATH_SPEED), user.getHitboxFilter(), true, false, user, Sprite.NOTHING);
        poison.setPassability(BodyConstants.BIT_WALL);
        poison.makeUnreflectable();

        poison.addStrategy(new ControllerDefault(state, poison, user.getBodyData()));
        poison.addStrategy(new ContactWallDie(state, poison, user.getBodyData()));
        poison.addStrategy(new CreateSound(state, poison, user.getBodyData(), SoundEffect.OOZE, 0.8f, true)
                .setSyncType(SyncType.NOSYNC));
        poison.addStrategy(new PoisonTrail(state, poison, user.getBodyData(), POISON_SIZE, (int) POISON_SIZE.y, POISON_DAMAGE,
                POISON_BREATH_LIFESPAN, user.getHitboxFilter())
                .setParticle(Particle.POLLEN_POISON, POISON_PARTICLE_LIFESPAN, POISON_PARTICLE_INTERVAL));
        poison.addStrategy(new HitboxStrategy(state, poison, user.getBodyData()) {

               @Override
               public void die() {
                   createPoisonWave(1);
                   createPoisonWave(-1);
               }

               private void createPoisonWave(int direction) {
                   RangedHitbox hbox = new RangedHitbox(state, poison.getPixelPosition(), POISON_SIZE, POISON_2_LIFESPAN,
                           new Vector2(direction * POISON_CLOUD_SPEED, 0), user.getHitboxFilter(), false,
                           false, user, Sprite.NOTHING);
                   hbox.makeUnreflectable();
                   hbox.setGravity(1.0f);

                   hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                   hbox.addStrategy(new PoisonTrail(state, hbox, user.getBodyData(), POISON_CLOUD_SIZE, (int) POISON_CLOUD_SIZE.x,
                           POISON_DAMAGE, POISON_CLOUD_LIFESPAN, user.getHitboxFilter())
                           .setParticle(Particle.POLLEN_POISON, POISON_PARTICLE_LIFESPAN, POISON_PARTICLE_INTERVAL));

                   if (!state.isServer()) {
                       ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
                   }
               }
           }
        );

        return poison;
    }
}
