package com.mygdx.hadal.strategies.enemy;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class DeathPlayerScore extends EnemyStrategy {

    public DeathPlayerScore(PlayState state, Enemy enemy) {
        super(state, enemy);
    }

    @Override
    public void die(BodyData perp, DamageSource source) {
        if (perp instanceof PlayerBodyData playerData) {
            state.getMode().processPlayerScoreChange(state, playerData.getPlayer(), 1);
        }
    }
}
