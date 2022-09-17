package com.mygdx.hadal.input;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;

import java.util.Arrays;
import java.util.HashSet;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * The Action Controller receives actions from a player controller or packets from clients and uses them to make a player 
 * perform actions.
 * @author Lobideen Lolbatross
 */
public class ActionController {

	//this is the player that this controller control
	private final Player player;

	//Is the player currently holding move left/right? This is used for processing holding both buttons -> releasing one.
	private boolean leftDown;
	private boolean rightDown;
	
	public ActionController(Player player) {
		this.player = player;
	}
	
	/**
	 * onReset is true if this is being called by the controller being synced (from finishing a transition/opening window)
	 * when this happens, we don't want to trigger jumping/shooting, just reset our button-held statuses
	 */
	public void keyUp(PlayerAction action, boolean onReset) {
		if (player == null) { return; }
		if (player.getPlayerData() == null) { return; }
		
		if (action == PlayerAction.WALK_LEFT) {
			leftDown = false;
			if (rightDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (action == PlayerAction.WALK_RIGHT) {
			rightDown = false;
			if (leftDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (action == PlayerAction.JUMP) {
			player.setHoveringAttempt(false);
		}
		else if (action == PlayerAction.CROUCH) {
			player.setFastFalling(false);
		}
		else if (action == PlayerAction.FIRE) {
			if (!onReset) {
				player.release();
			}
			player.setShooting(false);
		}
	}
	
	public void keyDown(PlayerAction action, boolean onReset) {
		if (player == null) return;

		//when spectating, host interact activates the map's designated "spectator event", if existant
		if (player.getState().isServer() && player.equals(player.getState().getPlayer()) && player.getState().isSpectatorMode()) {
			if (action == PlayerAction.INTERACT) {
				if (player.getState().getSpectatorActivation() != null) {
					player.getState().getSpectatorActivation().getEventData().onInteract(player);
				}
			}
		}

		if (player.getPlayerData() == null) return;

		if (action == PlayerAction.WALK_LEFT) {
			leftDown = true;
			if (!rightDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (action == PlayerAction.WALK_RIGHT) {
			rightDown = true;
			if (!leftDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (action == PlayerAction.JUMP) {
			player.setHoveringAttempt(true);
			if (!onReset) {
				player.jump();
			}
		}
		else if (action == PlayerAction.CROUCH) {
			player.setFastFalling(true);
		}
		else if (action == PlayerAction.INTERACT) {
			player.interact();
		}
		else if (action == PlayerAction.ACTIVE_ITEM) {
			player.activeItem();
		}
		else if (action == PlayerAction.RELOAD) {
			player.reload();
		}
		else if (action == PlayerAction.FIRE) {
			player.startShooting();
		}
		else if (action == PlayerAction.BOOST) {
			player.airblast();
		}
		else if (action == PlayerAction.SWITCH_TO_LAST) {
			player.getPlayerData().switchToLast();
		}
		else if (action == PlayerAction.SWITCH_TO_1) {
			player.getPlayerData().switchWeapon(1);
		}
		else if (action == PlayerAction.SWITCH_TO_2) {
			player.getPlayerData().switchWeapon(2);
		}
		else if (action == PlayerAction.SWITCH_TO_3) {
			player.getPlayerData().switchWeapon(3);
		}
		else if (action == PlayerAction.SWITCH_TO_4) {
			player.getPlayerData().switchWeapon(4);
		}
		else if (action == PlayerAction.WEAPON_CYCLE_UP) {
			player.getPlayerData().switchUp();
		}
		else if (action == PlayerAction.WEAPON_CYCLE_DOWN) {
			player.getPlayerData().switchDown();
		}
		else if (action == PlayerAction.PING) {
			player.ping();
		}
	}

	private float lastTimestamp;
	private final Vector2 relativeMouse = new Vector2();
	private HashSet<PlayerAction> keysHeld = new HashSet<>();
	/**
	 * This is run when receiving client inputs. Set client keys as pressed/released and set attack angle
	 */
	public void syncClientKeyStrokes(float mouseX, float mouseY, Vector2 pos,
		PlayerAction[] actions, float timestamp) {

		//we want to ignore snapshots sent out of order in favor of the most recent snapshot
		if (timestamp > lastTimestamp) {
			lastTimestamp = timestamp;

			relativeMouse.set(mouseX, mouseY).mulAdd(pos, -PPM).add(player.getPixelPosition());

			//we want the relative mouse location to get the angle the client is shooting at
			//this makes high-lag mess with the aim less
			if (player.getMouse() != null) {
				player.getMouse().setDesiredLocation(relativeMouse.x, relativeMouse.y);
			}

			HashSet<PlayerAction> keysHeldNew = new HashSet<>(Arrays.asList(actions));

			for (PlayerAction a : keysHeldNew) {
				if (!keysHeld.contains(a)) {
					keyDown(a);
				}
			}
			for (PlayerAction a : keysHeld) {
				if (!keysHeldNew.contains(a)) {
					keyUp(a);
				}
			}
			keysHeld = keysHeldNew;
		}
	}
	
	public void keyDown(PlayerAction action) { keyDown(action, false); }

	public void keyUp(PlayerAction action) { keyUp(action, false); }
}
