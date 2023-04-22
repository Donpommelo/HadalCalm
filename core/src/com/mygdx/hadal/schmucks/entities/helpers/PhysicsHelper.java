package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.Player;

/**
 * A Physics schmuck runs custom acceleration physics for their movement.
 * atm, this includes only the player
 * @author Duldwin Droshibish
 */
public class PhysicsHelper {

    private final Player player;

    public PhysicsHelper(Player player) {
        this.player = player;
    }

    private final Vector2 force = new Vector2();
    private final Vector2 currentVel = new Vector2();
    public void controllerInterval() {

        currentVel.set(player.getLinearVelocity());
        boolean grounded = player.getGroundedHelper().isGrounded();
        float desiredXVel = 0.0f;
        float desiredYVel = 0.0f;

        //set desired velocity depending on move states.
        switch (player.getMoveState()) {
            case MOVE_LEFT:
                desiredXVel = grounded ? -player.getBodyData().getXGroundSpeed() : -player.getBodyData().getXAirSpeed();
                break;
            case MOVE_RIGHT:
                desiredXVel = grounded ? player.getBodyData().getXGroundSpeed() : player.getBodyData().getXAirSpeed();
                break;
            default:
                break;
        }

        float accelX;
        float accelY;

        //Process acceleration based on bodyData stats.
        if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
            accelX = grounded ? player.getBodyData().getXGroundAccel(): player.getBodyData().getXAirAccel();
        } else {
            accelX = grounded ? player.getBodyData().getXGroundDeaccel() : player.getBodyData().getXAirDeaccel();
        }
        float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;

        if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
            accelY = grounded ? player.getBodyData().getYGroundAccel(): player.getBodyData().getYAirAccel();
        } else {
            accelY = grounded ? player.getBodyData().getYGroundDeaccel() : player.getBodyData().getYAirDeaccel();
        }
        float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;

        //apply resulting force
        force.set(newX - currentVel.x, newY - currentVel.y).scl(player.getMass());
        player.applyLinearImpulse(force);
    }
}
