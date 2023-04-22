package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class CommuterParasolActivate extends SyncedAttacker {

    public static final float LIFESPAN = 3.0f;
    private static final Vector2 SIZE = new Vector2(150, 20);
    private static final Vector2 POSITION = new Vector2(0, 2.5f);

    private static final Sprite SPRITE = Sprite.ORB_BLUE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {

        Hitbox hbox = new Hitbox(state, user.getPixelPosition(), SIZE, LIFESPAN, new Vector2(0, 0),
                user.getHitboxFilter(), true, false, user, SPRITE);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), POSITION));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void onHit(HadalData fixB) {
                if (fixB != null) {
                    if (UserDataType.HITBOX.equals(fixB.getType())) {
                        if (fixB.getEntity().isAlive()) {
                            Vector2 newVelo = new Vector2(fixB.getEntity().getPosition()).sub(user.getPosition());
                            fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().setAngleDeg(newVelo.angleDeg()));

                            SoundEffect.SPRING.playSourced(state, user.getPixelPosition(), 0.2f);
                        }
                    }
                }
            }
        });

        return hbox;
    }
}