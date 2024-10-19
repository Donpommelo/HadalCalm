package com.mygdx.hadal.battle.attacks.enemy;

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
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class NeptuneSporeburst extends SyncedAttacker {

    private static final Vector2 SPORE_SIZE = new Vector2(80, 80);
    private static final Vector2 SPORE_FRAG_SIZE = new Vector2(30, 30);
    private static final float SPORE_LIFESPAN = 3.0f;
    private static final float SPORE_FRAG_LIFESPAN = 6.0f;
    private static final float SPORE_DAMAGE = 18.0f;
    private static final float SPORE_KB = 5.0f;
    private static final float SPORE_FRAG_DAMAGE = 5.0f;
    private static final float SPORE_FRAG_KB = 8.0f;

    private static final float SPORE_HOMING = 50.0f;
    private static final int SPORE_HOMING_RADIUS = 120;

    private static final int SPORE_FRAG_NUMBER = 16;
    private static final float FRAG_DAMPEN = 2.0f;

    private static final Sprite PROJ_SPRITE = Sprite.SPORE_CLUSTER_MILD;
    private static final Sprite FRAG_SPRITE = Sprite.SPORE_MILD;
    private static final float LINGER = 1.0f;

    final Vector2 position = new Vector2();
    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundEffect.SPIT.playSourced(state, position, 1.2f, 0.5f);

        RangedHitbox hbox = new RangedHitbox(state, position, SPORE_SIZE, SPORE_LIFESPAN,
                startVelocity, user.getHitboxFilter(), false, false, user, PROJ_SPRITE);
        hbox.setRestitution(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), SPORE_DAMAGE, SPORE_KB,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), SPORE_HOMING, SPORE_HOMING_RADIUS).setSteering(false));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true).setSynced(false));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, LINGER)
                .setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 newVelocity = new Vector2();
            @Override
            public void die() {
                SoundEffect.EXPLOSION_FUN.playSourced(state, hbox.getPixelPosition(), 1.0f, 0.6f);

                for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
                    if (extraFields.length > i * 2 + 1) {
                        newVelocity.set(extraFields[i * 2], extraFields[i * 2 + 1]);

                        RangedHitbox frag = getFrag();

                        frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                        frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), SPORE_FRAG_DAMAGE, SPORE_FRAG_KB,
                                DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setStaticKnockback(true));
                        frag.addStrategy(new ContactUnitDie(state, frag, user.getBodyData()));
                        frag.addStrategy(new ContactUnitSound(state, frag, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                                .setSynced(false));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(frag.getEntityID(), frag, false, ClientState.ObjectLayer.HBOX);
                        }
                    }
                }
            }

            private RangedHitbox getFrag() {
                RangedHitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), SPORE_FRAG_SIZE, SPORE_FRAG_LIFESPAN,
                        new Vector2(newVelocity), user.getHitboxFilter(), false, false, user, FRAG_SPRITE) {

                    @Override
                    public void create() {
                        super.create();
                        getBody().setLinearDamping(FRAG_DAMPEN);
                    }
                };
                frag.setRestitution(1.0f);
                return frag;
            }
        });

        return hbox;
    }
}
