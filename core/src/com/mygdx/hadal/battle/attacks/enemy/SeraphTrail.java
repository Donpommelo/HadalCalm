package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class SeraphTrail extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(40, 20);
    private static final float LIFESPAN = 10.0f;
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LASER_TRAIL_SLOW, 0.0f, LINGER)
                .setParticleSize(15.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}
