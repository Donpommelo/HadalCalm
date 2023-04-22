package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;

public class GroundedHelper {

    private boolean grounded;

    //These track whether the schmuck has a specific artifacts equipped (to enable wall scaling.)
    private boolean groundedOverride;

    private final Player player;

    public GroundedHelper(Player player) {
        this.player = player;
    }

    public void controllerInterval() {

        //Determine if the player is in the air or on ground.
        grounded = player.getFeetData().getNumContacts() > 0 || groundedOverride;

        //player's jumps are refreshed on the ground
        if (grounded) {
            player.getJumpHelper().setExtraJumpsUsed(0);
        }

        player.getEffectHelper().toggleRunningEffects((MoveState.MOVE_LEFT.equals(player.getMoveState())
                || MoveState.MOVE_RIGHT.equals(player.getMoveState())) && grounded);
    }

    public boolean isGrounded() { return grounded; }

    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    public void setGroundedOverride(boolean groundedOverride) { this.groundedOverride = groundedOverride; }
}
