package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.text.TooltipManager;

import static com.mygdx.hadal.utils.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION;

/**
 * The Setting State allows the player to change their display settings, key bindings and other stuff like that.
 * @author Shorfutticus Squonzworth
 */
public class SettingState extends GameState {

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
		
	//Dimensions of the setting menu
	private static final int optionsX = -1025;
	private static final int optionsY = 100;
	private static final int optionsXEnabled = 25;
	private static final int optionsYEnabled = 100;
	private static final int optionsWidth = 300;
	private static final int optionsHeight = 600;
	
	private static final int detailsX = -730;
	private static final int detailsY = 20;
	private static final int detailsXEnabled = 320;
	private static final int detailsYEnabled = 20;
	private static final int detailsWidth = 500;
	private static final int detailsHeight = 680;
	private static final int scrollWidth = 480;

	private static final float optionsScale = 0.5f;
	private static final float optionHeight = 35.0f;
	private static final float optionPad = 10.0f;
	private static final float detailsScale = 0.25f;

	private static final float titlePad = 25.0f;
	private static final float detailHeight = 35.0f;
	private static final float detailPad = 10.0f;

	//These options make it easier to convert the index setting to a displayed string
	public static final String[] artifactChoices = {"0", "1", "2", "3", "4", "5", "6"};
	public static final String[] capacityChoices = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	
	//this is the current setting tab the player is using
	private settingTab currentTab;

