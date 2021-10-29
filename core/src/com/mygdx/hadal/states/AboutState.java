package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;

import static com.mygdx.hadal.utils.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION;

/**
 * The AboutState is selected from the title screen and gives information about the game
 * @author Snujube Spambly
 */
public class AboutState extends GameState {

	//This table contains the ui elements of the pause screen
	private Table options, details;
	private CheckBox continuePlaying;

	//options that the player can view
	private Text aboutOption, miscOption, tipsOption, soundRoomOption, creditsOption, exitOption, trackText, trackTime;
	private ScrollPane musicTracks;
	private Slider musicTime;

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
	private static final float sliderWidth = 400.0f;
	private static final float songTitleWidth = 300.0f;

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

				soundRoomOption = new Text("SOUND ROOM", 0, 0, true);
				soundRoomOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						soundRoomSelected();
					}
				});
				soundRoomOption.setScale(optionsScale);

				aboutOption = new Text("ABOUT", 0, 0, true);
				aboutOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						aboutSelected();
			        }
			    });
				aboutOption.setScale(optionsScale);

				tipsOption = new Text("TIPS", 0, 0, true);
				tipsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						tipsSelected();
			        }
			    });
				tipsOption.setScale(optionsScale);
				
				miscOption = new Text("MISC", 0, 0, true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						miscSelected();
			        }
			    });
				miscOption.setScale(optionsScale);
				
				creditsOption = new Text("CREDITS", 0, 0, true);
				creditsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						creditsSelected();
			        }
			    });
				creditsOption.setScale(optionsScale);
				
				exitOption = new Text("RETURN?", 0, 0, true);
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
		
		details.add(new Text("ABOUT", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text about = new Text(GameStateManager.miscText.getString("about"), 0, 0, false, true, detailsTextWidth);
		about.setScale(detailsScale);
		
		details.add(about);
	}

	private void soundRoomSelected() {
		details.clearChildren();

		details.add(new Text("SOUND ROOM", 0, 0, false)).colspan(2).pad(titlePad).row();

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

		VerticalGroup tracks = new VerticalGroup().space(optionPadding);

		//show list of available songs
		for (MusicTrack track: MusicTrack.values()) {
			Text trackListen = new Text("PLAY: " + track.getMusicName(), 0, 0, true);

			trackListen.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
					HadalGame.musicPlayer.playSong(track, 1.0f);
					setTrack(track);
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

		Text play = new Text ("PLAY", 0, 0, true);
		play.setScale(detailsScale);

		play.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

				if (HadalGame.musicPlayer.getCurrentSong() != null) {
					if (!HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
						HadalGame.musicPlayer.getCurrentSong().setPosition(musicTime.getValue());
						HadalGame.musicPlayer.play();
					}
				}
			}
		});

		Text pause = new Text ("PAUSE", 0, 0, true);
		pause.setScale(detailsScale);

		pause.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

				if (HadalGame.musicPlayer.getCurrentSong() != null) {
					if (HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
						HadalGame.musicPlayer.pause();
					}
				}
			}
		});

		Text stop = new Text ("STOP", 0, 0, true);
		stop.setScale(detailsScale);

		stop.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
				HadalGame.musicPlayer.stop();
				setTrack(null);
			}
		});

		continuePlaying = new CheckBox("CONTINUE PLAYING AFTER EXITING?", GameStateManager.getSkin());

		details.add(trackText).width(songTitleWidth).pad(detailPad);
		details.add(musicTime).row();

		details.add(musicTracks).width(scrollWidth).height(scrollHeight).expandY().pad(detailPad);
		stage.setScrollFocus(musicTracks);

		final Table soundRoomExtra = new Table();

		details.add(soundRoomExtra).top().size(extraWidth, extraHeight);
		soundRoomExtra.add(trackTime).colspan(2).height(optionHeight).pad(detailPad).row();
		soundRoomExtra.add(play).height(optionHeight).pad(detailPad);
		soundRoomExtra.add(pause).height(optionHeight).pad(detailPad);
		soundRoomExtra.add(stop).height(optionHeight).pad(detailPad).row();
		soundRoomExtra.add(continuePlaying).colspan(3).height(optionHeight).pad(detailPad).row();

		HadalGame.musicPlayer.setMusicState(MusicPlayer.MusicState.NOTHING);
		setTrack(HadalGame.musicPlayer.getCurrentTrack());
	}

	private void tipsSelected() {
		details.clearChildren();
		
		details.add(new Text("TIPS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text tips = new Text(GameStateManager.miscText.getString("tips"), 0, 0, false, true, detailsTextWidth);
		tips.setScale(detailsScale);
		
		details.add(tips);
	}
	
	private void miscSelected() {
		details.clearChildren();
		
		details.add(new Text("MISC", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text misc = new Text(GameStateManager.miscText.getString("misc"), 0, 0, false, true, detailsTextWidth);
		misc.setScale(detailsScale);
		
		details.add(misc);
	}

	private void creditsSelected() {
		details.clearChildren();
		
		details.add(new Text("CREDITS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		//dev and art options have url links
		Text dev = new Text("PROGRAMMING: DONPOMMELO", 0, 0, true);
		dev.setScale(detailsScale);
		dev.setColor(Color.RED);
		
		dev.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://donpommelo.itch.io/");
	        }
	    });
		
		Text art = new Text("ART: SHOEBANFOO", 0, 0, true);
		art.setScale(detailsScale);
		art.setColor(Color.RED);
		
		art.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://www.instagram.com/shoebanfoo/");
	        }
	    });

		Text music = new Text("MUSIC: VCRCHITECT", 0, 0, true);
		music.setScale(detailsScale);
		music.setColor(Color.RED);

		music.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://soundcloud.com/vcrchitect/");
			}
		});

		Text sfx = new Text(GameStateManager.miscText.getString("credits"), 0, 0, false, true, detailsTextWidth);
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

		if (continuePlaying != null) {
			if (continuePlaying.isChecked() && HadalGame.musicPlayer.getCurrentSong() != null) {
				if (HadalGame.musicPlayer.getCurrentSong().isPlaying()) {
					HadalGame.musicPlayer.setMusicState(MusicPlayer.MusicState.FREE);
				}
			}
		}
	}

	private void setTrack(MusicTrack track) {
		musicTime.setValue(0.0f);
		if (track != null) {
			musicTime.setRange(0.0f, track.getTrackLength());
			trackText.setText("NOW PLAYING: " + track.getMusicName());
		} else {
			musicTime.setRange(0.0f, 0.0f);
			trackText.setText("NOW PLAYING: NOTHING");
		}
	}

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
