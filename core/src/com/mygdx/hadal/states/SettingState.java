package com.mygdx.hadal.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TableWindow;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TooltipManager;

import static com.mygdx.hadal.constants.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.constants.Constants.TRANSITION_DURATION;
import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * The Setting State allows the player to change their display settings, key bindings and other stuff like that.
 * @author Shorfutticus Squonzworth
 */
public class SettingState extends GameState {

	//Dimensions of the setting menu
	private static final int OPTIONS_X = -1025;
	private static final int OPTIONS_Y = 100;
	private static final int OPTIONS_X_ENABLED = 25;
	private static final int OPTIONS_Y_ENABLED = 100;
	private static final int OPTIONS_WIDTH = 300;
	private static final int OPTIONS_HEIGHT = 600;

	private static final int DETAILS_X = -730;
	private static final int DETAILS_Y = 20;
	private static final int DETAILS_X_ENABLED = 320;
	private static final int DETAILS_Y_ENABLED = 20;
	private static final int DETAILS_WIDTH = 500;
	private static final int DETAILS_HEIGHT = 680;
	private static final int SCROLL_WIDTH = 480;

	private static final float OPTIONS_SCALE = 0.5f;
	private static final float OPTION_HEIGHT = 35.0f;
	private static final float OPTION_PAD = 10.0f;
	private static final float DETAILS_SCALE = 0.25f;

	private static final float TITLE_PAD = 25.0f;
	private static final float DETAIL_HEIGHT = 35.0f;
	private static final float DETAIL_PAD = 10.0f;

