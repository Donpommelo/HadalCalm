package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TitleBackdrop;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * The TitleState is created upon initializing the game and will display an image and allow the player to play or exit.
 * TODO: Eventually, this might be where we initialize game data + assets + player change settings.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState{

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Actor playOption, exitOption, quickPlayOption;
		
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public TitleState(final GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new TitleBackdrop(HadalGame.assetManager));
				
				playOption = new Text(HadalGame.assetManager, "PLAY?", 150, HadalGame.CONFIG_HEIGHT - 180);
				quickPlayOption = new Text(HadalGame.assetManager, "QUICK PLAY?", 150, HadalGame.CONFIG_HEIGHT - 220);
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 150, HadalGame.CONFIG_HEIGHT - 260);
				
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.addState(State.LOADOUT);
			        }
			    });
				playOption.setScale(0.5f);
				
				quickPlayOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.addState(State.PLAY);
			        }
			    });
				quickPlayOption.setScale(0.5f);
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	Gdx.app.exit();
			        }
			    });
				exitOption.setScale(0.5f);
				
				addActor(playOption);
				addActor(quickPlayOption);
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
