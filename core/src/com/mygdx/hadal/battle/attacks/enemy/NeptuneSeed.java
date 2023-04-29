package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.FlashShaderNearDeath;

public class NeptuneSeed extends SyncedAttacker {

    public static final Vector2 SEED_SIZE = new Vector2(45, 30);
    private static final float SEED_SPEED = 15.0f;
    private static final float LIFESPAN = 5.0f;
    private static final float VINE_SPEED = 20.0f;
    private static final int VINE_NUM = 11;

    private static final Sprite PROJ_SPRITE = Sprite.BEAN;

    final Vector2 position = new Vector2();
    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundEffect.SPIT.playSourced(state, position, 1.2f, 0.5f);

        RangedHitbox hbox = new RangedHitbox(state, position, SEED_SIZE, LIFESPAN, new Vector2(0, SEED_SPEED),
                user.getHitboxFilter(), false, false, user, PROJ_SPRITE);
        hbox.setGravity(5.0f);
        hbox.setFriction(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                 @Override
                 public void die() {
                     WeaponUtils.createVine(state, user, hbox.getPixelPosition(), new Vector2(0, VINE_SPEED),
                             VINE_NUM, 0, SyncedAttack.NEPTUNE_VINE);
                 }
             }
        );

        return hbox;
    }
}
