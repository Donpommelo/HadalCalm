package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

public class SamsonOptionUse extends SyncedAttacker {

    private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);

    private static final float DURATION = 1.5f;
    private static final float PROC_CD = 0.12f;
    private static final int EXPLOSION_RADIUS = 200;
    private static final float EXPLOSION_DAMAGE = 50.0f;
    private static final float EXPLOSION_KNOCKBACK = 30.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, DURATION, new Vector2(), (short) 0, false,
                false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float procCdCount = PROC_CD;
            private final Vector2 explosionPosition = new Vector2();
            int explosionNumber;
            @Override
            public void controller(float delta) {
                if (procCdCount < PROC_CD) {
                    procCdCount += delta;
                }
                if (procCdCount >= PROC_CD) {
                    procCdCount -= PROC_CD;

                    if (extraFields.length > explosionNumber * 2 + 1) {
                        float nextX = extraFields[explosionNumber * 2];
                        float nextY = extraFields[explosionNumber * 2 + 1];

                        SoundManager.play(state, new SoundLoad(SoundEffect.EXPLOSION6)
                                .setVolume(0.5f)
                                .setPosition(explosionPosition.set(nextX, nextY)));

                        WeaponUtils.createExplosion(state, explosionPosition, EXPLOSION_RADIUS, user, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                                user.getHitboxFilter(), false, DamageSource.SAMSON_OPTION);
                    }
                    explosionNumber++;
                }
            }
        });

        user.getBodyData().receiveDamage(9999, new Vector2(), user.getBodyData(), false, null, DamageSource.SAMSON_OPTION);

        return hbox;
    }
}