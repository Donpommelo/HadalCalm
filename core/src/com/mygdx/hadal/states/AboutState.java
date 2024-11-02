package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.TableWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.text.TooltipManager;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.constants.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.constants.Constants.TRANSITION_DURATION;
import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * The AboutState is selected from the title screen and gives information about the game
 * @author Snujube Spambly
 */
public class AboutState extends GameState {

	//Dimensions of the setting menu
	private static final int OPTIONS_X = -1125;
	private static final int OPTIONS_Y = 100;
	private static final int OPTIONS_X_ENABLED = 25;
	private static final int OPTIONS_Y_ENABLED = 100;
	private static final int OPTIONS_WIDTH = 300;
	private static final int OPTIONS_HEIGHT = 600;

	private static final int DETAILS_X = -830;
	private static final int DETAILS_Y = 100;
	private static final int DETAILS_X_ENABLED = 320;
	private static final int DETAILS_Y_ENABLED = 100;
	private static final int DETAILS_WIDTH = 800;
	private static final int DETAILS_HEIGHT = 600;

	private static final float OPTIONS_SCALE = 0.5f;
	private static final float OPTION_HEIGHT = 35.0f;
	private static final float OPTION_PAD = 15.0f;
	private static final float DETAILS_SCALE = 0.3f;
	private static final float OPTION_PADDING = 10.0f;

	private static final float TITLE_PAD = 25.0f;
	private static final int DETAILS_TEXT_WIDTH = 750;

	private static final int SCROLL_HEIGHT = 380;
	private static final int SCROLL_WIDTH = 330;
	private static final float DETAIL_HEIGHT = 35.0f;
	private static final float DETAIL_PAD = 10.0f;
	private static final float SLIDER_WIDTH = 350.0f;
	private static final float SONG_TITLE_WIDTH = 350.0f;

	private static final float EXTRA_WIDTH = 400.0f;
	private static final int EXTRA_HEIGHT = 120;

	//This table contains the ui elements of the pause screen
	private Table options, details;
	private Text pause;
	private CheckBox continuePlaying;

	//ui elements that the player can view and interact with
	private Text aboutOption, miscOption, tipsOption, soundRoomOption, creditsOption, exitOption, trackText, trackTime;

	//These manage the sound room music playlist
	private ScrollPane musicTracks;
	private VerticalGroup tracks;
	private SelectBox<String> loopOptions;
	private Slider musicTime;
	private final Array<MusicTrack> shuffleTracks = new Array<>();
	private int currentTrackIndex;

	//this is the state underneath this state.
	private final GameState peekState;
	private PlayState playState;

	//This determines whether the pause state should be removed or not next engine tick.
	//We do this instead of removing right away in case we remove as a result of receiving a packet from another player unpausing (which can happen whenever).
	private boolean toRemove;

