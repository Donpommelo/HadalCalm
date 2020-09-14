package com.mygdx.hadal.input;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.SettingState;

/**
 * This enum maps to each possible action the player can perform to an input.
 * @author Zachary Tu
 */
public enum PlayerAction {
	WALK_RIGHT(Input.Keys.D, true),
	WALK_LEFT(Input.Keys.A, true),
	JUMP(Input.Keys.W, true),
	CROUCH(Input.Keys.S, true),
	FIRE(Input.Buttons.LEFT, true),
	BOOST(Input.Buttons.RIGHT, false),
	INTERACT(Input.Keys.E, false),
	ACTIVE_ITEM(Input.Keys.SPACE, false),
	RELOAD(Input.Keys.R, false),
	DIALOGUE(Input.Keys.Z, false),
	SWITCH_TO_LAST(Input.Keys.Q, false),
	SWITCH_TO_1(Input.Keys.NUM_1, false),
	SWITCH_TO_2(Input.Keys.NUM_2, false),
	SWITCH_TO_3(Input.Keys.NUM_3, false),
	SWITCH_TO_4(Input.Keys.NUM_4, false),
	WEAPON_CYCLE_UP(-1000, false),
	WEAPON_CYCLE_DOWN(1000, false),
	MESSAGE_WINDOW(Input.Keys.T, false),
	SCORE_WINDOW(Input.Keys.TAB, true),
	CHAT_WHEEL(Input.Keys.Y, true),
	PAUSE(Input.Keys.P, false),
	EXIT_MENU(Input.Keys.ESCAPE, false);

	//this is the code for the key this action is bound to
	private int key;
	
	//this boolean notes actions that are "toggleable"
	//these are relevant for resetting when the input processor is temporarily disabled.
	private boolean toggleable;
	
	PlayerAction(int key, boolean toggleable) {
		this.key = key;
		this.toggleable = toggleable;
	}
	
	public int getKey() { return key; }
	
	public void setKey(int key) { this.key = key; }
	
	//this returns the text representing the button bound to this action. Used when text refers to a hotkey
	public String getKeyText() { return SettingState.getKey(key); }
	
	public boolean isToggleable() { return toggleable; }
	
	/**
	 * Reset key to default bindings
	 */
	public static void resetKeys() {
				
		WALK_RIGHT.setKey(Input.Keys.D);
		WALK_LEFT.setKey(Input.Keys.A);
		JUMP.setKey(Input.Keys.W);
		CROUCH.setKey(Input.Keys.S);
		FIRE.setKey(Input.Buttons.LEFT);
		BOOST.setKey(Input.Buttons.RIGHT);
		INTERACT.setKey(Input.Keys.E);
		ACTIVE_ITEM.setKey(Input.Keys.SPACE);
		RELOAD.setKey(Input.Keys.R);
		DIALOGUE.setKey(Input.Keys.Z);
		SWITCH_TO_LAST.setKey(Input.Keys.Q);
		SWITCH_TO_1.setKey(Input.Keys.NUM_1);
		SWITCH_TO_2.setKey(Input.Keys.NUM_2);
		SWITCH_TO_3.setKey(Input.Keys.NUM_3);
		SWITCH_TO_4.setKey(Input.Keys.NUM_4);
		WEAPON_CYCLE_UP.setKey(-1000);
		WEAPON_CYCLE_DOWN.setKey(1000);
		MESSAGE_WINDOW.setKey(Input.Keys.T);
		SCORE_WINDOW.setKey(Input.Keys.TAB);
		CHAT_WHEEL.setKey(Input.Keys.Y);
		PAUSE.setKey(Input.Keys.P);
		EXIT_MENU.setKey(Input.Keys.ESCAPE);
	}
	
	/**
	 * Retrieve saved bindings from save file.
	 */
	public static void retrieveKeys() {
		
		if (!Gdx.files.internal("save/Keybind.json").exists()) {
			resetKeys();
			saveKeys();
		}
		
		for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Keybind.json"))) {
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
			map.put(a.toString(), a.getKey());
		}
		
		Gdx.files.local("save/Keybind.json").writeString(GameStateManager.json.toJson(map), true);
	}
}
