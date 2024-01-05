package com.mygdx.hadal.strategies.enemy;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class CreateBossEffects extends EnemyStrategy {

    public CreateBossEffects(PlayState state, Enemy enemy) {
        super(state, enemy);
    }

    @Override
    public void create() {
        if (state.isServer()) {
            for (User user : HadalGame.usm.getUsers().values()) {
                if (user.getPlayer() != null) {
                    if (user.getPlayer().getPlayerData() != null) {
                        user.getPlayer().getPlayerData().statusProcTime(new ProcTime.AfterBossSpawn(enemy));
                    }
                }
            }
        }
    }
}
