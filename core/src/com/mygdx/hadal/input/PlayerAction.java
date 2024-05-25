package com.mygdx.hadal.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.managers.JSONManager.JSON;
import static com.mygdx.hadal.managers.JSONManager.READER;

/**
 * This enum maps to each possible action the player can perform to an input.
 * @author Gnaddam Ghermicelli
 */
public enum PlayerAction {
	WALK_RIGHT(Input.Keys.D, true, UIText.WALK_RIGHT),
	WALK_LEFT(Input.Keys.A, true, UIText.WALK_LEFT),
	JUMP(Input.Keys.W, true, UIText.JUMP),
	CROUCH(Input.Keys.S, true, UIText.FASTFALL),
	FIRE(Input.Buttons.LEFT, true, UIText.SHOOT),
	BOOST(Input.Buttons.RIGHT, false, UIText.BOOST),
	INTERACT(Input.Keys.E, false, UIText.INTERACT),
	ACTIVE_ITEM(Input.Keys.SPACE, false, UIText.USE_MAGIC),
	RELOAD(Input.Keys.R, false, UIText.RELOAD),
	DIALOGUE(Input.Keys.Z, false, UIText.DIALOG),
	SWITCH_TO_LAST(Input.Keys.Q, false, UIText.SWITCH_TO_LAST),
	SWITCH_TO_1(Input.Keys.NUM_1, false, UIText.SWITCH_TO_1),
	SWITCH_TO_2(Input.Keys.NUM_2, false,  UIText.SWITCH_TO_2),
	SWITCH_TO_3(Input.Keys.NUM_3, false, UIText.SWITCH_TO_3),
	SWITCH_TO_4(Input.Keys.NUM_4, false, UIText.SWITCH_TO_4),
	WEAPON_CYCLE_UP(-1000, false, UIText.WEAPON_CYCLE_UP),
	WEAPON_CYCLE_DOWN(1000, false, UIText.WEAPON_CYCLE_DOWN),
	MESSAGE_WINDOW(Input.Keys.T, false, UIText.CHAT),
	SCORE_WINDOW(Input.Keys.TAB, true, UIText.SCORE_WINDOW),
	CHAT_WHEEL(Input.Keys.C, true, UIText.CHAT_WHEEL),
	PING(Input.Keys.X, false, UIText.PING),
	PAUSE(Input.Keys.ESCAPE, false, UIText.PAUSE),
	READY_UP(Input.Keys.ENTER, false, UIText.READY_UP);

	//this is the default key bound to this action that will be used when resetting
	private final int defaultKey;

	//this boolean notes actions that are "toggleable"
	//these are relevant for resetting when the input processor is temporarily disabled.
	private final boolean toggleable;

	//this is the text that shows up in the ui to represent this action
	private final UIText text;

	//this is the code for the key this action is bound to
	private int key;

	PlayerAction(int key, boolean toggleable, UIText text) {
		this.defaultKey = key;
		this.key = key;
		this.toggleable = toggleable;
		this.text = text;
	}
	
	public int getKey() { return key; }
	
	public void setKey(int key) {
		this.key = key;
	}
	
	//this returns the text representing the button bound to this action. Used when text refers to a hotkey
	public String getKeyText() { return SettingState.getKey(key); }
	
	public boolean isToggleable() { return toggleable; }

	/**
	 * Reset key to default bindings
	 */
	public static void resetKeys() {
		for (PlayerAction action : PlayerAction.values()) {
			action.setKey(action.defaultKey);
		}
	}
	
	/**
	 * Retrieve saved bindings from save file.
	 * If file is missing or malformed, reset and create new keybind file
	 */
	public static void retrieveKeys() {
		try {
			for (JsonValue d : READER.parse(Gdx.files.local("save/Keybind.json"))) {
				PlayerAction.valueOf(d.name()).setKey(d.getInt("value"));
			}
		} catch (SerializationException | IllegalArgumentException e) {
			resetKeys();
			saveKeys();
			for (JsonValue d : READER.parse(Gdx.files.local("save/Keybind.json"))) {
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
		
		Gdx.files.local("save/Keybind.json").writeString(JSON.toJson(map), true);
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
