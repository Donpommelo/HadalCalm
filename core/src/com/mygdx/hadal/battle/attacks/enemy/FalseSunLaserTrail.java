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
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunLaserTrail extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(120, 60);
    private static final float LIFESPAN = 10.0f;
    private static final int DURABILITY = 9;
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();
        hbox.setDurability(DURABILITY);
        hbox.setRestitution(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LASER_TRAIL, 0.0f, LINGER)
                .setParticleSize(40.0f).setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}
