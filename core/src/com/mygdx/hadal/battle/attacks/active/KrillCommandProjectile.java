package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.MarkedKrill;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class KrillCommandProjectile extends SyncedAttacker {

    public static final float BASE_DAMAGE = 20.0f;
    private static final Vector2 HITBOX_SIZE = new Vector2(200, 240);
    private static final float RANGE = 4.5f;
    private static final float PARTICLE_OFFSET_RIGHT = -100.0f;
    private static final float PARTICLE_OFFSET_LEFT = 100.0f;
    private static final float LIFESPAN = 0.60f;
    private static final float DURATION = 10.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.POPTAB)
                .setVolume(0.8f)
                .setPosition(startPosition));

        int direction;
        if (startVelocity.x > 0) {
            direction = 1;
        } else {
            direction = -1;
        }

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN,
                new Vector2(), user.getHitboxFilter(),true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2(direction * RANGE, 0)));

        if (direction == 1) {
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KRILL_RIGHT)
                    .setOffset(PARTICLE_OFFSET_RIGHT, 0));
        } else {
            hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KRILL_LEFT)
                    .setOffset(PARTICLE_OFFSET_LEFT, 0));
        }

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB instanceof BodyData bodyData) {
                    bodyData.addStatus(new MarkedKrill(state, DURATION, creator, bodyData));
                }
            }
        });

        return hbox;
    }
}