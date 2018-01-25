package com.mygdx.hadal.input;
import com.badlogic.gdx.Input;

public enum PlayerAction {
	PAUSE(Input.Keys.ESCAPE),
	WALK_RIGHT(Input.Keys.D),
	WALK_LEFT(Input.Keys.A),
	JUMP(Input.Keys.W),
	CROUCH(Input.Keys.S),
	FIRE(Input.Buttons.LEFT),
	BOOST(Input.Buttons.RIGHT),
	INTERACT(Input.Keys.E),
	FREEZE(Input.Keys.SPACE),
	RELOAD(Input.Keys.R),
	DIALOGUE(Input.Keys.Z),
	SWITCH_TO_LAST(Input.Keys.Q),
	SWITCH_TO_1(Input.Keys.NUM_1),
	SWITCH_TO_2(Input.Keys.NUM_2),
	SWITCH_TO_3(Input.Keys.NUM_3),
	SWITCH_TO_4(Input.Keys.NUM_4),
	SWITCH_TO_5(Input.Keys.NUM_5),
	MO_CYCLE_UP(0),
	MO_CYCLE_DOWN(0);
	
	private int key;
	
	PlayerAction(int key) {
		this.key = key;
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	
}
