package com.mygdx.hadal.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.text.UIText;

/**
 * This enum maps to each possible action the player can perform to an input.
 * @author Gnaddam Ghermicelli
 */
public enum PlayerAction {
	WALK_RIGHT(Input.Keys.D, true, false, UIText.WALK_RIGHT),
	WALK_LEFT(Input.Keys.A, true, false, UIText.WALK_LEFT),
	JUMP(Input.Keys.W, true, false, UIText.JUMP),
	CROUCH(Input.Keys.S, true, false, UIText.FASTFALL),
	FIRE(Input.Buttons.LEFT, true, false, UIText.SHOOT),
	BOOST(Input.Buttons.RIGHT, false, false, UIText.BOOST),
	INTERACT(Input.Keys.E, false, false, UIText.INTERACT),
	ACTIVE_ITEM(Input.Keys.SPACE, false, true, UIText.USE_MAGIC),
	RELOAD(Input.Keys.R, false, false, UIText.RELOAD),
	DIALOGUE(Input.Keys.Z, false, false, UIText.DIALOG),
	SWITCH_TO_LAST(Input.Keys.Q, false, false, UIText.SWITCH_TO_LAST),
	SWITCH_TO_1(Input.Keys.NUM_1, false, false, UIText.SWITCH_TO_1),
	SWITCH_TO_2(Input.Keys.NUM_2, false, false, UIText.SWITCH_TO_2),
	SWITCH_TO_3(Input.Keys.NUM_3, false, false, UIText.SWITCH_TO_3),
	SWITCH_TO_4(Input.Keys.NUM_4, false, false, UIText.SWITCH_TO_4),
	WEAPON_CYCLE_UP(-1000, false, false, UIText.WEAPON_CYCLE_UP),
	WEAPON_CYCLE_DOWN(1000, false, false, UIText.WEAPON_CYCLE_DOWN),
	MESSAGE_WINDOW(Input.Keys.T, false, false, UIText.CHAT),
	SCORE_WINDOW(Input.Keys.TAB, true, false, UIText.SCORE_WINDOW),
	CHAT_WHEEL(Input.Keys.C, true, false, UIText.CHAT_WHEEL),
	PING(Input.Keys.X, false, false, UIText.PING),
	PAUSE(Input.Keys.P, false, false, UIText.PAUSE),
	EXIT_MENU(Input.Keys.ESCAPE, false, false, UIText.EXIT);

	//this is the code for the key this action is bound to
	private int key;
	
	//this boolean notes actions that are "toggleable"
	//these are relevant for resetting when the input processor is temporarily disabled.
	private final boolean toggleable;

	//Does the client inform the server if they have this button pressed or not?
	private final boolean synced;

	//this is the text that shows up in the ui to represent this action
	private final UIText text;

	PlayerAction(int key, boolean toggleable, boolean synced, UIText text) {
		this.key = key;
		this.toggleable = toggleable;
		this.synced = synced;
		this.text = text;
	}
	
	public int getKey() { return key; }
	
	public void setKey(int key) {
		this.key = key;
	}
	
	//this returns the text representing the button bound to this action. Used when text refers to a hotkey
	public String getKeyText() { return SettingState.getKey(key); }
	
	public boolean isToggleable() { return toggleable; }

	public boolean isSynced() { return synced; }

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
		CHAT_WHEEL.setKey(Input.Keys.C);
		PING.setKey(Input.Keys.X);
		PAUSE.setKey(Input.Keys.P);
		EXIT_MENU.setKey(Input.Keys.ESCAPE);
	}
	
	/**
	 * Retrieve saved bindings from save file.
	 * If file is missing or malformed, reset and create new keybind file
	 */
	public static void retrieveKeys() {
		try {
			for (JsonValue d : GameStateManager.READER.parse(Gdx.files.local("save/Keybind.json"))) {
				PlayerAction.valueOf(d.name()).setKey(d.getInt("value"));
			}
		} catch (SerializationException e) {
			resetKeys();
			saveKeys();
			for (JsonValue d : GameStateManager.READER.parse(Gdx.files.local("save/Keybind.json"))) {
				PlayerAction.valueOf(d.name()).setKey(d.getInt("value"));
			}
		}
	}
	
	/**
	 * Save edited keybinds to file
	 */
	public static void saveKeys() {		
		Gdx.files.local("save/Keybind.json").writeString("", false);

		ObjectMap<String, Integer> map = new ObjectMap<>();
		for (PlayerAction a : PlayerAction.values()) {
			map.put(a.toString(), a.getKey());
		}
		
		Gdx.files.local("save/Keybind.json").writeString(GameStateManager.JSON.toJson(map), true);
	}

	/**
	 * This returns if a key is currently pressed
	 * Whether a key is pressed depends on whether it is a keyboard or mouse button.
	 * This is used when controls are reset
	 */
	public boolean isPressed() {
		if (0 <= key && key <= 2) {
			return Gdx.input.isButtonPressed(key);
		} else if (5 <= key && key <= 255){
			return Gdx.input.isKeyPressed(key);
		}
		return false;
	}

	public String getText() { return text.text(); }
}
