package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 *
 *  @author Narsabaum Nolfner
 */
public class MouseHelper {

    //this is the mouse's position (instead of body position to avoid transforming)
    private final Vector2 mousePosition = new Vector2();

    //This tracks the location of the user's (host) mouse
    private final Vector3 tmpVec3 = new Vector3();

    //This tracks the location of a client mouse sent by packet
    private final Vector2 desiredLocation = new Vector2();

    private float attackAngle;

    //Does this mouse belong to the player or another networked player
    private final boolean self;

    public MouseHelper(PlayState state, Player player) {

        if (state.isServer()) {
            self = player.getConnID() == 0;
        } else {
            self = player instanceof PlayerSelfOnClient;
        }
    }

    private final Vector2 mouseLocation = new Vector2();
    private final Vector2 mouseAngle = new Vector2();
    public void controller(Vector2 playerPosition) {
        //server player's mouse sets location constantly. Client's mouse moves to desired location which is set when receiving packets from respective client
        if (self) {
            tmpVec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            HadalGame.viewportCamera.unproject(tmpVec3);
            mousePosition.set(tmpVec3.x / PPM, tmpVec3.y / PPM);
        } else {
            mousePosition.set(desiredLocation.x, desiredLocation.y);
        }

        mouseLocation.set(getPixelPosition());
        mouseAngle.set(playerPosition.x, playerPosition.y).sub(mouseLocation.x, mouseLocation.y);

        attackAngle = MathUtils.atan2(mouseAngle.y, mouseAngle.x) * MathUtils.radDeg;
    }

    /**
     * When receiving a mouse location from client, the server updates that client's mouse
     */
    public void setDesiredLocation(float x, float y) {
        this.desiredLocation.x = x;
        this.desiredLocation.y = y;
    }

    public Vector2 getPosition() { return mousePosition; }

    private final Vector2 mousePixelPosition = new Vector2();
    public Vector2 getPixelPosition() {
        mousePixelPosition.set(mousePosition).scl(PPM);
        mousePixelPosition.set((int) mousePixelPosition.x, (int) mousePixelPosition.y);
        return mousePixelPosition;
    }

    public float getAttackAngle() { return attackAngle; }
}