	/**
	 * Constructor will be called when the player enters the about state from the title menu.
	 */
	public AboutState(HadalGame app, GameState peekState) {
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

				soundRoomOption = new Text(UIText.SOUND_ROOM.text()).setButton(true);
				soundRoomOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.UISWITCH1);
						soundRoomSelected();
					}
				});
				soundRoomOption.setScale(OPTIONS_SCALE);

				aboutOption = new Text(UIText.ABOUT.text()).setButton(true);
				aboutOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.UISWITCH1);
						aboutSelected();
			        }
			    });
				aboutOption.setScale(OPTIONS_SCALE);

				tipsOption = new Text(UIText.TIPS.text()).setButton(true);
				tipsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.UISWITCH1);
						tipsSelected();
			        }
			    });
				tipsOption.setScale(OPTIONS_SCALE);
				
				miscOption = new Text(UIText.MISC.text()).setButton(true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.UISWITCH1);
						miscSelected();
			        }
			    });
				miscOption.setScale(OPTIONS_SCALE);
				
				creditsOption = new Text(UIText.CREDITS.text()).setButton(true);
				creditsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.UISWITCH1);
						creditsSelected();
			        }
			    });
				creditsOption.setScale(OPTIONS_SCALE);
				
				exitOption = new Text(UIText.RETURN.text()).setButton(true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundManager.play(SoundEffect.NEGATIVE);
						transitionOut(() -> StateManager.removeState(AboutState.class));
			        }
			    });
				exitOption.setScale(OPTIONS_SCALE);

				options.add(soundRoomOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(aboutOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(tipsOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(miscOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(creditsOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				options.add(exitOption).height(OPTION_HEIGHT).pad(OPTION_PAD).expand().row();
			}
		};
		app.newMenu(stage);
		transitionIn();
		
		//start off with about selected
		soundRoomSelected();
	}

	private void aboutSelected() {
		details.clearChildren();

		//about option displays some game information read from json
		details.add(new Text(UIText.ABOUT.text())).colspan(2).pad(TITLE_PAD).row();
		Text about = new Text(UIText.INFO_ABOUT.text()).setWrap(DETAILS_TEXT_WIDTH);
		about.setScale(DETAILS_SCALE);
		
		details.add(about);
	}

	private void soundRoomSelected() {
		details.clearChildren();

		details.add(new Text(UIText.SOUND_ROOM.text())).colspan(2).pad(TITLE_PAD).row();
		trackText = new Text("");
		trackText.setScale(DETAILS_SCALE);

		//the slider sets its position based on the song duration
		musicTime = new Slider(0.0f, 0.0f, 1.0f, false, SKIN) {

			//hack necessary to set length
			@Override
			public float getPrefWidth() { return SLIDER_WIDTH; }

		};

		//this lets the player set music position by dragging slider
		musicTime.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (MusicPlayer.getCurrentSong() != null) {
					MusicPlayer.setMusicPosition(musicTime.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { return true; }
		});

		tracks = new VerticalGroup().space(OPTION_PADDING);

		//show list of available songs
		for (MusicTrack track : MusicTrack.values()) {
			Text trackListen = new Text(track.getMusicName()).setButton(true);

			//clicking a track plays it
			trackListen.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					SoundManager.play(SoundEffect.NEGATIVE);
					MusicPlayer.playSong(track, 1.0f);
					setTrack(track, true);
				}
			});

			trackListen.setScale(DETAILS_SCALE);
			trackListen.setHeight(DETAIL_HEIGHT);

			tracks.addActor(trackListen);
		}

		if (musicTracks != null) {
			musicTracks.remove();
		}

		musicTracks = new ScrollPane(tracks, SKIN);
		musicTracks.setFadeScrollBars(false);

		trackTime = new Text("");
		trackTime.setScale(DETAILS_SCALE);

		pause = new Text(UIText.PAUSE.text()).setButton(true);
		pause.setScale(DETAILS_SCALE);

		//pausing track sets toggles this button between pausing and playing music
		pause.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.NEGATIVE);
				if (MusicPlayer.getCurrentSong() != null) {
					if (MusicPlayer.getCurrentSong().isPlaying()) {
						MusicPlayer.pause();
						pause.setText(UIText.PLAY.text());
					} else {
						MusicPlayer.getCurrentSong().setPosition(musicTime.getValue());
						MusicPlayer.play();
						pause.setText(UIText.PAUSE.text());
					}
					pause.setHeight(OPTION_HEIGHT);
				}
			}
		});

		Text stop = new Text (UIText.STOP.text()).setButton(true);
		stop.setScale(DETAILS_SCALE);

		//stop sets track to null
		stop.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.NEGATIVE);
				MusicPlayer.stop();
				setTrack(null, false);
			}
		});

		Text next = new Text (UIText.NEXT.text()).setButton(true);
		next.setScale(DETAILS_SCALE);

		//next sets the song position to the end of the track, making the next track immediately start
		next.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.NEGATIVE);

				if (MusicPlayer.getCurrentSong() != null) {
					MusicPlayer.getCurrentSong().setPosition(MusicPlayer.getCurrentTrack().getTrackLength());
				}
			}
		});

		//this checkbox controls whther the music should continue playing after exiting the sound room
		continuePlaying = new CheckBox(UIText.CONTINUE.text(), SKIN);
		TooltipManager.addTooltip(continuePlaying, UIText.CONTINUE_DESC.text());

		//loop options decide whether to loop, shuffle, cycle when a song completes
		loopOptions = new SelectBox<>(SKIN);
		loopOptions.setItems(UIText.LOOP_OPTIONS.text().split(","));
		loopOptions.setWidth(100);

		details.add(trackText).width(SONG_TITLE_WIDTH).pad(DETAIL_PAD);
		details.add(musicTime).height(EXTRA_HEIGHT).row();

		details.add(musicTracks).width(SCROLL_WIDTH).height(SCROLL_HEIGHT).expandY().pad(DETAIL_PAD);
		stage.setScrollFocus(musicTracks);

		final Table soundRoomExtra = new Table();

		details.add(soundRoomExtra).top().size(EXTRA_WIDTH, EXTRA_HEIGHT);
		soundRoomExtra.add(trackTime).colspan(2).height(OPTION_HEIGHT).pad(DETAIL_PAD).row();
		soundRoomExtra.add(pause).height(OPTION_HEIGHT).pad(DETAIL_PAD);
		soundRoomExtra.add(stop).height(OPTION_HEIGHT).pad(DETAIL_PAD);
		soundRoomExtra.add(next).height(OPTION_HEIGHT).pad(DETAIL_PAD).row();
		soundRoomExtra.add(continuePlaying).colspan(3).height(OPTION_HEIGHT).pad(DETAIL_PAD).row();
		soundRoomExtra.add(loopOptions).colspan(3).height(OPTION_HEIGHT).pad(DETAIL_PAD).row();

		MusicPlayer.setMusicState(MusicTrackType.NOTHING);
		setTrack(MusicPlayer.getCurrentTrack(), true);
	}

	private void tipsSelected() {
		details.clearChildren();

		//about option displays some gameplay tips read from json
		details.add(new Text(UIText.TIPS.text())).colspan(2).pad(TITLE_PAD).row();
		Text tips = new Text(UIText.INFO_TIPS.text()).setWrap(DETAILS_TEXT_WIDTH);
		tips.setScale(DETAILS_SCALE);
		
		details.add(tips);
	}
	
	private void miscSelected() {
		details.clearChildren();

		//about option displays some miscellaneous text read from json
		details.add(new Text(UIText.MISC.text())).colspan(2).pad(TITLE_PAD).row();
		Text misc = new Text(UIText.INFO_MISC.text()).setWrap(DETAILS_TEXT_WIDTH);
		misc.setScale(DETAILS_SCALE);
		
		details.add(misc);
	}

	private void creditsSelected() {
		details.clearChildren();

		//about option displays credit information read from json
		details.add(new Text(UIText.CREDITS.text())).colspan(2).pad(TITLE_PAD).row();
		
		//dev and art options have url links
		Text dev = new Text(UIText.INFO_CREDITS_CODE.text()).setButton(true);
		dev.setScale(DETAILS_SCALE);
		dev.setColor(Color.RED);
		
		dev.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.UISWITCH1);
				Gdx.net.openURI("https://donpommelo.itch.io/");
	        }
	    });
		
		Text art = new Text(UIText.INFO_CREDITS_ART.text()).setButton(true);
		art.setScale(DETAILS_SCALE);
		art.setColor(Color.RED);
		
		art.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.UISWITCH1);
				Gdx.net.openURI("https://www.instagram.com/shoebanfoo/");
	        }
	    });

		Text music = new Text(UIText.INFO_CREDITS_MUSIC.text()).setButton(true);
		music.setScale(DETAILS_SCALE);
		music.setColor(Color.RED);

		music.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundManager.play(SoundEffect.UISWITCH1);
				Gdx.net.openURI("https://soundcloud.com/vcrchitect/");
			}
		});

		Text sfx = new Text(UIText.INFO_CREDITS_SOUND.text()).setWrap(DETAILS_TEXT_WIDTH);
		sfx.setScale(DETAILS_SCALE);
		
		details.add(dev).height(OPTION_HEIGHT).row();
		details.add(art).height(OPTION_HEIGHT).row();
		details.add(music).height(OPTION_HEIGHT).row();
		details.add(sfx).height(OPTION_HEIGHT);
	}

	private void transitionOut(Runnable runnable) {
		options.addAction(Actions.moveTo(OPTIONS_X, OPTIONS_Y, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.sequence(Actions.moveTo(DETAILS_X, DETAILS_Y, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
	}

	private void transitionIn() {
		options.addAction(Actions.moveTo(OPTIONS_X_ENABLED, OPTIONS_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.moveTo(DETAILS_X_ENABLED, DETAILS_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
	}

	private boolean loopChecked;
	@Override
	public void update(float delta) {
		peekState.update(delta);

		//set music slider position if a song is playing
		if (musicTime != null && MusicPlayer.getCurrentSong() != null) {

			if (!musicTime.isDragging() && MusicPlayer.getCurrentSong().isPlaying()) {
				musicTime.setValue(MusicPlayer.getCurrentSong().getPosition());
			}
			trackTime.setText(secondsToMinutes((int) musicTime.getValue()) + " / " +
				secondsToMinutes(MusicPlayer.getCurrentTrack().getTrackLength()));

			if (musicTime.getValue() >= MusicPlayer.getCurrentTrack().getTrackLength()) {
				if (!loopChecked) {
					loopChecked = true;

					//when a song finishes, we may play a new song depending on the loop option selected
					switch (loopOptions.getSelectedIndex()) {
						case 1 -> {

							//when cycling, play the next song or thte first song if reaching the end of the list
							MusicTrack nextTrack = MusicTrack.values()[(currentTrackIndex + 1) % MusicTrack.values().length];
							MusicPlayer.playSong(nextTrack, 1.0f);
							setTrack(nextTrack, true);
						}
						case 2 -> {

							//when shuffling, add all songs to a list and shuffle
							if (shuffleTracks.isEmpty()) {
								shuffleTracks.addAll(MusicTrack.values());
							}

							//play a song from the random list and remove it to ensure cycling through all songs before reshuffling
							MusicTrack randomTrack = shuffleTracks.get(MathUtils.random(shuffleTracks.size- 1));
							MusicPlayer.playSong(randomTrack, 1.0f);
							setTrack(randomTrack, false);
							shuffleTracks.removeValue(randomTrack, false);
						}
						case 3 -> MusicPlayer.stop();
					}
				}
			} else {

				//loopChecked ensures we do not skip through multiple songs
				loopChecked = false;
			}
		}

		//If the state has been unpaused, remove it
		if (toRemove) {
			transitionOut(() -> {
				StateManager.removeState(AboutState.class, false);
				StateManager.removeState(PauseState.class);
			});
		}
	}

	@Override
	public void render(float delta) {
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

		//if we want to continue playing the song after closing, set music state to free
		if (continuePlaying != null) {
			if (continuePlaying.isChecked() && MusicPlayer.getCurrentSong() != null) {
				if (MusicPlayer.getCurrentSong().isPlaying()) {
					MusicPlayer.setMusicState(MusicTrackType.FREE);
				}
			}
		}
	}

	/**
	 * This sets the song room's currently played track
	 * @param track: track to play
	 * @param resetShuffle: do we reset the shuffle playlist when setting this track?
	 */
	private void setTrack(MusicTrack track, boolean resetShuffle) {
		musicTime.setValue(0.0f);

		//pause/play text should be set to pause since a song is now playing
		pause.setText(UIText.PAUSE.text());
		pause.setHeight(OPTION_HEIGHT);
		if (track != null) {

			//iterate through all tracks to color the one playing yellow and all others as white.
			for (int i = 0; i < MusicTrack.values().length; i++) {
				if (MusicTrack.values()[i].equals(track)) {
					currentTrackIndex = i;
					if (tracks.getChildren().get(currentTrackIndex) != null) {
						Text songText = ((Text) tracks.getChildren().get(i));
						songText.setColor(Color.YELLOW);
						songText.setHeight(DETAIL_HEIGHT);

						//scroll to the newly set track
						int scrollIndex = Math.max(Math.min(i, MusicTrack.values().length - 4), 4) - 4;
						musicTracks.setScrollPercentY((float) scrollIndex / (MusicTrack.values().length - 9));
					}
				} else {
					Text songText = ((Text) tracks.getChildren().get(i));
					songText.setColor(Color.WHITE);
					songText.setHeight(DETAIL_HEIGHT);
				}
			}
			musicTime.setRange(0.0f, track.getTrackLength());
			trackText.setText(UIText.NOW_PLAYING.text(track.getMusicName()));

			//if resetting shuffle, clear and readd all tracks to it except the newly set track
			if (resetShuffle) {
				shuffleTracks.clear();
				shuffleTracks.addAll(MusicTrack.values());
				shuffleTracks.removeValue(track, false);
			}
		} else {
			musicTime.setRange(0.0f, 0.0f);
			trackText.setText(UIText.NOW_PLAYING_DEFAULT.text());
		}
	}

	/**
	 * @param seconds amount of seconds
	 * @return string representation of minutes:seconds to have constant character length
	 */
	private String secondsToMinutes(int seconds) {

		int m = seconds / 60;
		int s = seconds % 60;

		if (s < 10) {
			return m + ":0" + s;
		} else {
			return m + ":" + s;
		}
	}

	public PlayState getPlayState() { return playState; }
}