	//These options make it easier to convert the index setting to a displayed string
	public static final String[] ARTIFACT_CHOICES = {"0", "1", "2", "3", "4", "5", "6"};
	public static final String[] CAPACITY_CHOICES = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};

	//This scrollpane holds the options for key bindings
	private ScrollPane keybinds;
	
	//This is the hotkey option that the player has selected to change
	private PlayerAction currentlyEditing;
	
	//this is the state underneath this state.
	private final GameState peekState;
	private PlayState playState;
	
	//This determines whether the pause state should be removed or not next engine tick.
	//We do this instead of removing right away in case we remove as a result of receiving a packet from another player unpausing (which can happen whenever).
	private boolean toRemove;
	
	//This table contains the ui elements of the pause screen
	private Table options, details;

	//These are all of the display and buttons visible to the player.
	private Text displayOption, controlOption, audioOption, serverOption, miscOption, exitOption, resetOption;
	private TextField portNumber, serverPassword;
	private SelectBox<String> resolutionOptions, framerateOptions, cursorOptions, cursorSize, cursorColor,
		hitsoundOptions, artifactSlots, playerCapacity;
	private Slider sound, music, master, hitsound;
	private CheckBox fullscreen, vsync, autoIconify, debugHitbox, displayNames, displayHp, randomNameAlliteration, consoleEnabled,
		verboseDeathMessage, multiplayerPause, exportChatLog, enableUPNP, hideHUD, mouseCameraTrack, screenShake;
		
	//this is the current setting tab the player is using
	private settingTab currentTab;

	/**
	 * Constructor will be called when the player enters the setting state from the title menu or the pause menu.
	 */
	public SettingState(HadalGame app, GameState peekState) {
		super(app);
		this.peekState = peekState;
		
		if (peekState instanceof PauseState pauseState) {
			playState = pauseState.getPs();
		}
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				options = new TableWindow();
				options.setPosition(OPTIONS_X, OPTIONS_Y);
				options.setSize(OPTIONS_WIDTH, OPTIONS_HEIGHT);
				options.top();
				addActor(options);
				
				details = new TableWindow();
				details.setPosition(DETAILS_X, DETAILS_Y);
				details.setSize(DETAILS_WIDTH, DETAILS_HEIGHT);
				details.top();
				addActor(details);
				
				displayOption = new Text(UIText.DISPLAY.text()).setButton(true);
				displayOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(1.0f, false);
						saveSettings();
						displaySelected();
			        }
			    });
				displayOption.setScale(OPTIONS_SCALE);
				
				controlOption = new Text(UIText.CONTROLS.text()).setButton(true);
				controlOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(1.0f, false);
						saveSettings();
						controlsSelected();
			        }
			    });
				controlOption.setScale(OPTIONS_SCALE);
				
				audioOption = new Text(UIText.AUDIO.text()).setButton(true);
				audioOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(1.0f, false);
						saveSettings();
						audioSelected();
			        }
			    });
				audioOption.setScale(OPTIONS_SCALE);
				
				serverOption = new Text(UIText.SERVER.text()).setButton(true);
				serverOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(1.0f, false);
						saveSettings();
						serverSelected();
			        }
			    });
				serverOption.setScale(OPTIONS_SCALE);
				
				miscOption = new Text(UIText.MISC.text()).setButton(true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(1.0f, false);
						saveSettings();
						miscSelected();
			        }
			    });
				miscOption.setScale(OPTIONS_SCALE);

				exitOption = new Text(UIText.RETURN.text()).setButton(true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(1.0f, false);
						saveSettings();

						//if exiting to title screen, play transition. Otherwise, just remove this state
						transitionOut(() -> StateManager.removeState(SettingState.class));
			        }
			    });
				exitOption.setScale(OPTIONS_SCALE);

				resetOption = new Text(UIText.RESET.text()).setButton(true);
				resetOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH3.play(1.0f, false);
						resetSettings();
			        }
			    });
				resetOption.setScale(OPTIONS_SCALE);
				
				options.add(displayOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(controlOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(audioOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(serverOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(miscOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(resetOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(exitOption).height(OPTION_HEIGHT).pad(OPTION_PAD).expand().row();
			}
		};
		app.newMenu(stage);
		
		transitionIn();

		//the setting state input processor accounts for the player changing their hotkeys
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputAdapter() {

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
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return keyDown(button); }

			//This is just a janky way of implementing setting mouse wheel as a hotkey.
			@Override
			public boolean scrolled(float amountX, float amountY) {	return keyDown((int) amountY * 1000); }
		});
		inputMultiplexer.addProcessor(Gdx.input.getInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		//start off with display selected unless this menu is called from lobby
		if (StateManager.states.size() > 1) {
			if (StateManager.states.get(StateManager.states.size() - 2) instanceof LobbyState) {
				serverSelected();
			} else {
				displaySelected();
			}
		} else {
			displaySelected();
		}
	}
	
	/**
	 * This is called whenever the player selects the DISPLAY tab
	 */
	private void displaySelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.DISPLAY;
		
		details.add(new Text(UIText.DISPLAY.text())).colspan(2).pad(TITLE_PAD).row();
		
		Text screen = new Text(UIText.RESOLUTION.text());
		screen.setScale(DETAILS_SCALE);
		
		resolutionOptions = new SelectBox<>(SKIN);
		resolutionOptions.setItems(UIText.RESOLUTION_OPTIONS.text().split(","));
		resolutionOptions.setWidth(100);
		
		resolutionOptions.setSelectedIndex(JSONManager.setting.getResolution());

		Text framerate = new Text(UIText.FRAMERATE.text());
		framerate.setScale(DETAILS_SCALE);
		
		Text cursortype = new Text(UIText.CURSOR_TYPE.text());
		cursortype.setScale(DETAILS_SCALE);
		
		Text cursorsize = new Text(UIText.CURSOR_SIZE.text());
		cursorsize.setScale(DETAILS_SCALE);
		
		Text cursorcolor = new Text(UIText.CURSOR_COLOR.text());
		cursorcolor.setScale(DETAILS_SCALE);

		framerateOptions = new SelectBox<>(SKIN);
		framerateOptions.setItems(UIText.FRAMERATE_OPTIONS.text().split(","));
		
		framerateOptions.setSelectedIndex(JSONManager.setting.getFramerate());

		ChangeListener cursorChange = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				JSONManager.setting.setCursorType(cursorOptions.getSelectedIndex());
				JSONManager.setting.setCursorSize(cursorSize.getSelectedIndex());
				JSONManager.setting.setCursorColor(cursorColor.getSelectedIndex());
				JSONManager.setting.setCursor();
			}
		};

		ChangeListener displayChange = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				saveSettings();
			}
		};

		cursorOptions = new SelectBox<>(SKIN);
		cursorOptions.setItems(UIText.CURSOR_TYPE_OPTIONS.text().split(","));
		cursorOptions.setSelectedIndex(JSONManager.setting.getCursorType());
		cursorOptions.addListener(cursorChange);

		cursorSize = new SelectBox<>(SKIN);
		cursorSize.setItems(UIText.CURSOR_SIZE_OPTIONS.text().split(","));
		cursorSize.setSelectedIndex(JSONManager.setting.getCursorSize());
		cursorSize.addListener(cursorChange);

		cursorColor = new SelectBox<>(SKIN);
		cursorColor.setItems(UIText.CURSOR_COLOR_OPTIONS.text().split(","));
		cursorColor.setSelectedIndex(JSONManager.setting.getCursorColor());
		cursorColor.addListener(cursorChange);

		fullscreen = new CheckBox(UIText.FULLSCREEN.text(), SKIN);
		vsync = new CheckBox(UIText.VSYNC.text(), SKIN);
		autoIconify = new CheckBox(UIText.ALT_TAB.text(), SKIN);
		debugHitbox = new CheckBox(UIText.DEBUG_OUTLINES.text(), SKIN);
		displayNames = new CheckBox(UIText.VISIBLE_NAMES.text(), SKIN);
		displayHp = new CheckBox(UIText.VISIBLE_HP.text(), SKIN);
		mouseCameraTrack = new CheckBox(UIText.CAMERA_AIM.text(), SKIN);
		screenShake = new CheckBox(UIText.SCREEN_SHAKE.text(), SKIN);

		TooltipManager.addTooltip(mouseCameraTrack, UIText.CAMERA_AIM_DESC.text());

		fullscreen.setChecked(JSONManager.setting.isFullscreen());
		vsync.setChecked(JSONManager.setting.isVSync());
		autoIconify.setChecked(JSONManager.setting.isAutoIconify());
		debugHitbox.setChecked(JSONManager.setting.isDebugHitbox());
		displayNames.setChecked(JSONManager.setting.isDisplayNames());
		displayHp.setChecked(JSONManager.setting.isDisplayHp());
		mouseCameraTrack.setChecked(JSONManager.setting.isMouseCameraTrack());
		screenShake.setChecked(JSONManager.setting.isScreenShake());

		fullscreen.addListener(displayChange);
		vsync.addListener(displayChange);
		autoIconify.addListener(displayChange);
		debugHitbox.addListener(displayChange);
		displayNames.addListener(displayChange);
		displayHp.addListener(displayChange);
		mouseCameraTrack.addListener(displayChange);
		screenShake.addListener(displayChange);
		resolutionOptions.addListener(displayChange);
		framerateOptions.addListener(displayChange);

		details.add(screen);
		details.add(resolutionOptions).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(framerate);
		details.add(framerateOptions).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(fullscreen);
		details.add(vsync).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(autoIconify);
		details.add(debugHitbox).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(displayNames);
		details.add(displayHp).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(cursortype);
		details.add(cursorOptions).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(cursorsize);
		details.add(cursorSize).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(cursorcolor);
		details.add(cursorColor).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(mouseCameraTrack);
		details.add(screenShake).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
	}
	
	/**
	 * This is called whenever the player selects the CONTROLS tab
	 */
	private void controlsSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.CONTROLS;
		
		details.add(new Text(UIText.CONTROLS.text())).pad(TITLE_PAD).row();
		
		VerticalGroup actions = new VerticalGroup().space(OPTION_PAD);
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(a.getText() + ":==   " + getKey(a.getKey())).setButton(true);
			
			actionChoose.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent e, float x, float y) {
					
					//Clicking any option will highlight it and designate it as the next to update.
					((Text) e.getListenerActor()).setText(action + ":==   " + getKey(action.getKey()) + " <--");
					currentlyEditing = action;
					actionChoose.setHeight(DETAIL_HEIGHT);
				}
			});
			actionChoose.setScale(DETAILS_SCALE);
			actionChoose.setHeight(DETAIL_HEIGHT);

			actions.addActor(actionChoose);
		}
		
		if (keybinds != null) {
			keybinds.remove();
		}
		
		keybinds = new ScrollPane(actions, SKIN);
		keybinds.setSize(DETAILS_WIDTH, DETAILS_HEIGHT);
		keybinds.setFadeScrollBars(false);
		
		details.add(keybinds).width(SCROLL_WIDTH).expandY().pad(OPTION_PAD);
		stage.setScrollFocus(keybinds);
	}
	
	/**
	 * This is called whenever the player selects the AUDIO tab
	 */
	private void audioSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.AUDIO;
		
		details.add(new Text(UIText.AUDIO .text())).colspan(2).pad(TITLE_PAD).row();
		
		Text soundText = new Text(UIText.SOUND_VOLUME.text(Integer.toString((int)(JSONManager.setting.getSoundVolume() * 100))));
		soundText.setScale(DETAILS_SCALE);
		
		sound = new Slider(0.0f, 1.0f, 0.01f, false, SKIN);
		sound.setValue(JSONManager.setting.getSoundVolume());
		
		sound.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundText.setText(UIText.SOUND_VOLUME.text(Integer.toString(((int)(sound.getValue() * 100)))));
			}
		});

		sound.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					JSONManager.setting.indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(sound.getValue() * master.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text musicText = new Text(UIText.MUSIC_VOLUME.text(Integer.toString((int)(JSONManager.setting.getMusicVolume() * 100))));
		musicText.setScale(DETAILS_SCALE);
		
		music = new Slider(0.0f, 1.0f, 0.01f, false, SKIN);
		music.setValue(JSONManager.setting.getMusicVolume());
		
		music.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				musicText.setText(UIText.MUSIC_VOLUME.text(Integer.toString((int)((music.getValue() * 100)))));
			}
		});

		music.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				JSONManager.setting.setMusicVolume(music.getValue() * master.getValue());
				JSONManager.setting.setAudio();
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text masterText = new Text(UIText.MASTER_VOLUME.text(Integer.toString((int)(JSONManager.setting.getMasterVolume() * 100))));
		masterText.setScale(DETAILS_SCALE);
		
		master = new Slider(0.0f, 1.0f, 0.01f, false, SKIN);
		master.setValue(JSONManager.setting.getMasterVolume());
		
		master.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				masterText.setText(UIText.MASTER_VOLUME.text(Integer.toString((int)(master.getValue() * 100))));
			}
		});

		master.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting master volume, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					JSONManager.setting.indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(sound.getValue() * master.getValue());
				}

				JSONManager.setting.setMusicVolume(master.getValue());
				JSONManager.setting.setAudio();
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text hitsoundText = new Text(UIText.HITSOUND.text());
		hitsoundText.setScale(0.25f);
		
		hitsoundOptions = new SelectBox<>(SKIN);
		hitsoundOptions.setItems(UIText.HITSOUND_OPTIONS.text().split(","));
		hitsoundOptions.setSelectedIndex(JSONManager.setting.getHitsound());
		
		hitsoundOptions.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					JSONManager.setting.indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(hitsound.getValue() * master.getValue());
				}
			}
		});
		
		Text hitsoundVolumeText = new Text(UIText.HITSOUND_VOLUME.text(Integer.toString((int)(JSONManager.setting.getHitsoundVolume() * 100))));
		hitsoundVolumeText.setScale(DETAILS_SCALE);
		
		hitsound = new Slider(0.0f, 1.0f, 0.01f, false, SKIN);
		hitsound.setValue(JSONManager.setting.getHitsoundVolume());
		hitsound.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hitsoundVolumeText.setText(UIText.HITSOUND_VOLUME.text(Integer.toString((int)(hitsound.getValue() * 100))));
			}
		});

		hitsound.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					JSONManager.setting.indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(hitsound.getValue() * master.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		details.add(soundText);
		details.add(sound).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(musicText);
		details.add(music).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(masterText);
		details.add(master).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(hitsoundText);
		details.add(hitsoundOptions).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(hitsoundVolumeText);
		details.add(hitsound).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
	}
	
	/**
	 * This is called whenever the player selects the GAME tab
	 */
	private void serverSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.SERVER;
		
		details.add(new Text(UIText.SERVER.text())).colspan(2).pad(TITLE_PAD).row();

		Text maxPlayers = new Text(UIText.SERVER_SIZE.text());
		maxPlayers.setScale(DETAILS_SCALE);

		Text port = new Text(UIText.PORT_NUMBER.text());
		port.setScale(DETAILS_SCALE);

		Text password = new Text(UIText.SERVER_PASSWORD.text());
		password.setScale(DETAILS_SCALE);

		Text slots = new Text(UIText.ARTIFACT_SLOTS.text());
		slots.setScale(DETAILS_SCALE);
		
		portNumber = new TextField(String.valueOf(JSONManager.setting.getPortNumber()), SKIN);
		portNumber.setMaxLength(5);
		portNumber.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

		serverPassword = new TextField(JSONManager.setting.getServerPassword(), SKIN);
		serverPassword.setMaxLength(20);

		playerCapacity = new SelectBox<>(SKIN);
		playerCapacity.setItems(CAPACITY_CHOICES);
		playerCapacity.setWidth(100);
		playerCapacity.setSelectedIndex(JSONManager.setting.getMaxPlayers());

		artifactSlots = new SelectBox<>(SKIN);
		artifactSlots.setItems(ARTIFACT_CHOICES);
		artifactSlots.setSelectedIndex(JSONManager.setting.getArtifactSlots());
		
		details.add(maxPlayers);
		details.add(playerCapacity).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(port);
		details.add(portNumber).colspan(2).width(100).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(password);
		details.add(serverPassword).colspan(2).width(100).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(slots);
		details.add(artifactSlots).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
	}
	
	/**
	 * This is called whenever the player selects the MISC tab
	 */
	private void miscSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.MISC;
		
		details.add(new Text(UIText.MISC.text())).colspan(2).pad(TITLE_PAD).row();

		randomNameAlliteration = new CheckBox(UIText.NAME_ALLITERATION.text(), SKIN);
		randomNameAlliteration.setChecked(JSONManager.setting.isRandomNameAlliteration());
		
		consoleEnabled = new CheckBox(UIText.CONSOLE_ENABLED.text(), SKIN);
		consoleEnabled.setChecked(JSONManager.setting.isConsoleEnabled());
		
		verboseDeathMessage = new CheckBox(UIText.VERBOSE_DEATH_MESSAGE.text(), SKIN);
		verboseDeathMessage.setChecked(JSONManager.setting.isVerboseDeathMessage());
		
		multiplayerPause = new CheckBox(UIText.MULTIPLAYER_PAUSE.text(), SKIN);
		multiplayerPause.setChecked(JSONManager.setting.isMultiplayerPause());
		
		exportChatLog = new CheckBox(UIText.EXPORT_CHAT.text(), SKIN);
		exportChatLog.setChecked(JSONManager.setting.isExportChatLog());

		enableUPNP = new CheckBox(UIText.UPNP.text(), SKIN);
		enableUPNP.setChecked(JSONManager.setting.isEnableUPNP());

		hideHUD = new CheckBox(UIText.HIDE_HUD.text(), SKIN);
		hideHUD.setChecked(JSONManager.setting.isHideHUD());

		details.add(randomNameAlliteration).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(consoleEnabled).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(verboseDeathMessage).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(multiplayerPause).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(exportChatLog).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(enableUPNP).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
		details.add(hideHUD).colspan(2).height(DETAIL_HEIGHT).pad(DETAIL_PAD).row();
	}
	
	/**
	 * Save the player chosen settings of whichever tab they are editing
	 */
	private void saveSettings() {
		switch (currentTab) {
			case CONTROLS -> PlayerAction.saveKeys();
			case DISPLAY -> {
				boolean screenChanged = (JSONManager.setting.getResolution() != resolutionOptions.getSelectedIndex()
						|| JSONManager.setting.isFullscreen() != fullscreen.isChecked()
						|| JSONManager.setting.isVSync() != vsync.isChecked());

				JSONManager.setting.setResolution(resolutionOptions.getSelectedIndex());
				JSONManager.setting.setFramerate(framerateOptions.getSelectedIndex());
				JSONManager.setting.setFullscreen(fullscreen.isChecked());
				JSONManager.setting.setVsync(vsync.isChecked());
				JSONManager.setting.setAutoIconify(autoIconify.isChecked());
				JSONManager.setting.setDebugHitbox(debugHitbox.isChecked());
				JSONManager.setting.setDisplayNames(displayNames.isChecked());
				JSONManager.setting.setDisplayHp(displayHp.isChecked());
				JSONManager.setting.setMouseCameraTrack(mouseCameraTrack.isChecked());
				JSONManager.setting.setScreenShake(screenShake.isChecked());
				JSONManager.setting.setDisplay(app, playState, screenChanged);
				JSONManager.setting.saveSetting();
			}
			case AUDIO -> {
				JSONManager.setting.setSoundVolume(sound.getValue());
				JSONManager.setting.setMusicVolume(music.getValue());
				JSONManager.setting.setMasterVolume(master.getValue());
				JSONManager.setting.setHitsoundType(hitsoundOptions.getSelectedIndex());
				JSONManager.setting.setHitsoundVolume(hitsound.getValue());
				JSONManager.setting.setAudio();
				JSONManager.setting.saveSetting();
			}
			case SERVER -> {
				JSONManager.setting.setMaxPlayers(playerCapacity.getSelectedIndex());
				JSONManager.setting.setPortNumber(Integer.parseInt(portNumber.getText()));
				JSONManager.setting.setServerPassword(serverPassword.getText());
				JSONManager.setting.setArtifactSlots(artifactSlots.getSelectedIndex());
				JSONManager.setting.saveSetting();
			}
			case MISC -> {
				JSONManager.setting.setRandomNameAlliteration(randomNameAlliteration.isChecked());
				JSONManager.setting.setConsoleEnabled(consoleEnabled.isChecked());
				JSONManager.setting.setVerboseDeathMessage(verboseDeathMessage.isChecked());
				JSONManager.setting.setMultiplayerPause(multiplayerPause.isChecked());
				JSONManager.setting.setExportChatLog(exportChatLog.isChecked());
				JSONManager.setting.setEnableUPNP(enableUPNP.isChecked());
				JSONManager.setting.setHideHUD(hideHUD.isChecked());
				JSONManager.setting.saveSetting();
			}
		}
		updateSharedSettings();
	}
	
	/**
	 * Reset this tab's settings to the default values
	 */
	private void resetSettings() {
		PlayerAction.resetKeys();
		JSONManager.setting.resetDisplay();
		JSONManager.setting.setDisplay(app, playState, true);
		JSONManager.setting.resetAudio();
		JSONManager.setting.setAudio();
		JSONManager.setting.resetServer();
		JSONManager.setting.resetMisc();
		JSONManager.setting.saveSetting();
		updateSharedSettings();

		switch (currentTab) {
			case CONTROLS -> controlsSelected();
			case DISPLAY -> displaySelected();
			case MISC -> miscSelected();
			case AUDIO -> audioSelected();
			case SERVER -> serverSelected();
		}
	}
	
	/**
	 * This updates the settings that are visible from the score window
	 */
	private void updateSharedSettings() {
		JSONManager.sharedSetting = JSONManager.setting.generateSharedSetting();
		
		//the server should update their scoretable when settings are changed
		if (playState != null) {
			if (playState.isServer()) {
				playState.getScoreWindow().syncSettingTable();
			}
		}
	}

	private void transitionOut(Runnable runnable) {
		options.addAction(Actions.moveTo(OPTIONS_X, OPTIONS_Y, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.sequence(Actions.moveTo(DETAILS_X, DETAILS_Y, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
	}

	private void transitionIn() {
		options.addAction(Actions.moveTo(OPTIONS_X_ENABLED, OPTIONS_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.moveTo(DETAILS_X_ENABLED, DETAILS_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
	}

	/**
	 * This converts a keycode to a readable string
	 * @param keycode: key to read
	 * @return string to return
	 */
	public static String getKey(int keycode) {
		
		if (keycode == 0) {	return UIText.MOUSE_LEFT.text(); }
		if (keycode == 1) { return UIText.MOUSE_RIGHT.text(); }
		if (keycode == 2) {	return UIText.MOUSE_MIDDLE.text(); }
		if (keycode == -1000) {	return UIText.M_WHEEL_UP.text(); }
		if (keycode == 1000) { return UIText.M_WHEEL_DOWN.text(); }
		
		return Input.Keys.toString(keycode);
	}
	
	@Override
	public void update(float delta) {
		//The playstate underneath should have their camera focus and ui act (letting dialog appear + disappear)
		if (peekState != null) {
			peekState.update(delta);
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
			transitionOut(() -> {
				StateManager.removeState(SettingState.class, false);
				StateManager.removeState(PauseState.class);
			});
		}
	}
	
	@Override
	public void render(float delta) {
		//Render the playstate and playstate ui underneath
		if (playState != null) {
			playState.render(delta);
			playState.stage.getViewport().apply();
			playState.stage.draw();
		} else {
			peekState.render(delta);
			peekState.stage.getViewport().apply();
			peekState.stage.act();
			peekState.stage.draw();
		}
	}
	
	//This is called when the setting state is designated to be removed. (if another player unpauses)
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }
		
	@Override
	public void dispose() { 
		stage.dispose();
	}
	
	public PlayState getPlayState() { return playState; }
	
	@Override
	public boolean processTransitions() {
		return peekState.processTransitions();
	}
	
	public enum settingTab {
		DISPLAY,
		CONTROLS,
		AUDIO,
		SERVER,
		MISC,
	}
}
