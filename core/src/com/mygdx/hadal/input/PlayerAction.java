package com.mygdx.hadal.input;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * This enum maps to each possible action the player can perform to an input.
 * @author Zachary Tu
 *
 */
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
	MO_CYCLE_UP(-1000),
	MO_CYCLE_DOWN(1000),
	MESSAGE_WINDOW(Input.Keys.M),
	SCORE_WINDOW(Input.Keys.TAB);
	
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
	
	/**
	 * Reset key to default bindings
	 */
	public static void resetKeys() {
				
		PAUSE.setKey(Input.Keys.ESCAPE);
		WALK_RIGHT.setKey(Input.Keys.D);
		WALK_LEFT.setKey(Input.Keys.A);
		JUMP.setKey(Input.Keys.W);
		CROUCH.setKey(Input.Keys.S);
		FIRE.setKey(Input.Buttons.LEFT);
		BOOST.setKey(Input.Buttons.RIGHT);
		INTERACT.setKey(Input.Keys.E);
		FREEZE.setKey(Input.Keys.SPACE);
		RELOAD.setKey(Input.Keys.R);
		DIALOGUE.setKey(Input.Keys.Z);
		SWITCH_TO_LAST.setKey(Input.Keys.Q);
		SWITCH_TO_1.setKey(Input.Keys.NUM_1);
		SWITCH_TO_2.setKey(Input.Keys.NUM_2);
		SWITCH_TO_3.setKey(Input.Keys.NUM_3);
		SWITCH_TO_4.setKey(Input.Keys.NUM_4);
		SWITCH_TO_5.setKey(Input.Keys.NUM_5);
		MO_CYCLE_UP.setKey(-1000);
		MO_CYCLE_DOWN.setKey(1000);
		MESSAGE_WINDOW.setKey(Input.Keys.M);
		SCORE_WINDOW.setKey(Input.Keys.TAB);
	}
	
	/**
	 * Retrieve saved bindings from save file.
	 * TODO: If fail to read file, reset to default?
	 */
	public static void retrieveKeys() {
		JsonValue base;
		base = GameStateManager.reader.parse(Gdx.files.internal("save/Keybind.json"));
		
		for (JsonValue d : base) {
			PlayerAction.valueOf(d.name()).setKey(d.getInt("value"));
		}
	}
	
	/**
	 * Save edited keybinds to file
	 */
	public static void saveKeys() {		
		Gdx.files.local("save/Keybind.json").writeString("", false);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		for (PlayerAction a : PlayerAction.values()) {
			map.put(a.name(), a.getKey());
		}
		
		Gdx.files.local("save/Keybind.json").writeString(GameStateManager.json.toJson(map), true);
	}
}
