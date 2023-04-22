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
    private static final float KNOCKBACK = 5.0f;
    private static final float FLASH_LIFESPAN = 1.0f;
    private static final float DEATH_DELAY = 0.5f;

    public static final int SPORE_FRAG_NUMBER = 12;
    public static final float SPORE_FRAG_LIFESPAN = 5.0f;
    public static final float SPORE_FRAG_DAMAGE = 12.0f;
    private static final Vector2 SPORE_FRAG_SIZE = new Vector2(30, 30);
    private static final float SPORE_FRAG_KB = 8.0f;
    private static final float FRAG_DAMPEN = 1.2f;

    private static final Sprite projSprite = Sprite.SPORE_CLUSTER_YELLOW;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIT.playSourced(state, startPosition, 1.2f, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false,true, user, projSprite) {

            private boolean markedForDeath, died;
            private float count;
            @Override
            public void controller(float delta) {
                super.controller(delta);

                count += delta;
                if (count >= DEATH_DELAY) {
                    if (markedForDeath && !died) {
                        died = true;
                        createFrags();
                        super.die();
                    }
                }
            }

            @Override
            public void die() {
                if (count >= DEATH_DELAY && !died) {
                    died = true;
                    createFrags();
                    super.die();
                } else {
                    markedForDeath = true;
                }
            }

            private final Vector2 newVelocity = new Vector2();
            private void createFrags() {
                SoundEffect.EXPLOSION_FUN.playSourced(state, getPixelPosition(), 1.0f, 0.6f);

                for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
                    if (extraFields.length > i * 2 + 1) {
                        newVelocity.set(extraFields[i * 2], extraFields[i * 2 + 1]);

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
        hbox.setDurability(2);
        hbox.setSyncedDeleteNoDelay(true);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.PUFFBALLER, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}