	/**
	 * Constructor will be called when the player enters the setting state from the title menu or the pause menu.
	 */
	public SettingState(GameStateManager gsm, GameState peekState) {
		super(gsm);
		this.peekState = peekState;
		
		if (peekState instanceof PauseState pauseState) {
			playState = pauseState.getPs();
		}
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				options = new WindowTable();
				options.setPosition(optionsX, optionsY);
				options.setSize(optionsWidth, optionsHeight);
				options.top();
				addActor(options);
				
				details = new WindowTable();
				details.setPosition(detailsX, detailsY);
				details.setSize(detailsWidth, detailsHeight);
				details.top();
				addActor(details);
				
				displayOption = new Text(HText.DISPLAY.text()).setButton(true);
				displayOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						saveSettings();
						displaySelected();
			        }
			    });
				displayOption.setScale(optionsScale);
				
				controlOption = new Text(HText.CONTROLS.text()).setButton(true);
				controlOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						saveSettings();
						controlsSelected();
			        }
			    });
				controlOption.setScale(optionsScale);
				
				audioOption = new Text(HText.AUDIO.text()).setButton(true);
				audioOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						saveSettings();
						audioSelected();
			        }
			    });
				audioOption.setScale(optionsScale);
				
				serverOption = new Text(HText.SERVER.text()).setButton(true);
				serverOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						saveSettings();
						serverSelected();
			        }
			    });
				serverOption.setScale(optionsScale);
				
				miscOption = new Text(HText.MISC.text()).setButton(true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						saveSettings();
						miscSelected();
			        }
			    });
				miscOption.setScale(optionsScale);

				exitOption = new Text(HText.RETURN.text()).setButton(true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
						saveSettings();

						//if exiting to title screen, play transition. Otherwise, just remove this state
						transitionOut(() -> gsm.removeState(SettingState.class));
			        }
			    });
				exitOption.setScale(optionsScale);

				resetOption = new Text(HText.RESET.text()).setButton(true);
				resetOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
						resetSettings();
			        }
			    });
				resetOption.setScale(optionsScale);
				
				options.add(displayOption).height(optionHeight).pad(optionPad).row();
				options.add(controlOption).height(optionHeight).pad(optionPad).row();
				options.add(audioOption).height(optionHeight).pad(optionPad).row();
				options.add(serverOption).height(optionHeight).pad(optionPad).row();
				options.add(miscOption).height(optionHeight).pad(optionPad).row();
				options.add(resetOption).height(optionHeight).pad(optionPad).row();
				options.add(exitOption).height(optionHeight).pad(optionPad).expand().row();
			}
		};
		app.newMenu(stage);
		
		transitionIn();

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

			//This is just a janky way of implementing setting mouse wheel as a hotkey.
			@Override
			public boolean scrolled(float amountX, float amountY) {	return keyDown((int) amountY * 1000); }
		});
		inputMultiplexer.addProcessor(Gdx.input.getInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		//start off with display selected unless this menu is called from lobby
		if (gsm.getStates().size() > 1) {
			if (gsm.getStates().get(gsm.getStates().size() - 2) instanceof LobbyState) {
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
		
		details.add(new Text(HText.DISPLAY.text())).colspan(2).pad(titlePad).row();
		
		Text screen = new Text(HText.RESOLUTION.text());
		screen.setScale(detailsScale);
		
		resolutionOptions = new SelectBox<>(GameStateManager.getSkin());
		resolutionOptions.setItems(HText.RESOLUTION_OPTIONS.text().split(","));
		resolutionOptions.setWidth(100);
		
		resolutionOptions.setSelectedIndex(gsm.getSetting().getResolution());

		Text framerate = new Text(HText.FRAMERATE.text());
		framerate.setScale(detailsScale);
		
		Text cursortype = new Text(HText.CURSOR_TYPE.text());
		cursortype.setScale(detailsScale);
		
		Text cursorsize = new Text(HText.CURSOR_SIZE.text());
		cursorsize.setScale(detailsScale);
		
		Text cursorcolor = new Text(HText.CURSOR_COLOR.text());
		cursorcolor.setScale(detailsScale);

		framerateOptions = new SelectBox<>(GameStateManager.getSkin());
		framerateOptions.setItems(HText.FRAMERATE_OPTIONS.text().split(","));
		
		framerateOptions.setSelectedIndex(gsm.getSetting().getFramerate());

		ChangeListener cursorChange = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gsm.getSetting().setCursorType(cursorOptions.getSelectedIndex());
				gsm.getSetting().setCursorSize(cursorSize.getSelectedIndex());
				gsm.getSetting().setCursorColor(cursorColor.getSelectedIndex());
				gsm.getSetting().setCursor();
			}
		};

		ChangeListener displayChange = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				saveSettings();
				gsm.getSetting().setDisplay(gsm.getApp(), playState);
			}
		};

		cursorOptions = new SelectBox<>(GameStateManager.getSkin());
		cursorOptions.setItems(HText.CURSOR_TYPE_OPTIONS.text().split(","));
		cursorOptions.setSelectedIndex(gsm.getSetting().getCursorType());
		cursorOptions.addListener(cursorChange);

		cursorSize = new SelectBox<>(GameStateManager.getSkin());
		cursorSize.setItems(HText.CURSOR_SIZE_OPTIONS.text().split(","));
		cursorSize.setSelectedIndex(gsm.getSetting().getCursorSize());
		cursorSize.addListener(cursorChange);

		cursorColor = new SelectBox<>(GameStateManager.getSkin());
		cursorColor.setItems(HText.CURSOR_COLOR_OPTIONS.text().split(","));
		cursorColor.setSelectedIndex(gsm.getSetting().getCursorColor());
		cursorColor.addListener(cursorChange);

		fullscreen = new CheckBox(HText.FULLSCREEN.text(), GameStateManager.getSkin());
		vsync = new CheckBox(HText.VSYNC.text(), GameStateManager.getSkin());
		autoIconify = new CheckBox(HText.ALT_TAB.text(), GameStateManager.getSkin());
		debugHitbox = new CheckBox(HText.DEBUG_OUTLINES.text(), GameStateManager.getSkin());
		displayNames = new CheckBox(HText.VISIBLE_NAMES.text(), GameStateManager.getSkin());
		displayHp = new CheckBox(HText.VISIBLE_HP.text(), GameStateManager.getSkin());
		mouseCameraTrack = new CheckBox(HText.CAMERA_AIM.text(), GameStateManager.getSkin());
		screenShake = new CheckBox(HText.SCREEN_SHAKE.text(), GameStateManager.getSkin());

		TooltipManager.addTooltip(mouseCameraTrack, HText.CAMERA_AIM_DESC.text());

		fullscreen.setChecked(gsm.getSetting().isFullscreen());
		vsync.setChecked(gsm.getSetting().isVSync());
		autoIconify.setChecked(gsm.getSetting().isAutoIconify());
		debugHitbox.setChecked(gsm.getSetting().isDebugHitbox());
		displayNames.setChecked(gsm.getSetting().isDisplayNames());
		displayHp.setChecked(gsm.getSetting().isDisplayHp());
		mouseCameraTrack.setChecked(gsm.getSetting().isMouseCameraTrack());
		screenShake.setChecked(gsm.getSetting().isScreenShake());

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
		details.add(resolutionOptions).height(detailHeight).pad(detailPad).row();
		details.add(framerate);
		details.add(framerateOptions).height(detailHeight).pad(detailPad).row();
		details.add(fullscreen);
		details.add(vsync).height(detailHeight).pad(detailPad).row();
		details.add(autoIconify);
		details.add(debugHitbox).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(displayNames);
		details.add(displayHp).height(detailHeight).pad(detailPad).row();
		details.add(cursortype);
		details.add(cursorOptions).height(detailHeight).pad(detailPad).row();
		details.add(cursorsize);
		details.add(cursorSize).height(detailHeight).pad(detailPad).row();
		details.add(cursorcolor);
		details.add(cursorColor).height(detailHeight).pad(detailPad).row();
		details.add(mouseCameraTrack);
		details.add(screenShake).height(detailHeight).pad(detailPad).row();
	}
	
	/**
	 * This is called whenever the player selects the CONTROLS tab
	 */
	private void controlsSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.CONTROLS;
		
		details.add(new Text(HText.CONTROLS.text())).pad(titlePad).row();
		
		VerticalGroup actions = new VerticalGroup().space(optionPad);
		
		for (PlayerAction a : PlayerAction.values()) {
			
			final PlayerAction action = a;
			Text actionChoose = new Text(a.getText() + ":==   " + getKey(a.getKey())).setButton(true);
			
			actionChoose.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent e, float x, float y) {
					
					//Clicking any option will highlight it and designate it as the next to update.
					((Text) e.getListenerActor()).setText(action + ":==   " + getKey(action.getKey()) + " <--");
					currentlyEditing = action;
					actionChoose.setHeight(detailHeight);
				}
			});
			actionChoose.setScale(detailsScale);
			actionChoose.setHeight(detailHeight);

			actions.addActor(actionChoose);
		}
		
		if (keybinds != null) {
			keybinds.remove();
		}
		
		keybinds = new ScrollPane(actions, GameStateManager.getSkin());
		keybinds.setSize(detailsWidth, detailsHeight);
		keybinds.setFadeScrollBars(false);
		
		details.add(keybinds).width(scrollWidth).expandY().pad(optionPad);
		stage.setScrollFocus(keybinds);
	}
	
	/**
	 * This is called whenever the player selects the AUDIO tab
	 */
	private void audioSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.AUDIO;
		
		details.add(new Text(HText.AUDIO .text())).colspan(2).pad(titlePad).row();
		
		Text soundText = new Text(HText.SOUND_VOLUME.text(Integer.toString((int)(gsm.getSetting().getSoundVolume() * 100))));
		soundText.setScale(detailsScale);
		
		sound = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		sound.setValue(gsm.getSetting().getSoundVolume());
		
		sound.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundText.setText(HText.SOUND_VOLUME.text(Integer.toString(((int)(sound.getValue() * 100)))));
			}
		});

		sound.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					gsm.getSetting().indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(sound.getValue() * master.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text musicText = new Text(HText.MUSIC_VOLUME.text(Integer.toString((int)(gsm.getSetting().getMusicVolume() * 100))));
		musicText.setScale(detailsScale);
		
		music = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		music.setValue(gsm.getSetting().getMusicVolume());
		
		music.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				musicText.setText(HText.MUSIC_VOLUME.text(Integer.toString((int)((music.getValue() * 100)))));
			}
		});

		music.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				gsm.getSetting().setMusicVolume(music.getValue() * master.getValue());
				gsm.getSetting().setAudio();
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text masterText = new Text(HText.MASTER_VOLUME.text(Integer.toString((int)(gsm.getSetting().getMasterVolume() * 100))));
		masterText.setScale(detailsScale);
		
		master = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		master.setValue(gsm.getSetting().getMasterVolume());
		
		master.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				masterText.setText(HText.MASTER_VOLUME.text(Integer.toString((int)(master.getValue() * 100))));
			}
		});

		master.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting master volume, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					gsm.getSetting().indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(sound.getValue() * master.getValue());
				}

				gsm.getSetting().setMusicVolume(master.getValue());
				gsm.getSetting().setAudio();
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		Text hitsoundText = new Text(HText.HITSOUND.text());
		hitsoundText.setScale(0.25f);
		
		hitsoundOptions = new SelectBox<>(GameStateManager.getSkin());
		hitsoundOptions.setItems(HText.HITSOUND_OPTIONS.text().split(","));
		hitsoundOptions.setSelectedIndex(gsm.getSetting().getHitsound());
		
		hitsoundOptions.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					gsm.getSetting().indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(hitsound.getValue() * master.getValue());
				}
			}
		});
		
		Text hitsoundVolumeText = new Text(HText.HITSOUND_VOLUME.text(Integer.toString((int)(gsm.getSetting().getHitsoundVolume() * 100))));
		hitsoundVolumeText.setScale(detailsScale);
		
		hitsound = new Slider(0.0f, 1.0f, 0.01f, false, GameStateManager.getSkin());
		hitsound.setValue(gsm.getSetting().getHitsoundVolume());
		hitsound.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hitsoundVolumeText.setText(HText.HITSOUND_VOLUME.text(Integer.toString((int)(hitsound.getValue() * 100))));
			}
		});

		hitsound.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//when selecting a hitsound, we want to play an example for the player
				if (hitsoundOptions.getSelectedIndex() != 0) {
					gsm.getSetting().indexToHitsound(
						hitsoundOptions.getSelectedIndex()).playNoModifiers(hitsound.getValue() * master.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		details.add(soundText);
		details.add(sound).height(detailHeight).pad(detailPad).row();
		details.add(musicText);
		details.add(music).height(detailHeight).pad(detailPad).row();
		details.add(masterText);
		details.add(master).height(detailHeight).pad(detailPad).row();
		details.add(hitsoundText);
		details.add(hitsoundOptions).height(detailHeight).pad(detailPad).row();
		details.add(hitsoundVolumeText);
		details.add(hitsound).height(detailHeight).pad(detailPad).row();
	}
	
	/**
	 * This is called whenever the player selects the GAME tab
	 */
	private void serverSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.SERVER;
		
		details.add(new Text(HText.SERVER.text())).colspan(2).pad(titlePad).row();

		Text maxPlayers = new Text(HText.SERVER_SIZE.text());
		maxPlayers.setScale(detailsScale);

		Text port = new Text(HText.PORT_NUMBER.text());
		port.setScale(detailsScale);

		Text password = new Text(HText.SERVER_PASSWORD.text());
		password.setScale(detailsScale);

		Text slots = new Text(HText.ARTIFACT_SLOTS.text());
		slots.setScale(detailsScale);
		
		portNumber = new TextField(String.valueOf(gsm.getSetting().getPortNumber()), GameStateManager.getSkin());
		portNumber.setMaxLength(5);
		portNumber.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

		serverPassword = new TextField(gsm.getSetting().getServerPassword(), GameStateManager.getSkin());
		serverPassword.setMaxLength(20);

		playerCapacity = new SelectBox<>(GameStateManager.getSkin());
		playerCapacity.setItems(capacityChoices);
		playerCapacity.setWidth(100);
		playerCapacity.setSelectedIndex(gsm.getSetting().getMaxPlayers());

		artifactSlots = new SelectBox<>(GameStateManager.getSkin());
		artifactSlots.setItems(artifactChoices);
		artifactSlots.setSelectedIndex(gsm.getSetting().getArtifactSlots());
		
		details.add(maxPlayers);
		details.add(playerCapacity).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(port);
		details.add(portNumber).colspan(2).width(100).height(detailHeight).pad(detailPad).row();
		details.add(password);
		details.add(serverPassword).colspan(2).width(100).height(detailHeight).pad(detailPad).row();
		details.add(slots);
		details.add(artifactSlots).height(detailHeight).pad(detailPad).row();
	}
	
	/**
	 * This is called whenever the player selects the MISC tab
	 */
	private void miscSelected() {
		details.clearChildren();
		currentlyEditing = null;
		currentTab = settingTab.MISC;
		
		details.add(new Text(HText.MISC.text())).colspan(2).pad(titlePad).row();

		randomNameAlliteration = new CheckBox(HText.NAME_ALLITERATION.text(), GameStateManager.getSkin());
		randomNameAlliteration.setChecked(gsm.getSetting().isRandomNameAlliteration());
		
		consoleEnabled = new CheckBox(HText.CONSOLE_ENABLED.text(), GameStateManager.getSkin());
		consoleEnabled.setChecked(gsm.getSetting().isConsoleEnabled());
		
		verboseDeathMessage = new CheckBox(HText.VERBOSE_DEATH_MESSAGE.text(), GameStateManager.getSkin());
		verboseDeathMessage.setChecked(gsm.getSetting().isVerboseDeathMessage());
		
		multiplayerPause = new CheckBox(HText.MULTIPLAYER_PAUSE.text(), GameStateManager.getSkin());
		multiplayerPause.setChecked(gsm.getSetting().isMultiplayerPause());
		
		exportChatLog = new CheckBox(HText.EXPORT_CHAT.text(), GameStateManager.getSkin());
		exportChatLog.setChecked(gsm.getSetting().isExportChatLog());

		enableUPNP = new CheckBox(HText.UPNP.text(), GameStateManager.getSkin());
		enableUPNP.setChecked(gsm.getSetting().isEnableUPNP());

		hideHUD = new CheckBox(HText.HIDE_HUD.text(), GameStateManager.getSkin());
		hideHUD.setChecked(gsm.getSetting().isHideHUD());

		details.add(randomNameAlliteration).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(consoleEnabled).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(verboseDeathMessage).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(multiplayerPause).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(exportChatLog).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(enableUPNP).colspan(2).height(detailHeight).pad(detailPad).row();
		details.add(hideHUD).colspan(2).height(detailHeight).pad(detailPad).row();
	}
	
	/**
	 * Save the player chosen settings of whichever tab they are editing
	 */
	private void saveSettings() {
		switch (currentTab) {
			case CONTROLS -> PlayerAction.saveKeys();
			case DISPLAY -> {
				gsm.getSetting().setResolution(resolutionOptions.getSelectedIndex());
				gsm.getSetting().setFramerate(framerateOptions.getSelectedIndex());
				gsm.getSetting().setFullscreen(fullscreen.isChecked());
				gsm.getSetting().setVsync(vsync.isChecked());
				gsm.getSetting().setAutoIconify(autoIconify.isChecked());
				gsm.getSetting().setDebugHitbox(debugHitbox.isChecked());
				gsm.getSetting().setDisplayNames(displayNames.isChecked());
				gsm.getSetting().setDisplayHp(displayHp.isChecked());
				gsm.getSetting().setMouseCameraTrack(mouseCameraTrack.isChecked());
				gsm.getSetting().setScreenShake(screenShake.isChecked());
				gsm.getSetting().setDisplay(gsm.getApp(), playState);
				gsm.getSetting().saveSetting();
			}
			case AUDIO -> {
				gsm.getSetting().setSoundVolume(sound.getValue());
				gsm.getSetting().setMusicVolume(music.getValue());
				gsm.getSetting().setMasterVolume(master.getValue());
				gsm.getSetting().setHitsoundType(hitsoundOptions.getSelectedIndex());
				gsm.getSetting().setHitsoundVolume(hitsound.getValue());
				gsm.getSetting().setAudio();
				gsm.getSetting().saveSetting();
			}
			case SERVER -> {
				gsm.getSetting().setMaxPlayers(playerCapacity.getSelectedIndex());
				gsm.getSetting().setPortNumber(Integer.parseInt(portNumber.getText()));
				gsm.getSetting().setServerPassword(serverPassword.getText());
				gsm.getSetting().setArtifactSlots(artifactSlots.getSelectedIndex());
				gsm.getSetting().saveSetting();
			}
			case MISC -> {
				gsm.getSetting().setRandomNameAlliteration(randomNameAlliteration.isChecked());
				gsm.getSetting().setConsoleEnabled(consoleEnabled.isChecked());
				gsm.getSetting().setVerboseDeathMessage(verboseDeathMessage.isChecked());
				gsm.getSetting().setMultiplayerPause(multiplayerPause.isChecked());
				gsm.getSetting().setExportChatLog(exportChatLog.isChecked());
				gsm.getSetting().setEnableUPNP(enableUPNP.isChecked());
				gsm.getSetting().setHideHUD(hideHUD.isChecked());
				gsm.getSetting().saveSetting();
			}
		}
		updateSharedSettings();
	}
	
	/**
	 * Reset this tab's settings to the default values
	 */
	private void resetSettings() {
		PlayerAction.resetKeys();
		gsm.getSetting().resetDisplay();
		gsm.getSetting().setDisplay(gsm.getApp(), playState);
		gsm.getSetting().resetAudio();
		gsm.getSetting().setAudio();
		gsm.getSetting().resetServer();
		gsm.getSetting().resetMisc();
		gsm.getSetting().saveSetting();
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
		gsm.setSharedSetting(gsm.getSetting().generateSharedSetting());
		
		//the server should update their scoretable when settings are changed
		if (playState != null) {
			if (playState.isServer()) {
				playState.getScoreWindow().syncSettingTable();
			}
		}
	}

	private void transitionOut(Runnable runnable) {
		options.addAction(Actions.moveTo(optionsX, optionsY, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.sequence(Actions.moveTo(detailsX, detailsY, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
	}

	private void transitionIn() {
		options.addAction(Actions.moveTo(optionsXEnabled, optionsYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.moveTo(detailsXEnabled, detailsYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
	}

	/**
	 * This converts a keycode to a readable string
	 * @param keycode: key to read
	 * @return string to return
	 */
	public static String getKey(int keycode) {
		
		if (keycode == 0) {	return HText.MOUSE_LEFT.text(); }
		if (keycode == 1) { return HText.MOUSE_RIGHT.text(); }
		if (keycode == 2) {	return HText.MOUSE_MIDDLE.text(); }
		if (keycode == -1000) {	return HText.M_WHEEL_UP.text(); }
		if (keycode == 1000) { return HText.M_WHEEL_DOWN.text(); }
		
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
				gsm.removeState(SettingState.class, false);
				gsm.removeState(PauseState.class);
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
