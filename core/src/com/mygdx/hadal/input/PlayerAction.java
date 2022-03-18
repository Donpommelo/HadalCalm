package com.mygdx.hadal.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.text.HText;

/**
 * This enum maps to each possible action the player can perform to an input.
 * @author Gnaddam Ghermicelli
 */
public enum PlayerAction {
	WALK_RIGHT(Input.Keys.D, true, HText.WALK_RIGHT),
	WALK_LEFT(Input.Keys.A, true, HText.WALK_LEFT),
	JUMP(Input.Keys.W, true, HText.JUMP),
	CROUCH(Input.Keys.S, true, HText.FASTFALL),
	FIRE(Input.Buttons.LEFT, true, HText.SHOOT),
	BOOST(Input.Buttons.RIGHT, false, HText.BOOST),
	INTERACT(Input.Keys.E, false, HText.INTERACT),
	ACTIVE_ITEM(Input.Keys.SPACE, false, HText.MAGIC),
	RELOAD(Input.Keys.R, false, HText.RELOAD),
	DIALOGUE(Input.Keys.Z, false, HText.DIALOG),
	SWITCH_TO_LAST(Input.Keys.Q, false, HText.SWITCH_TO_LAST),
	SWITCH_TO_1(Input.Keys.NUM_1, false, HText.SWITCH_TO_1),
	SWITCH_TO_2(Input.Keys.NUM_2, false, HText.SWITCH_TO_2),
	SWITCH_TO_3(Input.Keys.NUM_3, false, HText.SWITCH_TO_3),
	SWITCH_TO_4(Input.Keys.NUM_4, false, HText.SWITCH_TO_4),
	WEAPON_CYCLE_UP(-1000, false, HText.WEAPON_CYCLE_UP),
	WEAPON_CYCLE_DOWN(1000, false, HText.WEAPON_CYCLE_DOWN),
	MESSAGE_WINDOW(Input.Keys.T, false, HText.CHAT),
	SCORE_WINDOW(Input.Keys.TAB, true, HText.SCORE_WINDOW),
	CHAT_WHEEL(Input.Keys.C, true, HText.CHAT_WHEEL),
	PING(Input.Keys.X, false, HText.PING),
	PAUSE(Input.Keys.P, false, HText.PAUSE),
	EXIT_MENU(Input.Keys.ESCAPE, false, HText.EXIT);

	//this is the code for the key this action is bound to
	private int key;
	
	//this boolean notes actions that are "toggleable"
	//these are relevant for resetting when the input processor is temporarily disabled.
	private final boolean toggleable;

	private final HText text;

	private final static ObjectMap<Integer, PlayerAction> hotkeys = new ObjectMap<>();

	PlayerAction(int key, boolean toggleable, HText text) {
		this.key = key;
		this.toggleable = toggleable;
		this.text = text;
	}
	
	public int getKey() { return key; }
	
	public void setKey(int key) {
		this.key = key;
		hotkeys.put(key, this);
	}
	
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
			for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Keybind.json"))) {
				PlayerAction.valueOf(d.name()).setKey(d.getInt("value"));
			}
		} catch (SerializationException e) {
			resetKeys();
			saveKeys();
			for (JsonValue d : GameStateManager.reader.parse(Gdx.files.internal("save/Keybind.json"))) {
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
		
		Gdx.files.local("save/Keybind.json").writeString(GameStateManager.json.toJson(map), true);
	}

	public static PlayerAction hotkeyToAction(int key) {
		return hotkeys.get(key);
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
