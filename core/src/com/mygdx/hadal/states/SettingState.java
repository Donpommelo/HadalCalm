package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The Control State allows the player to change their key bindings.
 * @author Zachary Tu
 *
 */
public class SettingState extends GameState {
	
	//These are all of the display and buttons visible to the player.
	private Text displayOption, controlOption, exitOption, saveOption, resetOption;
	
	//This scrollpane holds the options for key bindings
	private ScrollPane keybinds;
	
	//This is the option that the player has selected to change
	private PlayerAction currentlyEditing;
	
	private PlayState ps;
	
	//This determines whether the pause state should be removed or not next engine tick.
	private boolean toRemove = false;
	
	//This table contains the ui elements of the pause screen
	private Table options, details;
	
	private SelectBox<String> resolutionOptions, framerateOptions;
	private CheckBox fullscreen, vsync;
		
	//Dimentions of the setting menu
	private final static int optionsX = 25;
	private final static int optionsY = 200;
	private final static int optionsWidth = 300;
	private final static int optionsHeight = 400;
	
	private final static int detailsX = 320;
	private final static int detailsY = 100;
	private final static int detailsWidth = 500;
	private final static int detailsHeight = 600;
	
	private final static float optionsScale = 0.5f;
	private final static float detailsScale = 0.3f;
	
	private settingTab currentTab;
	
	public SettingState(GameStateManager gsm, PlayState ps) {
		super(gsm);
		this.ps = ps;
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new MenuWindow(gsm, optionsX, optionsY, optionsWidth, optionsHeight));
				addActor(new MenuWindow(gsm, detailsX, detailsY, detailsWidth, detailsHeight));
				
				options = new Table();
				options.setLayoutEnabled(true);
				options.setPosition(optionsX, optionsY);
				options.setSize(optionsWidth, optionsHeight);
				addActor(options);
				
				details = new Table();
				details.setLayoutEnabled(true);
				details.setPosition(detailsX, detailsY);
				details.setSize(detailsWidth, detailsHeight);
				addActor(details);
				
