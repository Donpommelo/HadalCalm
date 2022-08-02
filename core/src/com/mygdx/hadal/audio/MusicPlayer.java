package com.mygdx.hadal.audio;

import com.badlogic.gdx.audio.Music;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The music player manages music tracks including fade transitions
 * @author Dichysoisse Duckheart
 */
public class MusicPlayer {

	//default values for sound fading
	private static final float DEFAULT_FADE_IN_SPEED = 1.0f;
	private static final float DEFAULT_FADE_OUT_SPEED = -1.0f;

	private final GameStateManager gsm;
	
	//this is the song currently playing
    private Music currentSong;
    private MusicTrack currentTrack;
	
    //this is the song to be played next
	private MusicTrack nextTrack;

	//the "mode" of the music. This determines behavior when trying to switch songs
	private MusicTrackType currentTrackType;

    //this is the rate at which the sound volume changes (default: 0, -x for fading out and +x for fading in)
  	private float fade;

  	//the volume of the sound and the max volume the sound will fade in to.
  	private float volume, maxVolume, nextVolume;
  	
    public MusicPlayer(GameStateManager gsm) {
    	this.gsm = gsm;
    }
	 
    public void controller(float delta) {

    	//process music fading. Gradually change music volume until it reaches 0.0 or max volume.
		if (fade != 0) {
			volume += delta * fade;
			
			//when a music finishes fading out, pause (or transition to next song)
			if (volume <= 0.0f) {
				volume = 0.0f;
				fade = 0.0f;

				if (nextTrack != null) {
					float lastVolume = freshenUpSong();

					currentSong = nextTrack.getMusic();
					currentTrack = nextTrack;
					currentSong.setLooping(true);
					currentSong.play();

					maxVolume = nextVolume;
					volume = lastVolume;
					
					fade = DEFAULT_FADE_IN_SPEED;
					
					nextTrack = null;
				} else {
					pause();
				}
			}

			//cap volume at max if we exceed it
			if (volume >= maxVolume) {
				volume = maxVolume;
				fade = 0.0f;
			}

			setVolume(volume);
		}
	}

	/**
	 * 	Play a song. music can be null to indicate we want to stop the current song.
 	 */
	public void playSong(MusicTrack music, float volume) {

    	//if we are playing another track, we make it fade out first
    	if (currentSong != null) {
			fade = DEFAULT_FADE_OUT_SPEED;
			nextTrack = music;
			nextVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
		} else {
			//if starting a new song, set designated track as next song and fade in
    		if (music != null) {
				currentSong = music.getMusic();

				//set volume to 0.0 to avoid having song play for 1 frame when game starts up muted
				currentSong.setVolume(0.0f);

				currentTrack = music;
				currentSong.setLooping(true);
				currentSong.play();
				maxVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
			}
			fade = DEFAULT_FADE_IN_SPEED;
		}
	}

	/**
	 * Play a random song of a specific musicTrackType
	 */
	public MusicTrack playSong(MusicTrackType type, float volume) {

		MusicTrack track = null;

		//in "free" mode, the player chose a song in the sound room to persist even after level transitions
		if (currentTrackType == MusicTrackType.FREE && currentSong != null) { return null; }

		//otherwise play a random track that matches the designated "mode"
		if (currentTrackType != type) {
			currentTrackType = type;
			track = type.getTrack();
			playSong(track, volume);
		}
		return track;
	}

	/**
	 * A magical function to address some wonkiness between the music fade and a new song on the next scene
	 * And by "magical" I mean neither I nor the person who told me to use it knew why it solved the issue
	 */
	private float freshenUpSong() {
		float volume = 1.0f;
		if (currentSong != null) {
			volume = currentSong.getVolume();

			currentSong.stop();
			currentSong = null;
			currentTrack = null;
		}
		return volume;
	}

	public void play() {
	    if (currentSong != null) {
	        currentSong.play();
	    }
	}

	public void pause() {
	    if (currentSong != null) {
	        currentSong.pause();
	    }
	}

	public void stop() {
		if (currentSong != null) {
	        currentSong.stop();
			currentSong = null;
			currentTrack = null;
	    }
	}

	public void setVolume(float vol) {
	    if (currentSong != null) {
	        currentSong.setVolume(vol);
	    }
	}

	// Clean up and dispose of player. Called when game closes.
	public void dispose() { 
		if (currentSong != null) {
			currentSong.dispose(); 
		}
	}

	public void setMusicState(MusicTrackType state) { currentTrackType = state; }

	public void setMusicPosition(float position) {
		if (currentSong != null) {
			currentSong.setPosition(position);
		}
	}

	public Music getCurrentSong() { return currentSong; }

	public MusicTrack getCurrentTrack() { return currentTrack; }
}
