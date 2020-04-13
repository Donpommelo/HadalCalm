package com.mygdx.hadal.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.schmucks.bodies.Player;

/**
 * The PlayerController controls the player using key events to process various player actions.
 * The Player Controller is used by the host in a playstate to map their keystrokes to the playercontroller
 * @author Zachary Tu
 *
 */
public class PlayerController implements InputProcessor {

	private Player player;
	
	public PlayerController(Player player) {
		this.player = player;
		
		syncController();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		if (player == null) return true;

		if (player.getController() == null) return true;

		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			player.getController().keyDown(PlayerAction.WALK_LEFT);
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			player.getController().keyDown(PlayerAction.WALK_RIGHT);
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			player.getController().keyDown(PlayerAction.JUMP);
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			player.getController().keyDown(PlayerAction.CROUCH);
		}
		
		if (keycode == PlayerAction.INTERACT.getKey()) {
			player.getController().keyDown(PlayerAction.INTERACT);
		}
		
		if (keycode == PlayerAction.FREEZE.getKey()) {
			player.getController().keyDown(PlayerAction.FREEZE);
		}
		
		if (keycode == PlayerAction.RELOAD.getKey()) {
			player.getController().keyDown(PlayerAction.RELOAD);
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			player.getController().keyDown(PlayerAction.FIRE);
		}
		
		if (keycode == PlayerAction.BOOST.getKey()) {
			player.getController().keyDown(PlayerAction.BOOST);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_LAST.getKey()) {
			player.getController().keyDown(PlayerAction.SWITCH_TO_LAST);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_1.getKey()) {
			player.getController().keyDown(PlayerAction.SWITCH_TO_1);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_2.getKey()) {
			player.getController().keyDown(PlayerAction.SWITCH_TO_2);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_3.getKey()) {
			player.getController().keyDown(PlayerAction.SWITCH_TO_3);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_4.getKey()) {
			player.getController().keyDown(PlayerAction.SWITCH_TO_4);
		}
		
		if (keycode == PlayerAction.DIALOGUE.getKey()) {
			player.getController().keyDown(PlayerAction.DIALOGUE);
		}
		
		if (keycode == PlayerAction.WEAPON_CYCLE_DOWN.getKey()) {
			player.getController().keyDown(PlayerAction.WEAPON_CYCLE_DOWN);
		}
		
		if (keycode == PlayerAction.WEAPON_CYCLE_UP.getKey()) {
			player.getController().keyDown(PlayerAction.WEAPON_CYCLE_UP);
		}
		
		if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
			player.getController().keyDown(PlayerAction.MESSAGE_WINDOW);
		}
		
		if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			player.getController().keyDown(PlayerAction.SCORE_WINDOW);
		}
		
		if (keycode == PlayerAction.EXIT_MENU.getKey()) {
			player.getController().keyDown(PlayerAction.EXIT_MENU);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		if (player == null) return true;

		if (player.getController() == null) return true;
		
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			player.getController().keyUp(PlayerAction.WALK_LEFT);
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			player.getController().keyUp(PlayerAction.WALK_RIGHT);
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			player.getController().keyUp(PlayerAction.JUMP);
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			player.getController().keyUp(PlayerAction.CROUCH);
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			player.getController().keyUp(PlayerAction.FIRE);
		}
		
		if (keycode == PlayerAction.PAUSE.getKey()) {
			player.getController().keyUp(PlayerAction.PAUSE);
		}
		
		if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			player.getController().keyUp(PlayerAction.SCORE_WINDOW);
		}	
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) { return false; }

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

	@Override
	public boolean scrolled(int amount) {
		keyDown(amount * 1000);
		keyUp(-amount * 1000);
		return false;
	}
	
	/**
	 * This resets all toggled controls to prevent stuff like locking a button after unpausing and such
	 */
	public void syncController() {
		if (player == null) return;
		if (player.getPlayerData() == null) return;
		if (player.getController() == null) return;
		
		//Let game account for all buttons held down before the processor is created.
		for (PlayerAction a: PlayerAction.values()) {
			
			if (a.isToggleable()) {
				if (Gdx.input.isKeyPressed(a.getKey())) {
					keyDown(a.getKey());
				} else {
					keyUp(a.getKey());
				}
			}
		}
	}

	public void resetController() {
		if (player == null) return;
		if (player.getPlayerData() == null) return;
		if (player.getController() == null) return;
		
		//Let game account for all buttons held down before the processor is created.
		for (PlayerAction a: PlayerAction.values()) {
			
			if (a.isToggleable()) {
				keyUp(a.getKey());
			}
		}
	}
	
	public Player getPlayer() {	return player; }

	public void setPlayer(Player player) {this.player = player;	}
}
