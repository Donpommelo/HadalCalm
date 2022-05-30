package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotController;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;
import com.mygdx.hadal.utils.Constants;

public class TargetPathfinding extends EnemyStrategy {

    private final BotController controller;

    public TargetPathfinding(PlayState state, Enemy enemy) {
        super(state, enemy);
        this.controller = new BotController(enemy) {

            @Override
            public void processBotMovement(Vector2 playerLocation, Vector2 playerVelocity) {
                super.processBotMovement(playerLocation, playerVelocity);
                enemy.getMoveVector().set(thisLocation);
                enemy.setMoveTarget(findTarget());
                enemy.setAttackTarget(shootTarget);

                enemy.setApproachTarget(false);
                if (shootTarget != null) {
                    if (BotManager.raycastUtility(enemy, playerLocation, shootTarget.getPosition(), Constants.BIT_PROJECTILE) == 1.0f) {
                        enemy.setApproachTarget(true);
                    }
                }
            }
        };
    }

    private final Vector2 entityWorldLocation = new Vector2();
    private final Vector2 entityVelocity = new Vector2();
    @Override
    public void acquireTarget() {
        entityWorldLocation.set(enemy.getPosition());
        entityVelocity.set(enemy.getLinearVelocity());
        controller.acquireTarget(entityWorldLocation, entityVelocity);
        controller.processBotMovement(entityWorldLocation, entityVelocity);
    }
}
