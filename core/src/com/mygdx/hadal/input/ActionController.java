package com.mygdx.hadal.input;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The Action Controller receives actions from a player controller or packets from clients and uses them to make a player 
 * perform actions.
 * @author Lobideen Lolbatross
 */
public class ActionController {

	//this is the player that this controller control
	private Player player;
	private final PlayState state;
	
	//Is the player currently holding move left/right? This is used for processing holding both buttons -> releasing one. 
	private boolean leftDown = false;
	private boolean rightDown = false;
	
	public ActionController(Player player, PlayState state) {
		this.player = player;
		this.state = state;
	}
	
	/**
	 * onReset is true if this is being called by the controller being synced (from finishing a transition/opening window)
	 * when this happens, we don't want to trigger jumping/shooting, just reset our button-held statuses
	 */
	public void keyUp(PlayerAction action, boolean onReset) {
		if (player == null) { return; }
		if (player.getPlayerData() == null) return;
		
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
		
		else if (action == PlayerAction.PAUSE) {
			if (state.getPlayer().equals(player) || state.getGsm().getSetting().isMultiplayerPause()) {
				if (GameStateManager.currentMode == Mode.SINGLE) {
					state.getGsm().addPauseState(state, player.getName(), PlayState.class, true);
				} else {
					state.getGsm().addPauseState(state, player.getName(), PlayState.class, state.getGsm().getSetting().isMultiplayerPause());
				}
			} else {
				HadalGame.server.sendPacketToPlayer(player, new Packets.Paused(player.getName(), false));
			}
		}
		
		else if (action == PlayerAction.SCORE_WINDOW) {
			state.getScoreWindow().setVisibility(false);
		}
		
		else if (action == PlayerAction.CHAT_WHEEL) {
			state.getChatWheel().setVisibility(false);
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
			player.switchToLast();
		}
		
		else if (action == PlayerAction.SWITCH_TO_1) {
			player.switchToSlot(1);
		}
		
		else if (action == PlayerAction.SWITCH_TO_2) {
			player.switchToSlot(2);
		}
		
		else if (action == PlayerAction.SWITCH_TO_3) {
			player.switchToSlot(3);
		}
		
		else if (action == PlayerAction.SWITCH_TO_4) {
			player.switchToSlot(4);
		}
		
		else if (action == PlayerAction.DIALOGUE) {
			if (state.getDialogBox() != null) {
				state.getDialogBox().nextDialogue();
			}
		}
		
		else if (action == PlayerAction.WEAPON_CYCLE_UP) {
			player.getPlayerData().switchUp();
		}
		
		else if (action == PlayerAction.WEAPON_CYCLE_DOWN) {
			player.getPlayerData().switchDown();
		}
		
		else if (action == PlayerAction.MESSAGE_WINDOW) {
			state.getMessageWindow().toggleWindow();
		}

		else if (action == PlayerAction.SCORE_WINDOW) {
			state.getScoreWindow().setVisibility(true);
		}
		
		else if (action == PlayerAction.CHAT_WHEEL) {
			state.getChatWheel().setVisibility(true);
		}
		
		else if (action == PlayerAction.PING) {
			player.ping();
		}
		
		else if (action == PlayerAction.EXIT_MENU) {
			if (state.getUiHub().isActive()) {
				state.getUiHub().leave();
			}
		}
	}
	
	public void keyDown(PlayerAction action) { keyDown(action, false); }

	public void keyUp(PlayerAction action) { keyUp(action, false); }

	public Player getPlayer() {	return player; }

	public void setPlayer(Player player) { this.player = player; }
}
