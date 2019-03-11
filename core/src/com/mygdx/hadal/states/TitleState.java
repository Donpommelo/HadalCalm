package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TitleBackdrop;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.server.KryoServer;

/**
 * The TitleState is created upon initializing the game and will display an image and allow the player to play or exit.
 * TODO: Eventually, this might be where we initialize game data + assets + player change settings.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Actor hostOption, joinOption, exitOption, controlOption;
		
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
				
				hostOption = new Text(HadalGame.assetManager, "HOST", x, 340, Color.BLACK);
				joinOption = new Text(HadalGame.assetManager, "JOIN", x, 300, Color.BLACK);
				controlOption = new Text(HadalGame.assetManager, "CONTROLS", x, 260, Color.BLACK);
				exitOption = new Text(HadalGame.assetManager, "EXIT?", x, 220, Color.BLACK);
				
				hostOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addState(State.HUB, TitleState.class);
			        	HadalGame.server = new KryoServer(getGsm());
			        }
			    });
				hostOption.setScale(0.5f);			

				joinOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	HadalGame.client.init(false);
			        	try {
                            Thread.sleep(600);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
			        	getGsm().addState(State.CLIENTPLAY, TitleState.class);
			        }
			    });
				joinOption.setScale(0.5f);	
				
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
				
				addActor(hostOption);
				addActor(joinOption);
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