				displayOption = new Text("DISPLAY", 0, 0, Color.WHITE);
				displayOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	
			        }
					
			    });
				displayOption.setScale(optionsScale);
				
				controlOption = new Text("CONTROLS", 0, 0, Color.WHITE);
				controlOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						controlsSelected();
			        }
					
			    });
				controlOption.setScale(optionsScale);
				
				exitOption = new Text("EXIT?", 0, 0, Color.WHITE);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(SettingState.class);
			        }
					
			    });
				exitOption.setScale(optionsScale);
				
				saveOption = new Text("SAVE?", 0, 0, Color.WHITE);
				saveOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						saveSettings();
			        }
					
			    });
				saveOption.setScale(optionsScale);
				
				resetOption = new Text("RESET?", 0, 0, Color.WHITE);
				resetOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						resetSettings();
			        }
					
			    });
				resetOption.setScale(optionsScale);
				
				options.add(displayOption).row();
				options.add(controlOption).row();
				options.add(saveOption).row();
				options.add(resetOption).row();
				options.add(exitOption).row();
			}
		};
		app.newMenu(stage);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				//If the player is currently editing an action, bind it to the pressed key
				if (currentlyEditing != null) {
					currentlyEditing.setKey(keycode);
					controlsSelected();
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {	return false; }

			@Override
			public boolean keyTyped(char character) { return false; }

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return keyDown(button); }

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {	return false; }

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

			@Override
			public boolean mouseMoved(int screenX, int screenY) { return false; }

			/**
			 * This is just a janky way of implementing setting mouse wheel as a hotkey.
			 */
			@Override
			public boolean scrolled(int amount) { return keyDown(amount * 1000); }
			
		});
		inputMultiplexer.addProcessor(Gdx.input.getInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		displaySelected();
	}
	
	/**
	 * This is called whenever a bind is changed to update the ui.
	 */
	public void displaySelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.DISPLAY;
		
		Text screen = new Text("RESOLUTION: ", 0, 0);
		screen.setScale(detailsScale);
		
		resolutionOptions = new SelectBox<String>(getGsm().getSkin());
		resolutionOptions.setItems("1024 X 576", "1280 X 720", "1600 X 900", "1920 X 1080");
		resolutionOptions.setWidth(100);
		
		resolutionOptions.setSelectedIndex(getGsm().getSetting().getResolution());

		Text framerate = new Text("FRAMERATE: ", 0, 0);
		framerate.setScale(detailsScale);
		
		framerateOptions = new SelectBox<String>(getGsm().getSkin());
		framerateOptions.setItems("30 fps", "60 fps", "90 fps", "120 fps");
		framerateOptions.setWidth(100);
		
		framerateOptions.setSelectedIndex(getGsm().getSetting().getFramerate());
		
		fullscreen = new CheckBox("FULLSCREEN", getGsm().getSkin());
		vsync = new CheckBox("VSYNC", getGsm().getSkin());
		
		fullscreen.setChecked(getGsm().getSetting().isFullscreen());
		vsync.setChecked(getGsm().getSetting().isVSync());

		details.add(screen);
		details.add(resolutionOptions).row();
		details.add(framerate);
		details.add(framerateOptions).row();
		details.add(fullscreen).row();
		details.add(vsync).row();
	}
	
	/**
	 * This is called whenever a bind is changed to update the ui.
	 */
	public void controlsSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.CONTROLS;
		
		VerticalGroup actions = new VerticalGroup().space(10).pad(50);
		actions.addActor(new Text("CONTROLS", 0, 0));
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(a.name() + ":==   " + getKey(a.getKey()) , 0, 0);
			
			actionChoose.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent e, float x, float y) {
					
					//Clicking any option will highlight it and designate it as the next to update.
					((Text)e.getListenerActor()).setText(action.name() + ":==   " + getKey(action.getKey()) + " <--");					
					currentlyEditing = action;
				}
			});
			
			actionChoose.setScale(detailsScale);
			actions.addActor(actionChoose);
		}
		
		if (keybinds != null) {
			keybinds.remove();
		}
		
		keybinds = new ScrollPane(actions, getGsm().getSkin());
		keybinds.setFadeScrollBars(false);
		keybinds.setSize(detailsWidth, detailsHeight);
		
		details.add(keybinds);
		stage.setScrollFocus(keybinds);
	}
	
	public void saveSettings() {
		switch(currentTab) {
		case CONTROLS:
			PlayerAction.saveKeys();
			controlsSelected();
			break;
		case DISPLAY:
			gsm.getSetting().setResolution(resolutionOptions.getSelectedIndex());
			gsm.getSetting().setFramerate(framerateOptions.getSelectedIndex());
			gsm.getSetting().setFullscreen(fullscreen.isChecked());
			gsm.getSetting().setVsync(vsync.isChecked());
			gsm.getSetting().setDisplay(gsm.getApp());
			gsm.getSetting().saveSetting();
			displaySelected();
			break;
		default:
			break;
		}
	}
	
	public void resetSettings() {
		switch(currentTab) {
		case CONTROLS:
			PlayerAction.resetKeys();
        	controlsSelected();
			break;
		case DISPLAY:
			gsm.getSetting().resetDisplay();
			gsm.getSetting().setDisplay(gsm.getApp());
			gsm.getSetting().saveSetting();
			displaySelected();
			break;
		default:
			break;
		}
	}
	
	/**
	 * This converts a keycode to s readable string
	 * @param keycode: key to read
	 * @return: string to return 
	 */
	public String getKey(int keycode) {
		
		if (keycode == 0) {	return "MOUSE_LEFT"; }
		
		if (keycode == 1) { return "MOUSE_RIGHT"; }		
		
		if (keycode == 2) {	return "MOUSE_MIDDLE"; }
		
		if (keycode == -1000) {	return "M_WHEEL_UP"; }
		
		if (keycode == 1000) { return "M_WHEEL_DOWN"; }
		
		return Input.Keys.toString(keycode);
	}
	
	@Override
	public void update(float delta) {
		//The playstate underneath should have their camera focus and ui act (letting dialog appear + disappear)
		if (ps != null) {
			ps.cameraUpdate();
			ps.stage.act();
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
        	getGsm().removeState(SettingState.class);
        	getGsm().removeState(PauseState.class);
		}
	}

	@Override
	public void render(float delta) {
		//Render the playstate and playstate ui underneath
		if (ps != null) {
			ps.render(delta);
			ps.stage.getViewport().apply();
			ps.stage.draw();
		}
	}
	
	@Override
	public void transitionState() {}
	
	//This is called when the setting state is designated to be removed. (if another player unpauses)
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }
		
	@Override
	public void dispose() { stage.dispose(); }
	
	public PlayState getPs() { return ps; }
	
	public enum settingTab {
		DISPLAY,
		CONTROLS,
		GAMEPLAY,
		MULTIPLAYER,
		AUDIO,
	}
}
