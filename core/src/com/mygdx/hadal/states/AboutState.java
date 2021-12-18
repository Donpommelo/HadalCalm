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
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.text.HText;

import static com.mygdx.hadal.utils.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION;

/**
 * The AboutState is selected from the title screen and gives information about the game
 * @author Snujube Spambly
 */
public class AboutState extends GameState {

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

	//Dimensions of the setting menu
	private static final int optionsX = -1125;
	private static final int optionsY = 100;
	private static final int optionsXEnabled = 25;
	private static final int optionsYEnabled = 100;
	private static final int optionsWidth = 300;
	private static final int optionsHeight = 600;

	private static final int detailsX = -830;
	private static final int detailsY = 100;
	private static final int detailsXEnabled = 320;
	private static final int detailsYEnabled = 100;
	private static final int detailsWidth = 800;
	private static final int detailsHeight = 600;
	
	private static final float optionsScale = 0.5f;
	private static final float optionHeight = 35.0f;
	private static final float optionPad = 15.0f;
	private static final float detailsScale = 0.3f;
	private static final float optionPadding = 10.0f;

	private static final float titlePad = 25.0f;
	private static final int detailsTextWidth = 750;

	private static final int scrollHeight = 380;
	private static final int scrollWidth = 330;
	private static final float detailHeight = 35.0f;
	private static final float detailPad = 10.0f;
	private static final float sliderWidth = 350.0f;
	private static final float songTitleWidth = 350.0f;

	private static final float extraWidth = 400.0f;
	private static final int extraHeight = 120;

	//this is the state underneath this state.
	private final GameState peekState;
	private PlayState playState;

	//This determines whether the pause state should be removed or not next engine tick.
	//We do this instead of removing right away in case we remove as a result of receiving a packet from another player unpausing (which can happen whenever).
	private boolean toRemove;

