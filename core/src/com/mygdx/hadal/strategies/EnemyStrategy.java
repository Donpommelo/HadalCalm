package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public abstract class EnemyStrategy implements IEnemyStrategy {

    //reference to game state.
    protected final PlayState state;

    //The enemy containing this data
    protected final Enemy enemy;

    public EnemyStrategy(PlayState state, Enemy enemy) {
        this.state = state;
        this.enemy = enemy;
    }

    @Override
    public void create() {}

    @Override
    public void controller(float delta) {}

    @Override
    public void render(SpriteBatch batch, float animationTime) {}

    @Override
    public void setRallyEvent(HadalEntity entity, float moveSpeed) {}

    @Override
    public void acquireTarget() {}

    @Override
    public void die(BodyData perp, DamageSource source) {}

    @Override
    public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) { return null; }

    @Override
    public Object onServerSync() { return null; }
}
