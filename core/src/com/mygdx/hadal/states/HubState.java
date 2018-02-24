package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

public class HubState extends PlayState {

	private Actor exitOption, playOption;
	
	public HubState(GameStateManager gsm, Loadout loadout) {
		super(gsm, loadout, "Maps/test_map.tmx", false);

	}
	
	@Override
	public void show() {
		super.show();
		
		exitOption = new Text(HadalGame.assetManager, "EXIT?", 100, HadalGame.CONFIG_HEIGHT - 260, Color.WHITE);
		exitOption.addListener(new ClickListener() {
	        public void clicked(InputEvent e, float x, float y) {
	        	getGsm().removeState(HubState.class);
	        }
	    });
		exitOption.setScale(0.5f);	
		
		playOption = new Text(HadalGame.assetManager, "PLAY?",  100, HadalGame.CONFIG_HEIGHT - 350, Color.WHITE);
		playOption.addListener(new ClickListener() {
	        public void clicked(InputEvent e, float x, float y) {
	        	getGsm().removeState(HubState.class);
	        	getGsm().addState(State.PLAY, TitleState.class);
	        }
	    });
		playOption.setScale(0.5f);
		
		getStage().addActor(exitOption);				
		getStage().addActor(playOption);
	}

}
