package com.mygdx.hadal.battle.attacks.special;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

public class Ping extends SyncedAttacker {

    private static final Vector2 PING_SIZE = new Vector2(60, 54);
    private static final Vector2 PING_ARROW_SIZE = new Vector2(60, 33);
    private static final Vector2 PING_OFFSET = new Vector2(0, -40);
    private static final float PING_LIFESPAN = 2.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.PING.playSourced(state, startPosition, 0.6f);

        Hitbox hbox = new RangedHitbox(state, startPosition, PING_SIZE, PING_LIFESPAN, new Vector2(),
                user.getHitboxFilter(), true, false, user, Sprite.NOTIFICATIONS_ALERT);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));

        Hitbox hboxPing = new RangedHitbox(state, new Vector2(startPosition).add(PING_OFFSET), PING_ARROW_SIZE, PING_LIFESPAN, new Vector2(),
                user.getHitboxFilter(), true, false, user, Sprite.NOTIFICATIONS_ALERT_PING);
        hboxPing.setSpriteSize(PING_ARROW_SIZE);
        hboxPing.setSyncDefault(false);

        hboxPing.addStrategy(new ControllerDefault(state, hboxPing, user.getBodyData()));
        hboxPing.addStrategy(new Static(state, hboxPing, user.getBodyData()));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(hboxPing.getEntityID(), hboxPing, false, ObjectLayer.HBOX);
        }

        return hbox;
    }
}