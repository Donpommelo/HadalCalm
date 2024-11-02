package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Impermeable;
import com.mygdx.hadal.statuses.ResetVelocity;
import com.mygdx.hadal.strategies.hitbox.*;

public class GhostStepProjectile extends SyncedAttacker {

    public static final float BASE_DAMAGE = 20.0f;
    private static final Vector2 HITBOX_SIZE = new Vector2(90, 120);
    private static final float RECOIL = 75.0f;
    private static final float LIFESPAN = 0.20f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.WOOSH).setPosition(startPosition));

        int direction;
        if (MoveState.MOVE_LEFT.equals(user.getMoveState())) {
            direction = -1;
        } else if (MoveState.MOVE_RIGHT.equals(user.getMoveState())) {
            direction = 1;
        } else if (startVelocity.x > 0) {
            direction = 1;
        } else {
            direction = -1;
        }

        user.getBodyData().addStatus(new Impermeable(state, LIFESPAN, user.getBodyData(), user.getBodyData()));
        user.getBodyData().addStatus(new ResetVelocity(state, LIFESPAN, user.getBodyData(), user.getBodyData(),
                user.getLinearVelocity()));

        user.setLinearVelocity(RECOIL * direction, 0);

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, 0.0f, DamageSource.GHOST_STEP,
                DamageTag.MAGIC));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SHADOW_PATH));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));

        return hbox;
    }
}