	/**
	 * Constructor will be called when the player enters the about state from the title menu.
	 */
	public AboutState(final GameStateManager gsm, GameState peekState) {
		super(gsm);
		this.peekState = peekState;

		if (peekState instanceof PauseState) {
			playState = ((PauseState) peekState).getPs();
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

				soundRoomOption = new Text(HText.SOUND_ROOM.text(), 0, 0, true);
				soundRoomOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						soundRoomSelected();
					}
				});
				soundRoomOption.setScale(optionsScale);

				aboutOption = new Text(HText.ABOUT.text(), 0, 0, true);
				aboutOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						aboutSelected();
			        }
			    });
				aboutOption.setScale(optionsScale);

				tipsOption = new Text(HText.TIPS.text(), 0, 0, true);
				tipsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						tipsSelected();
			        }
			    });
				tipsOption.setScale(optionsScale);
				
				miscOption = new Text(HText.MISC.text(), 0, 0, true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						miscSelected();
			        }
			    });
				miscOption.setScale(optionsScale);
				
				creditsOption = new Text(HText.CREDITS.text(), 0, 0, true);
				creditsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						creditsSelected();
			        }
			    });
				creditsOption.setScale(optionsScale);
				
				exitOption = new Text(HText.RETURN.text(), 0, 0, true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
						transitionOut(() -> gsm.removeState(AboutState.class));
			        }
			    });
				exitOption.setScale(optionsScale);

				options.add(soundRoomOption).height(optionHeight).pad(optionPad).row();
				options.add(aboutOption).height(optionHeight).pad(optionPad).row();
				options.add(tipsOption).height(optionHeight).pad(optionPad).row();
				options.add(miscOption).height(optionHeight).pad(optionPad).row();
				options.add(creditsOption).height(optionHeight).pad(optionPad).row();
				options.add(exitOption).height(optionHeight).pad(optionPad).expand().row();
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
		details.add(new Text(HText.ABOUT.text(), 0, 0, false)).colspan(2).pad(titlePad).row();
		Text about = new Text(HText.INFO_ABOUT.text(), 0, 0, false, true, detailsTextWidth);
		about.setScale(detailsScale);
		
		details.add(about);
	}

	private void soundRoomSelected() {
		details.clearChildren();

		details.add(new Text(HText.SOUND_ROOM.text(), 0, 0, false)).colspan(2).pad(titlePad).row();
		trackText = new Text("", 0, 0, false);
		trackText.setScale(detailsScale);

		//the slider sets its position based on the song duration
		musicTime = new Slider(0.0f, 0.0f, 1.0f, false, GameStateManager.getSkin()) {

			//hack necessary to set length
			@Override
			public float getPrefWidth() { return sliderWidth; }

		};

		//this lets the player set music position by dragging slider
		musicTime.addListener(new InputListener() {

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (HadalGame.musicPlayer.getCurrentSong() != null) {
					HadalGame.musicPlayer.setMusicPosition(musicTime.getValue());
				}
			}

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { return true; }
		});

		tracks = new VerticalGroup().space(optionPadding);

		//show list of available songs
		for (MusicTrack track: MusicTrack.values()) {
			Text trackListen = new Text(track.getMusicName(), 0, 0, true);

			//clicking a track plays it
			trackListen.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
					HadalGame.musicPlayer.playSong(track, 1.0f);
					setTrack(track, true);
				}
			});

			trackListen.setScale(detailsScale);
			trackListen.setHeight(detailHeight);

			tracks.addActor(trackListen);
		}

		if (musicTracks != null) {
			musicTracks.remove();
		}

		musicTracks = new ScrollPane(tracks, GameStateManager.getSkin());
		musicTracks.setFadeScrollBars(false);

		trackTime = new Text("", 0, 0, false);
		trackTime.setScale(detailsScale);

		pause = new Text(HText.PAUSE.text(), 0, 0, true);
		pause.setScale(detailsScale);

		//pausing track sets toggles this button between pausing and playing music
		pause.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
				if (HadalGame.musicPlayer.getCurrentSong() != null) {
					if (HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
						HadalGame.musicPlayer.pause();
						pause.setText(HText.PLAY.text());
					} else {
						HadalGame.musicPlayer.getCurrentSong().setPosition(musicTime.getValue());
						HadalGame.musicPlayer.play();
						pause.setText(HText.PAUSE.text());
					}
					pause.setHeight(optionHeight);
				}
			}
		});

		Text stop = new Text (HText.STOP.text(), 0, 0, true);
		stop.setScale(detailsScale);

		//stop sets track to null
		stop.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
				HadalGame.musicPlayer.stop();
				setTrack(null, false);
			}
		});

		Text next = new Text (HText.NEXT.text(), 0, 0, true);
		next.setScale(detailsScale);

		//next sets the song position to the end of the track, making the next track immediately start
		next.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

				if (HadalGame.musicPlayer.getCurrentSong() != null) {
					HadalGame.musicPlayer.getCurrentSong().setPosition(HadalGame.musicPlayer.getCurrentTrack().getTrackLength());
				}
			}
		});

		//this checkbox controls whther the music should continue playing after exiting the sound room
		continuePlaying = new CheckBox(HText.CONTINUE.text(), GameStateManager.getSkin());

		//loop options decide whether to loop, shuffle, cycle when a song completes
		loopOptions = new SelectBox<>(GameStateManager.getSkin());
		loopOptions.setItems(HText.LOOP_OPTIONS.text().split(","));
		loopOptions.setWidth(100);

		details.add(trackText).width(songTitleWidth).pad(detailPad);
		details.add(musicTime).height(extraHeight).row();

		details.add(musicTracks).width(scrollWidth).height(scrollHeight).expandY().pad(detailPad);
		stage.setScrollFocus(musicTracks);

		final Table soundRoomExtra = new Table();

		details.add(soundRoomExtra).top().size(extraWidth, extraHeight);
		soundRoomExtra.add(trackTime).colspan(2).height(optionHeight).pad(detailPad).row();
		soundRoomExtra.add(pause).height(optionHeight).pad(detailPad);
		soundRoomExtra.add(stop).height(optionHeight).pad(detailPad);
		soundRoomExtra.add(next).height(optionHeight).pad(detailPad).row();
		soundRoomExtra.add(continuePlaying).colspan(3).height(optionHeight).pad(detailPad).row();
		soundRoomExtra.add(loopOptions).colspan(3).height(optionHeight).pad(detailPad).row();

		HadalGame.musicPlayer.setMusicState(MusicTrackType.NOTHING);
		setTrack(HadalGame.musicPlayer.getCurrentTrack(), true);
	}

	private void tipsSelected() {
		details.clearChildren();

		//about option displays some gameplay tips read from json
		details.add(new Text(HText.TIPS.text(), 0, 0, false)).colspan(2).pad(titlePad).row();
		Text tips = new Text(HText.INFO_TIPS.text(), 0, 0, false, true, detailsTextWidth);
		tips.setScale(detailsScale);
		
		details.add(tips);
	}
	
	private void miscSelected() {
		details.clearChildren();

		//about option displays some miscellaneous text read from json
		details.add(new Text(HText.MISC.text(), 0, 0, false)).colspan(2).pad(titlePad).row();
		Text misc = new Text(HText.INFO_MISC.text(), 0, 0, false, true, detailsTextWidth);
		misc.setScale(detailsScale);
		
		details.add(misc);
	}

	private void creditsSelected() {
		details.clearChildren();

		//about option displays credit information read from json
		details.add(new Text(HText.CREDITS.text(), 0, 0, false)).colspan(2).pad(titlePad).row();
		
		//dev and art options have url links
		Text dev = new Text(HText.INFO_CREDITS_CODE.text(), 0, 0, true);
		dev.setScale(detailsScale);
		dev.setColor(Color.RED);
		
		dev.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://donpommelo.itch.io/");
	        }
	    });
		
		Text art = new Text(HText.INFO_CREDITS_ART.text(), 0, 0, true);
		art.setScale(detailsScale);
		art.setColor(Color.RED);
		
		art.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://www.instagram.com/shoebanfoo/");
	        }
	    });

		Text music = new Text(HText.INFO_CREDITS_MUSIC.text(), 0, 0, true);
		music.setScale(detailsScale);
		music.setColor(Color.RED);

		music.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://soundcloud.com/vcrchitect/");
			}
		});

		Text sfx = new Text(HText.INFO_CREDITS_SOUND.text(), 0, 0, false, true, detailsTextWidth);
		sfx.setScale(detailsScale);
		
		details.add(dev).height(optionHeight).row();
		details.add(art).height(optionHeight).row();
		details.add(music).height(optionHeight).row();
		details.add(sfx).height(optionHeight);
	}

	private void transitionOut(Runnable runnable) {
		options.addAction(Actions.moveTo(optionsX, optionsY, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.sequence(Actions.moveTo(detailsX, detailsY, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
	}

	private void transitionIn() {
		options.addAction(Actions.moveTo(optionsXEnabled, optionsYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
		details.addAction(Actions.moveTo(detailsXEnabled, detailsYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
	}

	private boolean loopChecked;
	@Override
	public void update(float delta) {
		peekState.update(delta);

		//set music slider position if a song is playing
		if (musicTime != null && HadalGame.musicPlayer.getCurrentSong() != null) {

			if (!musicTime.isDragging() && HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
				musicTime.setValue(HadalGame.musicPlayer.getCurrentSong().getPosition());
			}
			trackTime.setText(secondsToMinutes((int) musicTime.getValue()) + " / " +
				secondsToMinutes(HadalGame.musicPlayer.getCurrentTrack().getTrackLength()));

			if (musicTime.getValue() >= HadalGame.musicPlayer.getCurrentTrack().getTrackLength()) {
				if (!loopChecked) {
					loopChecked = true;

					//when a song finishes, we may play a new song depending on the loop option selected
					switch (loopOptions.getSelectedIndex()) {
						case 1 -> {

							//when cycling, play the next song or thte first song if reaching the end of the list
							MusicTrack nextTrack = MusicTrack.values()[(currentTrackIndex + 1) % MusicTrack.values().length];
							HadalGame.musicPlayer.playSong(nextTrack, 1.0f);
							setTrack(nextTrack, true);
						}
						case 2 -> {

							//when shuffling, add all songs to a list and shuffle
							if (shuffleTracks.isEmpty()) {
								shuffleTracks.addAll(MusicTrack.values());
							}

							//play a song from the random list and remove it to ensure cycling through all songs before reshuffling
							MusicTrack randomTrack = shuffleTracks.get(MathUtils.random(shuffleTracks.size- 1));
							HadalGame.musicPlayer.playSong(randomTrack, 1.0f);
							setTrack(randomTrack, false);
							shuffleTracks.removeValue(randomTrack, false);
						}
						case 3 -> HadalGame.musicPlayer.stop();
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
				gsm.removeState(AboutState.class, false);
				gsm.removeState(PauseState.class);
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
			if (continuePlaying.isChecked() && HadalGame.musicPlayer.getCurrentSong() != null) {
				if (HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
					HadalGame.musicPlayer.setMusicState(MusicTrackType.FREE);
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
		pause.setText(HText.PAUSE.text());
		pause.setHeight(optionHeight);
		if (track != null) {

			//iterate through all tracks to color the one playing yellow and all others as white.
			for (int i = 0; i < MusicTrack.values().length; i++) {
				if (MusicTrack.values()[i].equals(track)) {
					currentTrackIndex = i;
					if (tracks.getChildren().get(currentTrackIndex) != null) {
						Text songText = ((Text) tracks.getChildren().get(i));
						songText.setColor(Color.YELLOW);
						songText.setHeight(detailHeight);

						//scroll to the newly set track
						int scrollIndex = Math.max(Math.min(i, MusicTrack.values().length - 4), 4) - 4;
						musicTracks.setScrollPercentY((float) scrollIndex / (MusicTrack.values().length - 9));
					}
				} else {
					Text songText = ((Text) tracks.getChildren().get(i));
					songText.setColor(Color.WHITE);
					songText.setHeight(detailHeight);
				}
			}
			musicTime.setRange(0.0f, track.getTrackLength());
			trackText.setText(HText.NOW_PLAYING.text(track.getMusicName()));

			//if resetting shuffle, clear and readd all tracks to it except the newly set track
			if (resetShuffle) {
				shuffleTracks.clear();
				shuffleTracks.addAll(MusicTrack.values());
				shuffleTracks.removeValue(track, false);
			}
		} else {
			musicTime.setRange(0.0f, 0.0f);
			trackText.setText(HText.NOW_PLAYING_DEFAULT.text());
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
