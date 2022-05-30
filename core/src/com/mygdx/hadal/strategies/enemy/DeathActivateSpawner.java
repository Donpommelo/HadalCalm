package com.mygdx.hadal.strategies.enemy;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class DeathActivateSpawner extends EnemyStrategy {

    private final SpawnerSchmuck spawner;

    public DeathActivateSpawner(PlayState state, Enemy enemy, SpawnerSchmuck spawner) {
        super(state, enemy);
        this.spawner = spawner;
    }

    @Override
    public void die(BodyData perp, DamageSource source) {
        if (spawner != null) {
            spawner.onDeath();
        }
    }
}
