package com.mygdx.hadal.input;

import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.event.modes.ArcadeMarquis;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.entities.Player;

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
			player.getJumpHelper().setHoveringAttempt(false);
		}
		else if (action == PlayerAction.CROUCH) {
			player.getFastfallHelper().setFastFalling(false);
		}
		else if (action == PlayerAction.FIRE) {
			if (!onReset) {
				player.getShootHelper().release();
			}
			player.getShootHelper().setShooting(false);
		}

	}
	
	public void keyDown(PlayerAction action, boolean onReset) {
		if (player == null) return;
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
			player.getJumpHelper().setHoveringAttempt(true);
			if (!onReset) {
				player.getJumpHelper().jumpAttempt();
			}
		}
		else if (action == PlayerAction.CROUCH) {
			player.getFastfallHelper().setFastFalling(true);
		}
		else if (action == PlayerAction.INTERACT) {
			player.getEventHelper().interact();
		}
		else if (action == PlayerAction.ACTIVE_ITEM) {
			player.activeItem();
		}
		else if (action == PlayerAction.RELOAD) {
			player.reload();
		}
		else if (action == PlayerAction.FIRE) {
			player.getShootHelper().startShooting();
		}
		else if (action == PlayerAction.BOOST) {
			player.getAirblastHelper().airblast();
		}
		else if (action == PlayerAction.SWITCH_TO_LAST) {
			player.getEquipHelper().switchToLast();
		}
		else if (action == PlayerAction.SWITCH_TO_1) {
			player.getEquipHelper().switchWeapon(1);
		}
		else if (action == PlayerAction.SWITCH_TO_2) {
			player.getEquipHelper().switchWeapon(2);
		}
		else if (action == PlayerAction.SWITCH_TO_3) {
			player.getEquipHelper().switchWeapon(3);
		}
		else if (action == PlayerAction.SWITCH_TO_4) {
			player.getEquipHelper().switchWeapon(4);
		}
		else if (action == PlayerAction.WEAPON_CYCLE_UP) {
			player.getEquipHelper().switchUp();
		}
		else if (action == PlayerAction.WEAPON_CYCLE_DOWN) {
			player.getEquipHelper().switchDown();
		}
		else if (action == PlayerAction.PING) {
			player.getPingHelper().ping();
		}

		if (player.getState().getMode().equals(GameMode.ARCADE)) {
			if (action == PlayerAction.SWITCH_TO_1) {
				ArcadeMarquis.playerVote(player.getState(), player.getUser(), 0);
			} else if (action == PlayerAction.SWITCH_TO_2) {
				ArcadeMarquis.playerVote(player.getState(), player.getUser(), 1);
			} else if (action == PlayerAction.SWITCH_TO_3) {
				ArcadeMarquis.playerVote(player.getState(), player.getUser(), 2);
			} else if (action == PlayerAction.SWITCH_TO_4) {
				ArcadeMarquis.playerVote(player.getState(), player.getUser(), 3);
			}
		}
	}

	public void keyDown(PlayerAction action) { keyDown(action, false); }

	public void keyUp(PlayerAction action) { keyUp(action, false); }
}
