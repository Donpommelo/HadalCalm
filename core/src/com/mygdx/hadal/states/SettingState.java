package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The Setting State allows the player to change their display setttings, key bindings and other stuff like that.
 * @author Zachary Tu
 */
public class SettingState extends GameState {
	
	//These are all of the display and buttons visible to the player.
	private Text displayOption, controlOption, audioOption, gameOption, miscOption, exitOption, saveOption, resetOption;
	private TextField portNumber;

	//This scrollpane holds the options for key bindings
	private ScrollPane keybinds;
	
	//This is the hotkey option that the player has selected to change
	private PlayerAction currentlyEditing;
	
	//this is a pause state underneath this state. (null if calling this state from the title screen)
	private PauseState ps;
	private PlayState playstate;
	
	//This determines whether the pause state should be removed or not next engine tick.
	private boolean toRemove = false;
	
	//This table contains the ui elements of the pause screen
	private Table options, details, extra;
	
	private SelectBox<String> resolutionOptions, framerateOptions, cursorOptions, cursorSize, cursorColor, hitsoundOptions, pvpTimerOptions, coopTimerOptions, livesOptions, loadoutOptions, artifactSlots, pvpMode, playerCapacity;
	private Slider sound, music, master, hitsound;
	private CheckBox fullscreen, vsync, debugHitbox, randomNameAlliteration, consoleEnabled, verboseDeathMessage, multiplayerPause, exportChatLog;
		
	//Dimentions of the setting menu
	private final static int optionsX = 25;
	private final static int optionsY = 100;
	private final static int optionsWidth = 300;
	private final static int optionsHeight = 600;
	
	private final static int detailsX = 320;
	private final static int detailsY = 100;
	private final static int detailsWidth = 500;
	private final static int detailsHeight = 600;
	
	private final static int extraX = 820;
	private final static int extraY = 600;
	private final static int extraWidth = 240;
	private final static int extraHeight = 100;
	
	private final static float optionsScale = 0.5f;
	private final static float optionsPad = 15.0f;
	private final static float detailsScale = 0.3f;
	
	private final static float titlePad = 25.0f;
	private final static float detailsPad = 15.0f;
	
	//These options make it easier to convert the index setting to a displayed string
	public final static String[] timerChoices = {"NO TIMER", "1 MIN", "2 MIN", "3 MIN", "4 MIN", "5 MIN"};
	public final static String[] livesChoices = {"UNLIMITED", "1 LIFE", "2 LIVES", "3 LIVES", "4 LIVES", "5 LIVES"};
	public final static String[] loadoutChoices = {"COPY HOST", "SELECTED", "RANDOM"};
	public final static String[] artifactChoices = {"0", "1", "2", "3", "4", "5", "6"};
	public final static String[] modeChoices = {"KILLS -> SCORE", "EGGPLANTS -> SCORE"};
	public final static String[] capacityChoices = {"1", "2", "3", "4", "5", "6"};
	
	//this is the current setting tab the player is using
	private settingTab currentTab;
	
	//this state's background shader
	private Shader shaderBackground;
	private TextureRegion bg;
	
	public SettingState(GameStateManager gsm, PauseState ps) {
		super(gsm);
		this.ps = ps;
		
		if (ps != null) {
			playstate = ps.getPs();
		}
		
		shaderBackground = Shader.WAVE;
		shaderBackground.loadDefaultShader();
		this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new MenuWindow(optionsX, optionsY, optionsWidth, optionsHeight));
				addActor(new MenuWindow(detailsX, detailsY, detailsWidth, detailsHeight));
				addActor(new MenuWindow(extraX, extraY, extraWidth, extraHeight));
				
				options = new Table();
				options.setPosition(optionsX, optionsY);
				options.setSize(optionsWidth, optionsHeight);
				options.top();
				addActor(options);
				
				details = new Table();
				details.setPosition(detailsX, detailsY);
				details.setSize(detailsWidth, detailsHeight);
				details.top();
				addActor(details);
				
