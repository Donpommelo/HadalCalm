package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class MovementSwim extends EnemyStrategy {

    //this the frequency that the physics occurs

    //this is the enemy's ai state
    private SwimmingState currentState;

    //the speed of the enemy and the ranges that it tries to stay between from its target
    private float moveSpeed, minRange, maxRange;

    //noise determines the amount of randomness there is to this enemy's movement. (cd = frequency of noise, radius = magnitude of noise)
    private static final float noiseCd = 0.75f;
    private float noiseCdCount = noiseCd;
    private float noiseRadius = 8.0f;

    public MovementSwim(PlayState state, Enemy enemy, float startAngle) {
        super(state, enemy);
        this.moveSpeed = 1.0f;
        this.moveDirection.set(1, 0).setAngleDeg(startAngle);
        currentState = SwimmingState.STILL;
    }

    private static final float controllerInterval = 1 / 60f;
    private static final float distantMultiplier = 1.4f;
    private final Vector2 force = new Vector2();
    private final Vector2 currentVel = new Vector2();
    private final Vector2 currentDirection = new Vector2();
    private final Vector2 currentNoise = new Vector2();
    private final Vector2 moveDirection = new Vector2();
    private float controllerCount;
    @Override
    public void controller(float delta) {
        switch (currentState) {
            case CHASE:
                moveDirection.set(enemy.getMoveVector());
                //chasing enemies set their move direction towards their target if far away and away if too close.
                if (enemy.getMoveTarget() != null && enemy.isApproachTarget()) {
                    if (enemy.getMoveTarget().isAlive()) {
                        moveSpeed = 1.0f;

                        moveDirection.set(enemy.getPosition()).sub(enemy.getMoveTarget().getPosition());
                        float dist = moveDirection.len2();

                        if (dist > maxRange * maxRange) {
                            moveDirection.scl(-1.0f);
                        } else if (dist < maxRange * maxRange && dist > minRange * minRange) {
                            moveSpeed = 0.0f;
                        }
                    }
                }
                break;
            case STILL:
                moveSpeed = 0;
                break;
            case WANDER:
                moveDirection.set(currentNoise);
                break;
            default:
                break;
        }

        //process enemy movement noise
        noiseCdCount += delta;
        while (noiseCdCount >= noiseCd) {
            noiseCdCount -= noiseCd;
            currentNoise.setToRandomDirection().scl(noiseRadius);
        }
        if (!enemy.isApproachTarget()) {
            moveDirection.add(currentNoise);
        }

        //process enemy swimming physics
        controllerCount += delta;
        while (controllerCount >= controllerInterval) {
            controllerCount -= controllerInterval;

            //set desired velocity depending on move states.
            currentVel.set(enemy.getLinearVelocity());
            currentDirection.set(moveDirection).nor().scl(moveSpeed);
            float desiredXVel = enemy.getBodyData().getXAirSpeed() * currentDirection.x;
            float desiredYVel = enemy.getBodyData().getXAirSpeed() * currentDirection.y ;
            float accelX;
            float accelY;

            //Process acceleration based on bodyData stats.
            if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
                accelX = enemy.getBodyData().getXAirAccel();
            } else {
                accelX = enemy.getBodyData().getXAirDeaccel();
            }
            float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;

            if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
                accelY = enemy.getBodyData().getYAirAccel();
            } else {
                accelY = enemy.getBodyData().getYAirDeaccel();
            }
            float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;

            //apply resulting force
            force.set(newX - currentVel.x, newY - currentVel.y).scl(enemy.getMass());
            if (!enemy.isApproachTarget()) {
                force.scl(distantMultiplier);
            }
            enemy.applyLinearImpulse(force);
        }
    }

    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }

    public void setCurrentState(SwimmingState currentState) { this.currentState = currentState; }

    public void setMinRange(float minRange) { this.minRange = minRange; }

    public void setMaxRange(float maxRange) { this.maxRange = maxRange; }

    public void setNoiseRadius(float noiseRadius) { this.noiseRadius = noiseRadius; }

    public Vector2 getMoveDirection() { return moveDirection; }

    public enum SwimmingState {
        STILL,
        WANDER,
        CHASE,
        OTHER,
    }
}
