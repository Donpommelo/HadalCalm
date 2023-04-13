package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

public class TaintedWaterProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final float POISON_DAMAGE = 0.75f;
    public static final float POISON_DURATION = 2.5f;
    public static final float PROJECTILE_SPEED = 25.0f;

    private static final float LIFESPAN = 1.2f;
    private static final Vector2 POISON_SIZE = new Vector2(101, 50);
    private static final float POISON_SIZE_SQUARED = 15000f;
    private static final float POISON_SPREAD = 75f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.MAGIC27_EVIL.playSourced(state, startPosition, 1.0f);

        Hitbox hboxBase = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN,
                new Vector2(startVelocity).nor().scl(PROJECTILE_SPEED), user.getHitboxFilter(), false,
                false, user, Sprite.NOTHING);

        hboxBase.addStrategy(new ControllerDefault(state, hboxBase, user.getBodyData()));
        hboxBase.addStrategy(new ContactWallDie(state, hboxBase, user.getBodyData()));
        hboxBase.addStrategy(new HitboxStrategy(state, hboxBase, user.getBodyData()) {

            private final Vector2 lastPosition = new Vector2(startPosition);
            private float numPoison;
            @Override
            public void controller(float delta) {
                if (lastPosition.dst2(hbox.getPixelPosition()) > POISON_SIZE_SQUARED) {
                    numPoison++;
                    lastPosition.set(hbox.getPixelPosition());
                    Poison poison = new Poison(state, hbox.getPixelPosition(), new Vector2(POISON_SIZE).add(0, numPoison * POISON_SPREAD),
                            POISON_DAMAGE, POISON_DURATION, user,true, user.getHitboxFilter(), DamageSource.TAINTED_WATER) {

                        @Override
                        public void create() {
                            super.create();
                            setAngle(startVelocity.angleRad());
                        }
                    };

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(poison.getEntityID(), poison, false, ClientState.ObjectLayer.EFFECT);
                    }
                }
            }
        });

        return hboxBase;
    }
}