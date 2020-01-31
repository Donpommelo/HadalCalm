package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.states.PlayState;

/**
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
		
		Text timer = new Text("MATCH TIME: ", 0, 0);
		timer.setScale(0.25f);
		
		Text lives = new Text("LIVES: ", 0, 0);
		lives.setScale(0.25f);
		
		Text loadout = new Text("LOADOUT: ", 0, 0);
		loadout.setScale(0.25f);
		
		timerOptions = new SelectBox<String>(state.getGsm().getSkin());
		timerOptions.setItems("NO TIMER", "1 MIN", "2 MIN", "3 MIN", "4 MIN", "5 MIN");
		timerOptions.setWidth(100);
		
		timerOptions.setSelectedIndex(state.getGsm().getRecord().getTimer());
		
		livesOptions = new SelectBox<String>(state.getGsm().getSkin());
		livesOptions.setItems("UNLIMITED", "1 LIFE", "2 LIVES", "3 LIVES", "4 LIVES", "5 LIVES");
		livesOptions.setWidth(100);
		
		livesOptions.setSelectedIndex(state.getGsm().getRecord().getLives());
		
		loadoutOptions = new SelectBox<String>(state.getGsm().getSkin());
		loadoutOptions.setItems("DEFAULT", "SELECTED", "RANDOM");
		loadoutOptions.setWidth(100);
		
		loadoutOptions.setSelectedIndex(state.getGsm().getRecord().getLoadoutType());
		
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
			state.getGsm().getRecord().setTimer(timerOptions.getSelectedIndex());
			state.getGsm().getRecord().setLives(livesOptions.getSelectedIndex());
			state.getGsm().getRecord().setLoadoutType(loadoutOptions.getSelectedIndex());
		}
	}
	
	public static float indexToTimer(int index) {
		switch(index) {
		case 0:
			return 0.0f;
		case 1:
			return 60.0f;
		case 2:
			return 120.0f;
		case 3:
			return 180.0f;
		case 4:
			return 240.0f;
		case 5:
			return 300.0f;
		default:
			return 0.0f;
		}
	}
}
