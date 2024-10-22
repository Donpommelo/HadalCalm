package com.mygdx.hadal.battle.attacks.enemy;

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
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class KingKamabokoShot extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    private static final int BASE_DAMAGE = 10;
    private static final int KNOCKBACK = 25;
    private static final float LIFESPAN = 3.0f;

    private static final float HOME_POWER = 360.0f;
    private static final int HOME_RADIUS = 100;
    private static final float FRAG_SPEED = 10.0f;
    private static final int NUM_PROJ = 6;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIT.playSourced(state, startPosition, 0.8f, 0.6f);

        int type = 1;
        if (extraFields.length > 0) {
            type = (int) extraFields[0];
        }

        Hitbox hbox = new Hitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x),
                PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user,
                Sprite.NOTHING);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_SHOWER));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_IMPACT));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.SQUISH, 0.75f).setPitch(0.8f));

        if (type >= 2) {
            hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), HOME_POWER, HOME_RADIUS));
        }
        if (type == 3) {
            hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                @Override
                public void die() {
                    Vector2 fragVelo = new Vector2(0, FRAG_SPEED);
                    Vector2 fragPosition = new Vector2(hbox.getPixelPosition());
                    for (int i = 0; i < NUM_PROJ; i++) {
                        fragVelo.setAngleDeg(60 * i);
                        fragPosition.set(hbox.getPixelPosition()).add(new Vector2(fragVelo).nor().scl(5));
                        Hitbox frag = new Hitbox(state, fragPosition, PROJECTILE_SIZE, LIFESPAN, fragVelo, user.getHitboxFilter(),
                                true, true, user, Sprite.NOTHING);
                        frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
                        frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
                        frag.addStrategy(new ContactWallDie(state, frag, user.getBodyData()));
                        frag.addStrategy(new ContactUnitDie(state, frag, user.getBodyData()));
                        frag.addStrategy(new CreateParticles(state, frag, user.getBodyData(), Particle.KAMABOKO_SHOWER));
                        frag.addStrategy(new DieParticles(state, frag, user.getBodyData(), Particle.KAMABOKO_IMPACT));
                        frag.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));

                        if (!state.isServer()) {
                            ((ClientState) state).addEntity(frag.getEntityID(), frag, false, ObjectLayer.HBOX);
                        }
                    }
                }
            });
        }

        return hbox;
    }
}
