package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.users.User;

public class SniperReticle extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(153, 150);
    private static final float LIFESPAN = 1.0f;

    private static final Sprite PROJ_SPRITE = Sprite.SNIPER_RETICLE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        int connId = 0;
        if (extraFields.length > 0) {
            connId = (int) extraFields[0];
        }

        Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(startVelocity, PROJECTILE_SIZE.x),
                PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(), true, true, user, PROJ_SPRITE) {

            @Override
            public void render(SpriteBatch batch, Vector2 entityLocation) {
                super.render(batch, entityLocation);

            }
        };

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        User target = HadalGame.usm.getUsers().get(connId);
        if (null != target) {
            if (null != target.getPlayer()) {
                hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), target.getPlayer(), new Vector2(), new Vector2()));
            }
        }

        return hbox;
    }
}
