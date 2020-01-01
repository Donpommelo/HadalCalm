package com.mygdx.hadal.input;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The ClientController controls the player using events to process various player actions.
 * The Client Controller is used by the client in a clientstate to map their keystrokes to packets sent to the server
 * @author Zachary Tu
 *
 */
public class ClientController implements InputProcessor {
	
	private PlayState state;
	
	public ClientController(PlayState state) {
		this.state = state;
	}
	
	@Override
	public boolean keyDown(int keycode) {

		if (!HadalGame.client.client.isConnected()) return false;
		
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WALK_LEFT));
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.WALK_RIGHT));
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.JUMP));
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.CROUCH));
		}
		
		if (keycode == PlayerAction.INTERACT.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.INTERACT));
			if (state.getPlayStateStage() != null) {
				state.getPlayStateStage().nextDialogue();
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
			state.getPlayStateStage().nextDialogue();
		}
		
		if (keycode == PlayerAction.PAUSE.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.PAUSE));
		}
		
		if (keycode == PlayerAction.MO_CYCLE_UP.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.MO_CYCLE_UP));
		}
		
		if (keycode == PlayerAction.MO_CYCLE_DOWN.getKey()) {
			HadalGame.client.client.sendTCP(new Packets.KeyDown(PlayerAction.MO_CYCLE_DOWN));
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
