package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class FollowRallyPoints extends EnemyStrategy {

    //This is a dummy event in the map that the enemy is moving towards
    private HadalEntity eventTarget;

    private float moveSpeed;

    public FollowRallyPoints(PlayState state, Enemy enemy) {
        super(state, enemy);
    }

    private final Vector2 dist = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    @Override
    public void controller(float delta) {
        //move towards movement target, if existent.
        if (eventTarget != null) {
            targetPosition.set(eventTarget.getPosition());
            dist.set(targetPosition).sub(enemy.getPosition());

            //upon reaching target, conclude current action immediately and move on to the next action
            if (dist.len2() <= moveSpeed * moveSpeed * delta * delta) {
                enemy.setTransform(targetPosition, 0);
                enemy.setLinearVelocity(0, 0);
                eventTarget = null;

                enemy.setAiActionCdCount(0);
                enemy.setCurrentAction(null);
            } else {
                enemy.setLinearVelocity(dist.nor().scl(moveSpeed));
            }
        }
    }

    @Override
    public void setRallyEvent(HadalEntity entity, float moveSpeed) {
        this.eventTarget = entity;
        this.moveSpeed = moveSpeed;
    }
}
