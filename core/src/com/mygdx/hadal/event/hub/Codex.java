package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * The Codex is a hub event found in the multiplayer hub.
 * Interacting with it gives the player options to change match settings.
 * Settings are saved upon exiting the menu.
 * @author Zachary Tu
 *
 */
public class Codex extends HubEvent {

	private SelectBox<String> timerOptions, livesOptions, loadoutOptions;
	
	private final static String title = "Salvar's Codex";
	
	public Codex(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size, title, "MISC", true, hubTypes.CODEX);
	}
	
	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		Text timer = new Text("MATCH TIME: ", 0, 0, false);
		timer.setScale(0.25f);
		
		Text lives = new Text("LIVES: ", 0, 0, false);
		lives.setScale(0.25f);
		
		Text loadout = new Text("LOADOUT: ", 0, 0, false);
		loadout.setScale(0.25f);
		
		timerOptions = new SelectBox<String>(GameStateManager.getSkin());
		timerOptions.setItems("NO TIMER", "1 MIN", "2 MIN", "3 MIN", "4 MIN", "5 MIN");
		timerOptions.setWidth(100);
		
		timerOptions.setSelectedIndex(state.getGsm().getSetting().getTimer());
		
		livesOptions = new SelectBox<String>(GameStateManager.getSkin());
		livesOptions.setItems("UNLIMITED", "1 LIFE", "2 LIVES", "3 LIVES", "4 LIVES", "5 LIVES");
		livesOptions.setWidth(100);
		
		livesOptions.setSelectedIndex(state.getGsm().getSetting().getLives());
		
		loadoutOptions = new SelectBox<String>(GameStateManager.getSkin());
		loadoutOptions.setItems("DEFAULT", "SELECTED", "RANDOM");
		loadoutOptions.setWidth(100);
		
		loadoutOptions.setSelectedIndex(state.getGsm().getSetting().getLoadoutType());
		
		hub.getTableOptions().add(timer);
		hub.getTableOptions().add(timerOptions).row();
		hub.getTableOptions().add(lives);
		hub.getTableOptions().add(livesOptions).row();
		hub.getTableOptions().add(loadout);
		hub.getTableOptions().add(loadoutOptions).row();
	}
	
	@Override
	public void leave() {
		super.leave();
		if (state.isServer()) {
			state.getGsm().getSetting().setTimer(timerOptions.getSelectedIndex());
			state.getGsm().getSetting().setLives(livesOptions.getSelectedIndex());
			state.getGsm().getSetting().setLoadoutType(loadoutOptions.getSelectedIndex());
			state.getGsm().getSetting().saveSetting();
		}
	}
}
