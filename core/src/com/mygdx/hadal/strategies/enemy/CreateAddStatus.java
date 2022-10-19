package com.mygdx.hadal.strategies.enemy;

import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class CreateAddStatus extends EnemyStrategy {

    private final Status status;

    public CreateAddStatus(PlayState state, Enemy enemy, Status status) {
        super(state, enemy);
        this.status = status;
    }

    @Override
    public void create() {
        enemy.getBodyData().addStatus(status);
    }
}
