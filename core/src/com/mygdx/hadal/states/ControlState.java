package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;

public class ControlState extends GameState {
	private Stage stage;
	
	private Actor exitOption;
	
	private ScrollPane options;
	
	private PlayerAction currentlyEditing;
	
	public ControlState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 100, HadalGame.CONFIG_HEIGHT - 260);
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(ControlState.class);
			        }
			    });
				exitOption.setScale(0.5f);
				
				
				addActor(exitOption);
			}
		};
		app.newMenu(stage);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				if (currentlyEditing != null) {
					currentlyEditing.setKey(keycode);
					refreshBinds();
					currentlyEditing = null;
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return keyDown(button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
			
		});
		inputMultiplexer.addProcessor(Gdx.input.getInputProcessor());

		Gdx.input.setInputProcessor(inputMultiplexer);
		
		refreshBinds();

	}
	
	public void refreshBinds() {
		VerticalGroup actions = new VerticalGroup();
		actions.addActor(new Text(HadalGame.assetManager, "CONTROLS", 0, 0));
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(HadalGame.assetManager, a.name() + ":== " + getKey(a.getKey()) , 0, 0);
			
			actionChoose.addListener(new ClickListener() {
				public void clicked(InputEvent e, float x, float y) {
					currentlyEditing = action;
				}
			});
			
			actions.addActor(actionChoose);
		}
		
		options = new ScrollPane(actions, getGsm().getSkin());
		options.setPosition(200, 0);
		options.setSize(1500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
	}
	
	public String getKey(int keycode) {
		
		if (keycode == 0) {
			return "MOUSE_LEFT";
		}
		
		if (keycode == 1) {
			return "MOUSE_RIGHT";
		}
		
		if (keycode == 2) {
			return "MOUSE_MIDDLE";
		}
		
		
		return Input.Keys.toString(keycode);
	}
	
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();		
	}

}
