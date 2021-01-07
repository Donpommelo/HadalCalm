package com.mygdx.hadal.input;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The ClientController controls the player using events to process various player actions.
 * The ClientController is used by the client in a clientstate to map their keystrokes to packets sent to the server
 * @author Flitcheroo Fenella
 */
public class ClientController implements InputProcessor {
	
	private final Player player;
	private final PlayState state;
	
	//Is the player currently holding move left/right? This is used for processing holding both buttons -> releasing one. 
	private boolean leftDown;
	private boolean rightDown;
		
	public ClientController(Player player, PlayState state) {
		this.player = player;
		this.state = state;
		syncController();
	}
	
	//note that moving, jumping, crouching, airblast are processed on client end for prediction purposes
	@Override
	public boolean keyDown(int keycode) {
		if (player == null) { return false; }
		if (player.getPlayerData() == null) return false;
		if (!HadalGame.client.getClient().isConnected()) { return false; }
		
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.WALK_LEFT));
			
			leftDown = true;
			if (!rightDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		} 
		
		else if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.WALK_RIGHT));
			
			rightDown = true;
			if (!leftDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		} 
		
		else if (keycode == PlayerAction.JUMP.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.JUMP));
			player.setHoveringAttempt(true);
			player.jump();
		} 
		
		else if (keycode == PlayerAction.CROUCH.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.CROUCH));
			player.setFastFalling(true);
		} 
		
		else if (keycode == PlayerAction.INTERACT.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.INTERACT));
		} 
		
		else if (keycode == PlayerAction.ACTIVE_ITEM.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.ACTIVE_ITEM));
		} 
		
		else if (keycode == PlayerAction.RELOAD.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.RELOAD));
		} 
		
		else if (keycode == PlayerAction.FIRE.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.FIRE));
		} 
		
		else if (keycode == PlayerAction.BOOST.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.BOOST));
			player.airblast();
		} 
		
		else if (keycode == PlayerAction.SWITCH_TO_LAST.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.SWITCH_TO_LAST));
		} 
		
		else if (keycode == PlayerAction.SWITCH_TO_1.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.SWITCH_TO_1));
		} 
		
		else if (keycode == PlayerAction.SWITCH_TO_2.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.SWITCH_TO_2));
		} 
		
		else if (keycode == PlayerAction.SWITCH_TO_3.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.SWITCH_TO_3));
		} 
		
		else if (keycode == PlayerAction.SWITCH_TO_4.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.SWITCH_TO_4));
		} 

		else if (keycode == PlayerAction.WEAPON_CYCLE_UP.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.WEAPON_CYCLE_UP));
		} 
		
		else if (keycode == PlayerAction.WEAPON_CYCLE_DOWN.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.WEAPON_CYCLE_DOWN));
		} 
		
		else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			state.getChatWheel().setVisibility(true);
		}
		
		else if (keycode == PlayerAction.PING.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyDown(PlayerAction.PING));
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (player == null) { return false; }
		if (player.getPlayerData() == null) return false;
		if (!HadalGame.client.getClient().isConnected()) { return false; }

		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyUp(PlayerAction.WALK_LEFT));
			
			leftDown = false;
			if (rightDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		} 
		
		else if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyUp(PlayerAction.WALK_RIGHT));
			
			rightDown = false;
			if (leftDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		} 
		
		else if (keycode == PlayerAction.JUMP.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyUp(PlayerAction.JUMP));
			
			player.setHoveringAttempt(false);
		} 
		
		else if (keycode == PlayerAction.CROUCH.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyUp(PlayerAction.CROUCH));
			
			player.setFastFalling(false);
		} 
		
		else if (keycode == PlayerAction.FIRE.getKey()) {
			HadalGame.client.sendUDP(new Packets.KeyUp(PlayerAction.FIRE));
		} 
		
		else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			state.getChatWheel().setVisibility(false);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) { return false; }

	//we have touchdown call keydown (and touchup calling keyup) if any actions are bound to the mouse
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		keyDown(button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		keyUp(button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) { return false; }

	//This is just a janky way of implementing setting mouse wheel as a hotkey.
	@Override
	public boolean scrolled(float amountX, float amountY) {
		keyDown((int) amountY * 1000);
		keyUp((int) -amountY * 1000);
		return false;
	}

	/**
	 * This resets all toggled controls to prevent stuff like locking a button after unpausing and such
	 */
	public void syncController() {

		//Let game account for all buttons held down before the processor is created.
		for (PlayerAction a: PlayerAction.values()) {
			if (a.isToggleable()) {
				if (a.isPressed()) {
					keyDown(a.getKey());
				} else {
					keyUp(a.getKey());
				}
			}
		}
	}
	
	/**
	 * registers all keys up. This is called when the msg window is enabled.
	 */
	public void resetController() {
		
		//Let game account for all buttons held down before the processor is created.
		for (PlayerAction a: PlayerAction.values()) {
			if (a.isToggleable()) {
				keyUp(a.getKey());
			}
		}
	}
}
