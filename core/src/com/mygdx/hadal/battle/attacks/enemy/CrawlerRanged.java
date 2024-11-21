package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class CrawlerRanged extends SyncedAttacker {

    private static final float BASE_DAMAGE = 7.0f;
    private static final float KNOCKBACK = 12.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(16, 16);
    private static final float LIFESPAN = 1.2f;
    private static final int SPREAD = 10;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_RED;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        if (startPosition.length != 0) {
            SoundManager.play(state, new SoundLoad(SoundEffect.SPIT)
                    .setVolume(0.8f)
                    .setPosition(startPosition[0]));

            for (int i = 0; i < startPosition.length; i++) {

                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i],
                        user.getHitboxFilter(), true, true, user, PROJ_SPRITE);
                hbox.setGravity(3.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
                hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}