				extra = new Table();
				extra.setPosition(extraX, extraY);
				extra.setSize(extraWidth, extraHeight);
				addActor(extra);

				displayOption = new Text("DISPLAY", 0, 0, true);
				displayOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						displaySelected();
			        }
			    });
				displayOption.setScale(optionsScale);
				
				controlOption = new Text("CONTROLS", 0, 0, true);
				controlOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						controlsSelected();
			        }
			    });
				controlOption.setScale(optionsScale);
				
				audioOption = new Text("AUDIO", 0, 0, true);
				audioOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						audioSelected();
			        }
			    });
				audioOption.setScale(optionsScale);
				
				gameOption = new Text("GAME", 0, 0, true);
				gameOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						gameSelected();
			        }
			    });
				gameOption.setScale(optionsScale);
				
				miscOption = new Text("MISC", 0, 0, true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						miscSelected();
			        }
			    });
				miscOption.setScale(optionsScale);
				
				if (playstate == null) {
					exitOption = new Text("EXIT?", 0, 0, true);
				} else {
					exitOption = new Text("RETURN?", 0, 0, true);
				}
				
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
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
				
				saveOption = new Text("SAVE CHANGES?", 0, 0, true);
				saveOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						saveSettings();
						SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
			        }
			    });
				saveOption.setScale(optionsScale);
				
				resetOption = new Text("RESET CHANGES?", 0, 0, true);
				resetOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
						resetSettings();
			        }
			    });
				resetOption.setScale(optionsScale);
				
				options.add(displayOption).pad(optionsPad).row();
				options.add(controlOption).pad(optionsPad).row();
				options.add(audioOption).pad(optionsPad).row();
				options.add(gameOption).pad(optionsPad).row();
				options.add(miscOption).pad(optionsPad).row();
				options.add(exitOption).pad(optionsPad).expand().row();
				
				extra.add(saveOption).pad(optionsPad).row();
				extra.add(resetOption).pad(optionsPad).row();
			}
		};
		app.newMenu(stage);
		
		//fade in the state upon opening it if the game is faded out at all. (from a title screen transition)
		if (gsm.getApp().getFadeLevel() >= 1.0f) {
			gsm.getApp().fadeIn();
		}
		
		//the setting state input processor accounts for the player changing their hotkeys
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
		
		//start off with display selected
		displaySelected();
	}
	
	/**
	 * This is called whenever the player selects the DISPLAY tab
	 */
	private void displaySelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.DISPLAY;
		
		details.add(new Text("DISPLAY", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text screen = new Text("RESOLUTION: ", 0, 0, false);
		screen.setScale(detailsScale);
		
		resolutionOptions = new SelectBox<String>(GameStateManager.getSkin());
		resolutionOptions.setItems("1024 X 576", "1280 X 720", "1366 x 768", "1600 X 900", "1920 x 1080", "2560 x 1080");
		resolutionOptions.setWidth(100);
		
		resolutionOptions.setSelectedIndex(gsm.getSetting().getResolution());

		Text framerate = new Text("FRAMERATE: ", 0, 0, false);
		framerate.setScale(detailsScale);
		
		Text cursortype = new Text("CURSOR TYPE: ", 0, 0, false);
		cursortype.setScale(detailsScale);
		
		Text cursorsize = new Text("CURSOR SIZE: ", 0, 0, false);
		cursorsize.setScale(detailsScale);
		
		Text cursorcolor = new Text("CURSOR COLOR: ", 0, 0, false);
		cursorcolor.setScale(detailsScale);
		
		framerateOptions = new SelectBox<String>(GameStateManager.getSkin());
		framerateOptions.setItems("30 fps", "60 fps", "90 fps", "120 fps");
		
		framerateOptions.setSelectedIndex(gsm.getSetting().getFramerate());
		
		cursorOptions = new SelectBox<String>(GameStateManager.getSkin());
		cursorOptions.setItems("DEFAULT", "CROSSHAIR", "DOT");
		
		cursorOptions.setSelectedIndex(gsm.getSetting().getCursorType());

		cursorSize = new SelectBox<String>(GameStateManager.getSkin());
		cursorSize.setItems("SMALL", "MEDIUM", "LARGE");
		
		cursorSize.setSelectedIndex(gsm.getSetting().getCursorSize());
		
		cursorColor = new SelectBox<String>(GameStateManager.getSkin());
		cursorColor.setItems("BLACK", "CYAN", "LIME", "MAGENTA", "RED", "WHITE", "YELLOW");
		
		cursorColor.setSelectedIndex(gsm.getSetting().getCursorColor());
		
		fullscreen = new CheckBox("FULLSCREEN?", GameStateManager.getSkin());
		vsync = new CheckBox("VSYNC?", GameStateManager.getSkin());
		debugHitbox = new CheckBox("DRAW DEBUG HITBOX OUTLINES?", GameStateManager.getSkin());
		
		fullscreen.setChecked(gsm.getSetting().isFullscreen());
		vsync.setChecked(gsm.getSetting().isVSync());
		debugHitbox.setChecked(gsm.getSetting().isDebugHitbox());

		details.add(screen);
		details.add(resolutionOptions).pad(detailsPad).row();
		details.add(framerate);
		details.add(framerateOptions).pad(detailsPad).row();
		details.add(fullscreen);
		details.add(vsync).pad(detailsPad).row();
		details.add(debugHitbox).colspan(2).pad(detailsPad).row();
		details.add(cursortype);
		details.add(cursorOptions).pad(detailsPad).row();
		details.add(cursorsize);
		details.add(cursorSize).pad(detailsPad).row();
		details.add(cursorcolor);
		details.add(cursorColor).pad(detailsPad).row();
	}
	
	/**
	 * This is called whenever the player selects the CONTROLS tab
	 */
	private void controlsSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.CONTROLS;
		
		details.add(new Text("CONTROLS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		VerticalGroup actions = new VerticalGroup().space(detailsPad).pad(titlePad);
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(a.toString() + ":==   " + getKey(a.getKey()) , 0, 0, true);
			
			actionChoose.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent e, float x, float y) {
					
					//Clicking any option will highlight it and designate it as the next to update.
					((Text) e.getListenerActor()).setText(action.toString() + ":==   " + getKey(action.getKey()) + " <--");					
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
		keybinds.setSize(detailsWidth, detailsHeight);
		
		details.add(keybinds);
		stage.setScrollFocus(keybinds);
	}
	
	/**
	 * This is called whenever the player selects the AUDIO tab
	 */
	private void audioSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.AUDIO;
		
		details.add(new Text("AUDIO", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		final Text soundText = new Text("SOUND VOLUME: " + (int)(gsm.getSetting().getSoundVolume() * 100), 0, 0, false);
		soundText.setScale(detailsScale);
		
		sound = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		sound.setValue(gsm.getSetting().getSoundVolume());
		
		sound.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundText.setText("SOUND VOLUME: " + (int)(sound.getValue() * 100));
			}
		});
		
		final Text musicText = new Text("MUSIC VOLUME: " + (int)(gsm.getSetting().getMusicVolume() * 100), 0, 0, false);
		musicText.setScale(detailsScale);
		
		music = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		music.setValue(gsm.getSetting().getMusicVolume());
		
		music.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				musicText.setText("MUSIC VOLUME: " + (int)(music.getValue() * 100));
			}
		});
		
		final Text masterText = new Text("MASTER VOLUME: " + (int)(gsm.getSetting().getMasterVolume() * 100), 0, 0, false);
		masterText.setScale(detailsScale);
		
		master = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		master.setValue(gsm.getSetting().getMasterVolume());
		
		master.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				masterText.setText("MASTER VOLUME: " + (int)(master.getValue() * 100));
			}
		});
		
		Text hitsoundText = new Text("HITSOUND: ", 0, 0, false);
		hitsoundText.setScale(0.25f);
		
		hitsoundOptions = new SelectBox<String>(GameStateManager.getSkin());
		hitsoundOptions.setItems("NONE", "BLIP", "COWBELL", "DING", "DRUM", "PIANO", "SHREK");
		hitsoundOptions.setSelectedIndex(gsm.getSetting().getHitsound());
		
		hitsoundOptions.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (hitsoundOptions.getSelectedIndex() != 0) {
					gsm.getSetting().indexToHitsound(hitsoundOptions.getSelectedIndex()).play(gsm, gsm.getSetting().getHitsoundVolume(), 1.0f, false);
				}
			}
		});
		
		final Text hitsoundVolumeText = new Text("HITSOUND VOLUME: " + (int)(gsm.getSetting().getHitsoundVolume() * 100), 0, 0, false);
		hitsoundVolumeText.setScale(detailsScale);
		
		hitsound = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		hitsound.setValue(gsm.getSetting().getHitsoundVolume());
		
		hitsound.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hitsoundVolumeText.setText("HITSOUND VOLUME: " + (int)(hitsound.getValue() * 100));
			}
		});
		
		details.add(soundText);
		details.add(sound).pad(detailsPad).row();
		details.add(musicText);
		details.add(music).pad(detailsPad).row();
		details.add(masterText);
		details.add(master).pad(detailsPad).row();
		details.add(hitsoundText);
		details.add(hitsoundOptions).pad(detailsPad).row();
		details.add(hitsoundVolumeText);
		details.add(hitsound).pad(detailsPad).row();
	}
	
	/**
	 * This is called whenever the player selects the GAME tab
	 */
	private void gameSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.GAMEPLAY;
		
		details.add(new Text("GAMEPLAY", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text pvpTimer = new Text("PVP MATCH TIME: ", 0, 0, false);
		pvpTimer.setScale(0.25f);
		
		Text coopTimer = new Text("COOP MATCH TIME: ", 0, 0, false);
		coopTimer.setScale(0.25f);
		
		Text lives = new Text("LIVES: ", 0, 0, false);
		lives.setScale(0.25f);
		
		Text loadout = new Text("LOADOUT: ", 0, 0, false);
		loadout.setScale(0.25f);
		
		Text slots = new Text("MULTIPLAYER ARTIFACT SLOTS: ", 0, 0, false);
		slots.setScale(0.25f);
		
		Text mode = new Text("PVP MODE: ", 0, 0, false);
		mode.setScale(0.25f);
		
		pvpTimerOptions = new SelectBox<String>(GameStateManager.getSkin());
		pvpTimerOptions.setItems(timerChoices);
		pvpTimerOptions.setWidth(100);
		
		pvpTimerOptions.setSelectedIndex(gsm.getSetting().getPVPTimer());
		
		coopTimerOptions = new SelectBox<String>(GameStateManager.getSkin());
		coopTimerOptions.setItems(timerChoices);
		coopTimerOptions.setWidth(100);
		
		coopTimerOptions.setSelectedIndex(gsm.getSetting().getCoopTimer());
		
		livesOptions = new SelectBox<String>(GameStateManager.getSkin());
		livesOptions.setItems(livesChoices);
		
		livesOptions.setSelectedIndex(gsm.getSetting().getLives());
		
		loadoutOptions = new SelectBox<String>(GameStateManager.getSkin());
		loadoutOptions.setItems(loadoutChoices);
		
		loadoutOptions.setSelectedIndex(gsm.getSetting().getLoadoutType());
		
		artifactSlots = new SelectBox<String>(GameStateManager.getSkin());
		artifactSlots.setItems(artifactChoices);
		
		artifactSlots.setSelectedIndex(gsm.getSetting().getArtifactSlots());
		
		pvpMode = new SelectBox<String>(GameStateManager.getSkin());
		pvpMode.setItems(modeChoices);
		
		pvpMode.setSelectedIndex(gsm.getSetting().getPVPMode());
		
		details.add(pvpTimer);
		details.add(pvpTimerOptions).pad(detailsPad).row();
		details.add(coopTimer);
		details.add(coopTimerOptions).pad(detailsPad).row();
		details.add(lives);
		details.add(livesOptions).pad(detailsPad).row();
		details.add(loadout);
		details.add(loadoutOptions).pad(detailsPad).row();
		details.add(slots);
		details.add(artifactSlots).pad(detailsPad).row();
		details.add(mode);
		details.add(pvpMode).pad(detailsPad).row();
	}
	
	/**
	 * This is called whenever the player selects the MISC tab
	 */
	private void miscSelected() {
		details.clear();
		currentlyEditing = null;
		currentTab = settingTab.MISC;
		
		details.add(new Text("MISCELLANEOUS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text maxPlayers = new Text("MAX SERVER SIZE: ", 0, 0, false);
		maxPlayers.setScale(0.25f);
		
		Text port = new Text("PORT NUMBER: ", 0, 0, false);
		port.setScale(0.25f);
		
		randomNameAlliteration = new CheckBox("RANDOM NAME ALLITERATION?", GameStateManager.getSkin());
		randomNameAlliteration.setChecked(gsm.getSetting().isRandomNameAlliteration());
		
		consoleEnabled = new CheckBox("Console Enabled?", GameStateManager.getSkin());
		consoleEnabled.setChecked(gsm.getSetting().isConsoleEnabled());
		
		verboseDeathMessage = new CheckBox("Verbose Death Messages?", GameStateManager.getSkin());
		verboseDeathMessage.setChecked(gsm.getSetting().isVerboseDeathMessage());
		
		multiplayerPause = new CheckBox("Enable Multiplayer Pause?", GameStateManager.getSkin());
		multiplayerPause.setChecked(gsm.getSetting().isMultiplayerPause());
		
		playerCapacity = new SelectBox<String>(GameStateManager.getSkin());
		playerCapacity.setItems(capacityChoices);
		playerCapacity.setWidth(100);
		
		playerCapacity.setSelectedIndex(gsm.getSetting().getMaxPlayers());
		
		portNumber = new TextField(String.valueOf(gsm.getSetting().getPortNumber()), GameStateManager.getSkin());
		portNumber.setMessageText("PORT NUMBER");
		portNumber.setMaxLength(5);
		portNumber.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		
		exportChatLog = new CheckBox("Export Chat Logs on Exit?", GameStateManager.getSkin());
		exportChatLog.setChecked(gsm.getSetting().isExportChatLog());
		
		details.add(randomNameAlliteration).colspan(2).pad(detailsPad).row();
		details.add(consoleEnabled).colspan(2).pad(detailsPad).row();
		details.add(verboseDeathMessage).colspan(2).pad(detailsPad).row();
		details.add(multiplayerPause).colspan(2).pad(detailsPad).row();
		details.add(maxPlayers);
		details.add(playerCapacity).colspan(2).pad(detailsPad).row();
		details.add(port);
		details.add(portNumber).colspan(2).width(100).pad(detailsPad).row();
		details.add(exportChatLog).colspan(2).pad(detailsPad).row();
	}
	
	/**
	 * Save the player chosen settings of whichever tab they are editing
	 */
	private void saveSettings() {
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
			gsm.getSetting().setDebugHitbox(debugHitbox.isChecked());
			gsm.getSetting().setCursorType(cursorOptions.getSelectedIndex());
			gsm.getSetting().setCursorSize(cursorSize.getSelectedIndex());
			gsm.getSetting().setCursorColor(cursorColor.getSelectedIndex());
			gsm.getSetting().setDisplay(gsm.getApp(), playstate);
			gsm.getSetting().saveSetting();
			displaySelected();
			break;
		case AUDIO:
			gsm.getSetting().setSoundVolume(sound.getValue());
			gsm.getSetting().setMusicVolume(music.getValue());
			gsm.getSetting().setMasterVolume(master.getValue());
			gsm.getSetting().setHitsoundType(hitsoundOptions.getSelectedIndex());
			gsm.getSetting().setHitsoundVolume(hitsound.getValue());
			gsm.getSetting().saveSetting();
			audioSelected();
			break;
		case GAMEPLAY:
			gsm.getSetting().setPVPTimer(pvpTimerOptions.getSelectedIndex());
			gsm.getSetting().setCoopTimer(coopTimerOptions.getSelectedIndex());
			gsm.getSetting().setLives(livesOptions.getSelectedIndex());
			gsm.getSetting().setLoadoutType(loadoutOptions.getSelectedIndex());
			gsm.getSetting().setArtifactSlots(artifactSlots.getSelectedIndex());
			gsm.getSetting().setPVPMode(pvpMode.getSelectedIndex());
			gsm.getSetting().saveSetting();
			gameSelected();
			break;
		case MISC:
			gsm.getSetting().setRandomNameAlliteration(randomNameAlliteration.isChecked());
			gsm.getSetting().setConsoleEnabled(consoleEnabled.isChecked());
			gsm.getSetting().setVerboseDeathMessage(verboseDeathMessage.isChecked());
			gsm.getSetting().setMultiplayerPause(multiplayerPause.isChecked());
			gsm.getSetting().setMaxPlayers(playerCapacity.getSelectedIndex());
			gsm.getSetting().setPortNumber(Integer.valueOf(portNumber.getText()));
			gsm.getSetting().setExportChatLog(exportChatLog.isChecked());
			gsm.getSetting().saveSetting();
			miscSelected();
			break;
		default:
			break;
		}
		
		updateSharedSettings();
	}
	
	/**
	 * Reset this tab's settings to the default values
	 */
	private void resetSettings() {
		switch(currentTab) {
		case CONTROLS:
			PlayerAction.resetKeys();
        	controlsSelected();
			break;
		case DISPLAY:
			gsm.getSetting().resetDisplay();
			gsm.getSetting().setDisplay(gsm.getApp(), playstate);
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
		
		updateSharedSettings();
	}
	
	/**
	 * This updates the settings that are visible from the score window
	 */
	private void updateSharedSettings() {
		gsm.setSharedSetting(gsm.getSetting().generateSharedSetting());
		
		//the server should update their scoretable when settings are changed
		if (ps != null) {
			if (ps.getPs().isServer()) {
				ps.getPs().getScoreWindow().syncSettingTable();
			}
		}
	}
	
	/**
	 * This converts a keycode to s readable string
	 * @param keycode: key to read
	 * @return: string to return 
	 */
	public static String getKey(int keycode) {
		
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
			ps.update(delta);
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
			gsm.removeState(SettingState.class);
			gsm.removeState(PauseState.class);
		}
	}
	
	private float timer;
	@Override
	public void render(float delta) {
		
		//Render the playstate and playstate ui underneath
		if (ps != null) {
			ps.getPs().render(delta);
			ps.getPs().stage.getViewport().apply();
			ps.getPs().stage.draw();
		} else {
			timer += delta;
			
			batch.begin();
			
			shaderBackground.getShader().begin();
			shaderBackground.shaderDefaultUpdate(timer);
			shaderBackground.getShader().end();
			batch.setShader(shaderBackground.getShader());
			
			batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
			
			batch.setShader(null);
			
			batch.end();
		}
	}
	
	//This is called when the setting state is designated to be removed. (if another player unpauses)
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }
		
	@Override
	public void dispose() { 
		stage.dispose();
	}
	
	public PlayState getPs() { return ps.getPs(); }
	
	@Override
	public boolean processTransitions() { 
		
		//if this is a setting state over a play state, we don't process transitions
		if (ps == null) {
			return true;
		} else {
			return ps.processTransitions();
		}
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
