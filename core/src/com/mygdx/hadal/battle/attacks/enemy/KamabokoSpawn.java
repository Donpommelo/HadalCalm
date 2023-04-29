package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class KamabokoSpawn extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    private static final float LIFESPAN = 3.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_RED;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIT.playSourced(state, startPosition, 1.0f, 0.60f);

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x),
                PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, PROJ_SPRITE);
        hbox.setGravity(1.0f);

        if (state.isServer()) {
            hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                @Override
                public void die() {
                    EnemyType.CRAWLER1.generateEnemy(state, hbox.getPixelPosition(), user.getHitboxFilter(), 0.0f);
                }
            });
        }

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_IMPACT).setSyncType(SyncType.NOSYNC));


        return hbox;
    }
}
