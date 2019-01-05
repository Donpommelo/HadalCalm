package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuBackdrop;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The MenuState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class MenuState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Actor resumeOption, exitOption;
		
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public MenuState(final GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new MenuBackdrop(HadalGame.assetManager));
				resumeOption = new Text(HadalGame.assetManager, "RESUME?", HadalGame.CONFIG_WIDTH / 2, 
						HadalGame.CONFIG_HEIGHT - 300, Color.BLACK);
				exitOption = new Text(HadalGame.assetManager, "EXIT?", HadalGame.CONFIG_WIDTH / 2,
						HadalGame.CONFIG_HEIGHT - 340, Color.BLACK);
				
				resumeOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(MenuState.class);
			        }
			    });
				resumeOption.setScale(0.5f);
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(MenuState.class);
			        	getGsm().removeState(PlayState.class);
			        	getGsm().removeState(HubState.class);
			        }
			    });
				exitOption.setScale(0.5f);
				
				addActor(resumeOption);
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
