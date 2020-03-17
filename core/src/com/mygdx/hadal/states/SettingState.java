package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The Control State allows the player to change their key bindings.
 * @author Zachary Tu
 *
 */
public class SettingState extends GameState {
	
	//These are all of the display and buttons visible to the player.
	private Text displayOption, controlOption, audioOption, gameOption, miscOption, exitOption, saveOption, resetOption;
	
	//This scrollpane holds the options for key bindings
	private ScrollPane keybinds;
	
	//This is the hotkey option that the player has selected to change
	private PlayerAction currentlyEditing;
	
	private PlayState ps;
	
	//This determines whether the pause state should be removed or not next engine tick.
	private boolean toRemove = false;
	
	//This table contains the ui elements of the pause screen
	private Table options, details;
	
	private SelectBox<String> resolutionOptions, framerateOptions, timerOptions, livesOptions, loadoutOptions, playerCapacity;
	private Slider sound, music, master;
	private CheckBox fullscreen, vsync, randomNameAlliteration, consoleEnabled;
		
	//Dimentions of the setting menu
	private final static int optionsX = 25;
	private final static int optionsY = 100;
	private final static int optionsWidth = 300;
	private final static int optionsHeight = 600;
	
	private final static int detailsX = 320;
	private final static int detailsY = 100;
	private final static int detailsWidth = 500;
	private final static int detailsHeight = 600;
	
	private final static float optionsScale = 0.5f;
	private final static float optionsPad = 10.0f;
	private final static float detailsScale = 0.3f;
	
	//this is the current setting tab the player is using
	private settingTab currentTab;
	
	public SettingState(GameStateManager gsm, PlayState ps) {
		super(gsm);
		this.ps = ps;
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new MenuWindow(optionsX, optionsY, optionsWidth, optionsHeight));
				addActor(new MenuWindow(detailsX, detailsY, detailsWidth, detailsHeight));
				
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
				
