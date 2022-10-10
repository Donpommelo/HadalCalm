package com.mygdx.hadal.strategies.enemy;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.EnemyStrategy;
import com.mygdx.hadal.constants.Stats;

public class CreateMultiplayerHpScaling extends EnemyStrategy {

    private final float hp;

    public CreateMultiplayerHpScaling(PlayState state, Enemy enemy, float hp) {
        super(state, enemy);
        this.hp = hp;
    }

    @Override
    public void create() {
        if (state.isServer()) {
            multiplayerScaling(HadalGame.server.getNumPlayers());
        } else {
            multiplayerScaling(HadalGame.client.getNumPlayers());
        }
    }

    public void multiplayerScaling(int numPlayers) {
        enemy.getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, hp * numPlayers, enemy.getBodyData()));
    }
}
