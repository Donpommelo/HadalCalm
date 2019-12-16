package com.mygdx.hadal.retired;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.GameoverBackdrop;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.GameState;
import com.mygdx.hadal.states.TitleState;

/**
 * The Gameover state appears when you lose.
 * @author Zachary Tu
 *
 */
public class GameoverState extends GameState{

	//Temporary links to other modules for testing.
	private Actor playOption, loadoutOption, exitOption;
		
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public GameoverState(final GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new GameoverBackdrop(HadalGame.assetManager));
				
				playOption = new Text(HadalGame.assetManager, "PLAY AGAIN?", 150, HadalGame.CONFIG_HEIGHT - 300, Color.BLACK);
				loadoutOption = new Text(HadalGame.assetManager, "LOADOUT?", 150, HadalGame.CONFIG_HEIGHT - 340, Color.BLACK);
				exitOption = new Text(HadalGame.assetManager, "TITLE?", 150, HadalGame.CONFIG_HEIGHT - 380, Color.BLACK);
				
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(GameoverState.class);
			        	getGsm().addPlayState(UnlockLevel.valueOf(gsm.getRecord().getLevel()), new Loadout(gsm.getRecord()),
			        			null, TitleState.class);
			        }
			    });
				playOption.setScale(0.5f);
				
				loadoutOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(GameoverState.class);
			        	getGsm().gotoHubState();
			        }
			    });
				loadoutOption.setScale(0.5f);
				
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(GameoverState.class);
			        }
			    });
				exitOption.setScale(0.5f);
				
				addActor(playOption);
				addActor(loadoutOption);
				addActor(exitOption);
			}
		};
		app.newMenu(stage);
	}

	/**
	 * 
	 */
	@Override
	public void update(float delta) {

	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {

	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

}