				displayOption = new Text("DISPLAY", 0, 0, true);
				displayOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm);
						displaySelected();
			        }
					
			    });
				displayOption.setScale(optionsScale);
				
				controlOption = new Text("CONTROLS", 0, 0, true);
				controlOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm);
						controlsSelected();
			        }
					
			    });
				controlOption.setScale(optionsScale);
				
				audioOption = new Text("AUDIO", 0, 0, true);
				audioOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm);
						audioSelected();
			        }
					
			    });
				audioOption.setScale(optionsScale);
				
				gameOption = new Text("GAME", 0, 0, true);
				gameOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm);
						gameSelected();
			        }
					
			    });
				gameOption.setScale(optionsScale);
				
				miscOption = new Text("MISC", 0, 0, true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm);
						miscSelected();
			        }
					
			    });
				miscOption.setScale(optionsScale);
				
				exitOption = new Text("EXIT?", 0, 0, true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(gsm);
						if (ps == null) {
							gsm.getApp().fadeOut();
							gsm.getApp().setRunAfterTransition(new Runnable() {

								@Override
								public void run() {
									gsm.removeState(SettingState.class);
								}
								
							});
						} else {
							gsm.removeState(SettingState.class);
						}
			        }
					
			    });
				exitOption.setScale(optionsScale);
				
				saveOption = new Text("SAVE?", 0, 0, true);
				saveOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH3.play(gsm);
						saveSettings();
			        }
					
			    });
				saveOption.setScale(optionsScale);
				
				resetOption = new Text("RESET?", 0, 0, true);
				resetOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH3.play(gsm);
						resetSettings();
			        }
					
			    });
				resetOption.setScale(optionsScale);
				
				options.add(displayOption).pad(optionsPad).row();
				options.add(controlOption).pad(optionsPad).row();
				options.add(audioOption).pad(optionsPad).row();
				options.add(gameOption).pad(optionsPad).row();
				options.add(miscOption).pad(optionsPad).row();
				options.add(saveOption).pad(optionsPad).row();
				options.add(resetOption).pad(optionsPad).row();
				options.add(exitOption).pad(optionsPad).row();
			}
		};
		app.newMenu(stage);
		
		if (gsm.getApp().getFadeLevel() >= 1.0f) {
			gsm.getApp().fadeIn();
		}
		
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
	 * This is called whenever the player selects the DISPLAY tab
	 */
	public void displaySelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.DISPLAY;
		
		Text screen = new Text("RESOLUTION: ", 0, 0, false);
		screen.setScale(detailsScale);
		
		resolutionOptions = new SelectBox<String>(GameStateManager.getSkin());
		resolutionOptions.setItems("1024 X 576", "1280 X 720", "1600 X 900", "1920 X 1080");
		resolutionOptions.setWidth(100);
		
		resolutionOptions.setSelectedIndex(gsm.getSetting().getResolution());

		Text framerate = new Text("FRAMERATE: ", 0, 0, false);
		framerate.setScale(detailsScale);
		
		framerateOptions = new SelectBox<String>(GameStateManager.getSkin());
		framerateOptions.setItems("30 fps", "60 fps", "90 fps", "120 fps");
		framerateOptions.setWidth(100);
		
		framerateOptions.setSelectedIndex(gsm.getSetting().getFramerate());
		
		fullscreen = new CheckBox("FULLSCREEN", GameStateManager.getSkin());
		vsync = new CheckBox("VSYNC", GameStateManager.getSkin());
		
		fullscreen.setChecked(gsm.getSetting().isFullscreen());
		vsync.setChecked(gsm.getSetting().isVSync());

		details.add(screen);
		details.add(resolutionOptions).row();
		details.add(framerate);
		details.add(framerateOptions).row();
		details.add(fullscreen).row();
		details.add(vsync).row();
	}
	
	/**
	 * This is called whenever the player selects the CONTROLS tab
	 */
	public void controlsSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.CONTROLS;
		
		VerticalGroup actions = new VerticalGroup().space(10).pad(50);
		actions.addActor(new Text("CONTROLS", 0, 0, false));
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(a.name() + ":==   " + getKey(a.getKey()) , 0, 0, true);
			
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
		
		keybinds = new ScrollPane(actions, GameStateManager.getSkin());
		keybinds.setFadeScrollBars(false);
		keybinds.setSize(detailsWidth, detailsHeight);
		
		details.add(keybinds);
		stage.setScrollFocus(keybinds);
	}
	
	/**
	 * This is called whenever the player selects the AUDIO tab
	 */
	public void audioSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.AUDIO;
		
		final Text soundText = new Text("SOUND VOLUME: " + (int)(gsm.getSetting().getSoundVolume() * 100), 0, 0, false);
		soundText.setScale(detailsScale);
		
		sound = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		sound.setValue(gsm.getSetting().getSoundVolume());
		
		sound.addListener( new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundText.setText("SOUND VOLUME: " + (int)(sound.getValue() * 100));
			}
		});
		
		final Text musicText = new Text("MUSIC VOLUME: " + (int)(gsm.getSetting().getMusicVolume() * 100), 0, 0, false);
		musicText.setScale(detailsScale);
		
		music = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		music.setValue(gsm.getSetting().getMusicVolume());
		
		music.addListener( new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				musicText.setText("MUSIC VOLUME: " + (int)(music.getValue() * 100));
			}
		});
		
		final Text masterText = new Text("MASTER VOLUME: " + (int)(gsm.getSetting().getMasterVolume() * 100), 0, 0, false);
		masterText.setScale(detailsScale);
		
		master = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		master.setValue(gsm.getSetting().getMasterVolume());
		
		master.addListener( new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				masterText.setText("MASTER VOLUME: " + (int)(master.getValue() * 100));
			}
		});
		
		details.add(soundText);
		details.add(sound).row();
		details.add(musicText);
		details.add(music).row();
		details.add(masterText);
		details.add(master).row();
	}
	
	public void gameSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.GAMEPLAY;
		
		Text timer = new Text("MATCH TIME: ", 0, 0, false);
		timer.setScale(0.25f);
		
		Text lives = new Text("LIVES: ", 0, 0, false);
		lives.setScale(0.25f);
		
		Text loadout = new Text("LOADOUT: ", 0, 0, false);
		loadout.setScale(0.25f);
		
		timerOptions = new SelectBox<String>(GameStateManager.getSkin());
		timerOptions.setItems("NO TIMER", "1 MIN", "2 MIN", "3 MIN", "4 MIN", "5 MIN");
		timerOptions.setWidth(100);
		
		timerOptions.setSelectedIndex(gsm.getSetting().getTimer());
		
		livesOptions = new SelectBox<String>(GameStateManager.getSkin());
		livesOptions.setItems("UNLIMITED", "1 LIFE", "2 LIVES", "3 LIVES", "4 LIVES", "5 LIVES");
		livesOptions.setWidth(100);
		
		livesOptions.setSelectedIndex(gsm.getSetting().getLives());
		
		loadoutOptions = new SelectBox<String>(GameStateManager.getSkin());
		loadoutOptions.setItems("DEFAULT", "SELECTED", "RANDOM");
		loadoutOptions.setWidth(100);
		
		loadoutOptions.setSelectedIndex(gsm.getSetting().getLoadoutType());
		
		details.add(timer);
		details.add(timerOptions).row();
		details.add(lives);
		details.add(livesOptions).row();
		details.add(loadout);
		details.add(loadoutOptions).row();
	}
	
	public void miscSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.MISC;
		
		Text maxPlayers = new Text("MAX PLAYERS: ", 0, 0, false);
		maxPlayers.setScale(0.25f);
		
		randomNameAlliteration = new CheckBox("RANDOM NAME ALLITERATION?", GameStateManager.getSkin());
		randomNameAlliteration.setChecked(gsm.getSetting().isRandomNameAlliteration());
		
		consoleEnabled = new CheckBox("Console Enabled?", GameStateManager.getSkin());
		consoleEnabled.setChecked(gsm.getSetting().isConsoleEnabled());
		
		playerCapacity = new SelectBox<String>(GameStateManager.getSkin());
		playerCapacity.setItems("1", "2", "3", "4", "5", "6");
		playerCapacity.setWidth(100);
		
		playerCapacity.setSelectedIndex(gsm.getSetting().getMaxPlayers());
		
		details.add(randomNameAlliteration).row();
		details.add(consoleEnabled).row();
		details.add(maxPlayers);
		details.add(playerCapacity).row();
	}
	
	/**
	 * Save the player chosen settings of whichever tab they are editing
	 */
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
		case AUDIO:
			gsm.getSetting().setSoundVolume(sound.getValue());
			gsm.getSetting().setMusicVolume(music.getValue());
			gsm.getSetting().setMasterVolume(master.getValue());
			gsm.getSetting().saveSetting();
			audioSelected();
			break;
		case GAMEPLAY:
			gsm.getSetting().setTimer(timerOptions.getSelectedIndex());
			gsm.getSetting().setLives(livesOptions.getSelectedIndex());
			gsm.getSetting().setLoadoutType(loadoutOptions.getSelectedIndex());
			gsm.getSetting().saveSetting();
			gameSelected();
			break;
		case MISC:
			gsm.getSetting().setRandomNameAlliteration(randomNameAlliteration.isChecked());
			gsm.getSetting().setConsoleEnabled(consoleEnabled.isChecked());
			gsm.getSetting().setMaxPlayers(playerCapacity.getSelectedIndex());
			gsm.getSetting().saveSetting();
			miscSelected();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Reset this tab's settings to the default values
	 */
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
		case AUDIO:
			gsm.getSetting().resetAudio();
			gsm.getSetting().saveSetting();
			audioSelected();
		case GAMEPLAY:
			gsm.getSetting().resetGameplay();
			gsm.getSetting().saveSetting();
			gameSelected();
			break;
		case MISC:
			gsm.getSetting().resetMisc();
			gsm.getSetting().saveSetting();
			miscSelected();
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
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
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
	
	//This is called when the setting state is designated to be removed. (if another player unpauses)
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }
		
	@Override
	public void dispose() { stage.dispose(); }
	
	public PlayState getPs() { return ps; }
	
	@Override
	public boolean processTransitions() { 
		
		//if this is a setting state over a play state, we don't process transitions
		return ps == null; 
	}
	
	public enum settingTab {
		DISPLAY,
		CONTROLS,
		AUDIO,
		GAMEPLAY,
		MISC,
		MULTIPLAYER,
		
	}
}
