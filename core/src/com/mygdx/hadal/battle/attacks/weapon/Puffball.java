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
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Puffball extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    public static final float LIFESPAN = 5.0f;
    public static final float BASE_DAMAGE = 36.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 8.0f;
    private static final float FLASH_LIFESPAN = 1.0f;

    public static final int SPORE_FRAG_NUMBER = 15;
    public static final float SPORE_FRAG_LIFESPAN = 5.0f;
    public static final float SPORE_FRAG_DAMAGE = 12.0f;
    private static final Vector2 SPORE_FRAG_SIZE = new Vector2(30, 30);
    private static final float SPORE_FRAG_KB = 8.0f;
    private static final float FRAG_DAMPEN = 2.2f;

    private static final Sprite projSprite = Sprite.SPORE_CLUSTER_YELLOW;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIT.playSourced(state, startPosition, 1.2f, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false,true, user, projSprite) {

            private final Vector2 targetLocation = new Vector2();
            @Override
            public void create() {
                float damp = 0.0f;
                if (extraFields.length > 1) {
                    targetLocation.set(extraFields[0], extraFields[1]);
                    float dist = targetLocation.dst(new Vector2(startPosition));
                    damp = distToDamp(dist);
                    float initialVelocity = (float) Math.sqrt(2 * dist * damp);
                    startVelo.nor().scl(initialVelocity);
                }
                super.create();
                body.setLinearDamping(damp);
            }

            private float count;
            private static final float SPEED_CHECK_INTERVAL = 0.1f;
            private static final float SPEED_THRESHOLD = 5.0f;
            @Override
            public void controller(float delta) {
                super.controller(delta);

                count += delta;
                if (count >= SPEED_CHECK_INTERVAL) {
                    count -= SPEED_CHECK_INTERVAL;
                    if (getLinearVelocity().len2() < SPEED_THRESHOLD) {
                        die();
                    }
                }
            }

            @Override
            public void die() {
                createFrags();
                super.die();
            }

            private final Vector2 newVelocity = new Vector2();
            private void createFrags() {
                SoundEffect.EXPLOSION_FUN.playSourced(state, getPixelPosition(), 1.0f, 0.6f);

                for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
                    if (extraFields.length > i * 2 + 3) {
                        newVelocity.set(extraFields[i * 2 + 2], extraFields[i * 2 + 3]);

                        RangedHitbox frag = new RangedHitbox(state, getPixelPosition(), new Vector2(SPORE_FRAG_SIZE), SPORE_FRAG_LIFESPAN,
                                new Vector2(newVelocity), user.getHitboxFilter(), false, false, user, Sprite.SPORE_YELLOW) {

                            @Override
                            public void create() {
                                super.create();
                                getBody().setLinearDamping(FRAG_DAMPEN);
                            }
                        };
                        frag.setRestitution(1.0f);
                        frag.setSyncDefault(false);

                        frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                        frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), SPORE_FRAG_DAMAGE, SPORE_FRAG_KB,
                                DamageSource.PUFFBALLER, DamageTag.RANGED).setStaticKnockback(true));
                        frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
                        frag.addStrategy(new FlashNearDeath(state, frag, user.getBodyData(), FLASH_LIFESPAN));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(frag.getEntityID(), frag, false, ClientState.ObjectLayer.HBOX);
                        }
                    }
                }
            }
        };
        hbox.setRestitution(1.0f);
        hbox.setSyncedDeleteNoDelay(true);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.PUFFBALLER, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }

    private float distToDamp(float dist) {
        if (dist < 220) {
            return 9.0f;
        }
        if (dist < 250) {
            return 8.0f;
        }
        if (dist < 290) {
            return 7.0f;
        }
        if (dist < 340) {
            return 6.0f;
        }
        if (dist < 420) {
            return 5.0f;
        }
        if (dist < 510) {
            return 4.0f;
        }
        if (dist < 700) {
            return 3.0f;
        }
        return 2.0f;
    }
}