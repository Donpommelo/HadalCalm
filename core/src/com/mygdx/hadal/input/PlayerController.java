package com.mygdx.hadal.input;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class PlayerController implements InputProcessor {

	private Player player;
	private PlayState state;
	
	private boolean leftDown = false;
	private boolean rightDown = false;
	
	public PlayerController(Player player, PlayState state) {
		this.player = player;
		this.state = state;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		if (player == null) return true;
		
		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			leftDown = true;
			if (!rightDown) {
				player.moveState = MoveStates.MOVE_LEFT;
			} else {
				player.moveState = MoveStates.STAND;
			}
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			rightDown = true;
			if (!leftDown) {
				player.moveState = MoveStates.MOVE_RIGHT;
			} else {
				player.moveState = MoveStates.STAND;
			}
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			player.hovering = true;
			player.jump();
		}
		
		if (keycode == PlayerAction.CROUCH.getKey()) {
			player.fastFall();
		}
		
		if (keycode == PlayerAction.INTERACT.getKey()) {
			player.interact();
		}
		
		if (keycode == PlayerAction.FREEZE.getKey()) {
			player.momentum();
		}
		
		if (keycode == PlayerAction.RELOAD.getKey()) {
			player.reload();
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			player.shooting = true;
		}
		
		if (keycode == PlayerAction.BOOST.getKey()) {
			player.airblast();
		}
		
		if (keycode == PlayerAction.SWITCH_TO_LAST.getKey()) {
			player.switchToLast();
		}
		
		if (keycode == PlayerAction.SWITCH_TO_1.getKey()) {
			player.switchToSlot(1);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_2.getKey()) {
			player.switchToSlot(2);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_3.getKey()) {
			player.switchToSlot(3);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_4.getKey()) {
			player.switchToSlot(4);
		}
		
		if (keycode == PlayerAction.SWITCH_TO_5.getKey()) {
			player.switchToSlot(4);
		}
		
		if (keycode == PlayerAction.DIALOGUE.getKey()) {
			state.stage.nextDialogue();
		}
		
		if (keycode == PlayerAction.PAUSE.getKey()) {
			state.getGsm().addState(State.MENU, PlayState.class);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		if (player == null) return true;

		if (keycode == PlayerAction.WALK_LEFT.getKey()) {
			leftDown = false;
			if (rightDown) {
				player.moveState = MoveStates.MOVE_RIGHT;
			} else {
				player.moveState = MoveStates.STAND;
			}
		}
		
		if (keycode == PlayerAction.WALK_RIGHT.getKey()) {
			rightDown = false;
			if (leftDown) {
				player.moveState = MoveStates.MOVE_LEFT;
			} else {
				player.moveState = MoveStates.STAND;
			}
		}
		
		if (keycode == PlayerAction.JUMP.getKey()) {
			player.hovering = false;
		}
		
		if (keycode == PlayerAction.FIRE.getKey()) {
			player.shooting = false;
			player.release();
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

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
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
