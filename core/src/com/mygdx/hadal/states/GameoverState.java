package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.GameoverBackdrop;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * The Gameover state appears when you lose.
 * @author Zachary Tu
 *
 */
public class GameoverState extends GameState{

	private Stage stage;
	
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
				
				playOption = new Text(HadalGame.assetManager, "PLAY AGAIN?", 150, HadalGame.CONFIG_HEIGHT - 180);
				loadoutOption = new Text(HadalGame.assetManager, "LOADOUT?", 150, HadalGame.CONFIG_HEIGHT - 220);
				exitOption = new Text(HadalGame.assetManager, "TITLE?", 150, HadalGame.CONFIG_HEIGHT - 260);
				
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState();
			        	gsm.addState(State.PLAY);
			        }
			    });
				playOption.setScale(0.5f);
				
				loadoutOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState();
			        	gsm.addState(State.LOADOUT);
			        }
			    });
				loadoutOption.setScale(0.5f);
				
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState();
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
		stage.act();
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		stage.draw();
		batch.end();
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

}
