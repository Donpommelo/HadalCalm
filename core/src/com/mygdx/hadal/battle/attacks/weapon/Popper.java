package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Popper extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    public static final float LIFESPAN = 0.3f;
    public static final float BASE_DAMAGE = 61.0f;
    private static final float RECOIL = 12.0f;
    private static final float KNOCKBACK = 30.0f;

    public static final float FRAG_DAMAGE = 20.0f;
    private static final float FRAG_SPEED = 50.0f;
    private static final Vector2 FRAG_SIZE = new Vector2(18, 18);
    private static final float FRAG_LIFESPAN = 1.2f;
    private static final float FRAG_KNOCKBACK = 2.0f;

    private static final float PROJ_DAMPEN = 9.0f;
    private static final float FRAG_DAMPEN = 3.0f;

    private static final Sprite PROJ_SPRITE = Sprite.POPPER;
    private static final Sprite[] FRAG_SPRITES = {Sprite.ORB_PINK, Sprite.ORB_RED, Sprite.ORB_BLUE, Sprite.ORB_YELLOW, Sprite.ORB_ORANGE};

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.CRACKER1.playSourced(state, startPosition, 1.0f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.PARTY_POPPER, DamageTag.RANGED));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.CRACKER2, 0.4f).setSynced(false));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.NOISEMAKER, 0.4f).setSynced(false));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.PARTY));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                hbox.getBody().setLinearDamping(PROJ_DAMPEN);
            }

            private final Vector2 hboxPosition = new Vector2();
            @Override
            public void die() {
                Vector2 newVelocity = new Vector2();
                hboxPosition.set(hbox.getPixelPosition());
                for (float newDegrees : extraFields) {
                    newVelocity.set(0, 1).nor().scl(FRAG_SPEED);

                    int randomIndex = MathUtils.random(FRAG_SPRITES.length - 1);
                    Sprite projSprite = FRAG_SPRITES[randomIndex];

                    Hitbox frag = new RangedHitbox(state, hboxPosition, FRAG_SIZE, FRAG_LIFESPAN,
                            newVelocity.setAngleDeg(newDegrees), user.getHitboxFilter(), false, true,
                            user, projSprite) {

                        @Override
                        public void create() {
                            super.create();
                            getBody().setLinearDamping(FRAG_DAMPEN);
                        }
                    };
                    frag.setSyncDefault(false);
                    frag.setGravity(7.5f);
                    frag.setDurability(3);

                    frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                    frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
                    frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), FRAG_DAMAGE, FRAG_KNOCKBACK,
                            DamageSource.PARTY_POPPER, DamageTag.RANGED));

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(frag.getEntityID(), frag, false, ObjectLayer.HBOX);
                    }
                }
            }
        });

        return hbox;
    }
}