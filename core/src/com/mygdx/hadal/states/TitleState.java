package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TitleBackdrop;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.save.UnlockEquip;

/**
 * The TitleState is created upon initializing the game and will display an image and allow the player to play or exit.
 * TODO: Eventually, this might be where we initialize game data + assets + player change settings.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Actor playOption, exitOption, quickPlayOption, tutorialOption, controlOption;
		
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
				
				int x = HadalGame.CONFIG_WIDTH - 200;
				
				playOption = new Text(HadalGame.assetManager, "PLAY", x, 340);
				quickPlayOption = new Text(HadalGame.assetManager, "QUICK PLAY", x, 300);
				tutorialOption = new Text(HadalGame.assetManager, "TUTORIAL", x, 260);
				controlOption = new Text(HadalGame.assetManager, "CONTROLS", x, 220);
				exitOption = new Text(HadalGame.assetManager, "EXIT?", x, 180);
				
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addState(State.HUB, TitleState.class);
			        }
			    });
				playOption.setScale(0.5f);
				
				quickPlayOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addState(State.PLAY, TitleState.class);
			        }
			    });
				quickPlayOption.setScale(0.5f);
				
				tutorialOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addPlayState("Maps/tutorial.tmx", new Loadout(UnlockEquip.NOTHING), null, TitleState.class);
			        }
			    });
				tutorialOption.setScale(0.5f);
				
				controlOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addState(State.CONTROL, TitleState.class);
			        }
			    });
				controlOption.setScale(0.5f);
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	Gdx.app.exit();
			        }
			    });
				exitOption.setScale(0.5f);
				
				addActor(playOption);
				addActor(quickPlayOption);
				addActor(tutorialOption);
				addActor(controlOption);
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
