package com.mygdx.hadal.input;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The ClientController controls the player using events to process various player actions.
 * The Client Controller is used by the client in a clientstate to map their keystrokes to packets sent to the server
 * @author Zachary Tu
 *
 */
public class ClientController implements InputProcessor {
	
	private Player player;
	private PlayState state;
	
	//Is the player currently holding move left/right? This is used for processing holding both buttons -> releasing one. 
	private boolean leftDown = false;
	private boolean rightDown = false;
		
	public ClientController(Player player, PlayState state) {
		this.player = player;
		this.state = state;
	}
	
	@Override
	public boolean keyDown(int keycode) {

		if (!HadalGame.client.client.isConnected()) return false;
		
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WALK_LEFT));
			
			leftDown = true;
			if (!rightDown) {
				player.setMoveState(MoveState.MOVE_LEFT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WALK_RIGHT));
			
			rightDown = true;
			if (!leftDown) {
				player.setMoveState(MoveState.MOVE_RIGHT);
			} else {
				player.setMoveState(MoveState.STAND);
			}
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.JUMP));
			
			player.setHoveringAttempt(true);
			player.jump();
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.CROUCH));
			
			player.setFastFalling(true);
		}
		
		if (keycode == PlayerAction.INTERACT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.INTERACT));
			if (state.getDialogBox() != null) {
				state.getDialogBox().nextDialogue();
			}
		}
		
		if (keycode == PlayerAction.FREEZE.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.FREEZE));
		}
		
		if (keycode == PlayerAction.RELOAD.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.RELOAD));
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.FIRE));
		}
		
		if (keycode == PlayerAction.BOOST.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.BOOST));
		}
		
		if (keycode == PlayerAction.SWITCH_TO_LAST.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.SWITCH_TO_LAST));
		}
		
		if (keycode == PlayerAction.SWITCH_TO_1.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.SWITCH_TO_1));
		}
		
		if (keycode == PlayerAction.SWITCH_TO_2.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.SWITCH_TO_2));
		}
		
		if (keycode == PlayerAction.SWITCH_TO_3.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.SWITCH_TO_3));
		}
		
		if (keycode == PlayerAction.SWITCH_TO_4.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.SWITCH_TO_4));
		}

		if (keycode == PlayerAction.DIALOGUE.getKey()) {
			if (state.getDialogBox() != null) {
				state.getDialogBox().nextDialogue();
			}
		}
		
		if (keycode == PlayerAction.WEAPON_CYCLE_UP.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WEAPON_CYCLE_UP));
		}
		
		if (keycode == PlayerAction.WEAPON_CYCLE_DOWN.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WEAPON_CYCLE_DOWN));
		}
		
		if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
			state.getMessageWindow().toggleWindow();
		}
		
		if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			state.getScoreWindow().setVisibility(true);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		if (!HadalGame.client.client.isConnected()) return false;

		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.WALK_LEFT));
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.WALK_RIGHT));
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.JUMP));
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.CROUCH));
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.FIRE));
		}
		
		if (keycode == PlayerAction.PAUSE.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyUp(PlayerAction.PAUSE));
		}
		
		if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			state.getScoreWindow().setVisibility(false);
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
}
