package com.mygdx.hadal.input;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

import java.util.HashSet;

/**
 * The ClientController controls the player using events to process various player actions.
 * The ClientController is used by the client in a clientstate to map their keystrokes to packets sent to the server
 * @author Flitcheroo Fenella
 */
public class ClientController implements InputProcessor {
	
	private final Player player;
	private final PlayState state;

	private final HashSet<PlayerAction> buttonsHeld = new HashSet<>();
	private final HashSet<PlayerAction> buttonsJustPressed = new HashSet<>();
	private final HashSet<PlayerAction> buttonsJustReleased = new HashSet<>();

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

		//we return false here b/c if the player is a spectator, we want input to be deferred to common controller
		if (player == null) { return false; }
		if (player.getPlayerData() == null) return false;
		if (!HadalGame.client.getClient().isConnected()) { return false; }

		PlayerAction action = PlayerAction.hotkeyToAction(keycode);
		if (action != null) {
			if (action.isSynced()) {
				buttonsHeld.add(action);
				buttonsJustPressed.add(action);
			}
		}
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			leftDown = true;
			if (!rightDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			rightDown = true;
			if (!leftDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (keycode == PlayerAction.JUMP.getKey()) {
			player.setHoveringAttempt(true);
			player.jump();
		}
		else if (keycode == PlayerAction.CROUCH.getKey()) {
			player.setFastFalling(true);
		}
		else if (keycode == PlayerAction.BOOST.getKey()) {
			player.airblast();
		}
		else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			state.getChatWheel().setVisibility(true);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		//we return false here b/c if the player is a spectator, we want input to be deferred to common controller
		if (player == null) { return false; }
		if (player.getPlayerData() == null) return false;
		if (!HadalGame.client.getClient().isConnected()) { return false; }

		PlayerAction action = PlayerAction.hotkeyToAction(keycode);
		if (action != null) {
			if (action.isSynced()) {
				if (buttonsJustPressed.contains(action)) {
					buttonsJustReleased.add(action);
				} else {
					buttonsHeld.remove(action);
				}
			}
		}
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			leftDown = false;
			if (rightDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			rightDown = false;
			if (leftDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		else if (keycode == PlayerAction.JUMP.getKey()) {
			player.setHoveringAttempt(false);
		}
		else if (keycode == PlayerAction.CROUCH.getKey()) {
			player.setFastFalling(false);
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
		keyUp((int) amountY * 1000);
		return false;
	}

	/**
	 * This resets all toggled controls to prevent stuff like locking a button after unpausing and such
	 */
	public void syncController() {

		//Let game account for all buttons held down before the processor is created.
		for (PlayerAction a : PlayerAction.values()) {
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
		for (PlayerAction a : PlayerAction.values()) {
			if (a.isToggleable()) {
				keyUp(a.getKey());
			}
		}
	}

	/**
	 * This is run periodically for clients to designate just-released buttons as no longer held
	 */
	public void postKeystrokeSync() {
		buttonsJustPressed.clear();
		for (PlayerAction action : buttonsJustReleased) {
			buttonsHeld.remove(action);
		}
		buttonsJustReleased.clear();
	}

	public HashSet<PlayerAction> getButtonsHeld() { return buttonsHeld; }
}
