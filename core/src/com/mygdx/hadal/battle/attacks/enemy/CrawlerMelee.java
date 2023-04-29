package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class CrawlerMelee extends SyncedAttacker {

    private static final int ATTACK_1_DAMAGE = 12;
    private static final int DEFAULT_MELEE_KB = 20;
    private static final Vector2 MELEE_SIZE = new Vector2(100.0f, 100.0f);
    private static final int MELEE_RANGE = 1;
    private static final float MELEE_INTERVAL = 0.25f;

    private static final Sprite PROJ_SPRITE = Sprite.IMPACT;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        float moveDirection = 0;
        if (extraFields.length > 0) {
            moveDirection = extraFields[0];
        }

        Hitbox hbox = new Hitbox(state, startPosition, MELEE_SIZE, MELEE_INTERVAL, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.makeUnreflectable();
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), ATTACK_1_DAMAGE, DEFAULT_MELEE_KB,
                DamageSource.ENEMY_ATTACK, DamageTag.MELEE).setStaticKnockback(true));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(),
                new Vector2(MELEE_RANGE * moveDirection, 0)).setRotate(true));

        return hbox;
    }
}
