package com.mygdx.hadal.audio;

import com.badlogic.gdx.audio.Music;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The music player manages music tracks including fade transitions
 * @author Zachary Tu
 */
public class MusicPlayer {

	private GameStateManager gsm;
	
	//this is the song currently playing
    private Music currentSong = null;
	
    //this is the song to be played next
    private MusicTrack nextSong;
    
    //this is the rate at which the sound volume changes (default: 0, -x for fading out and +x for fading in)
  	private float fade;

  	//the volume of the sound and the max volume the sound will fade in to.
  	private float volume, maxVolume, nextVolume;
  	
  //default values for sound fading
  	private static final float defaultFadeInSpeed = 1.0f;
  	private static final float defaultFadeOutSpeed = -1.0f;
  	
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
				
				if (nextSong != null) {
					stop();
					
					currentSong = nextSong.getMusic();
					currentSong.setLooping(true);
					currentSong.play();
					
					maxVolume = nextVolume;
					volume = 0.0f;
					
					fade = defaultFadeInSpeed;
					
					nextSong = null;
				} else {
					pause();
				}
			}
			if (volume >= maxVolume) {
				volume = maxVolume;
				fade = 0.0f;
			}
			
			setVolume(volume);
		}
	}
	
	// Play a non-tracklist song.
	public void playSong(MusicTrack music, float volume) {
		if (currentSong != null) {
			fade = defaultFadeOutSpeed;
			nextSong = music;
			nextVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
		} else {
			currentSong = music.getMusic();
			currentSong.setLooping(true);
			currentSong.play();
			
			maxVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
			volume = 0.0f;
			
			fade = defaultFadeInSpeed;
		}
	}

	//server plays a song and tells all clients to play the same song
	public void syncSong(PlayState state, MusicTrack music, float volume) {
		
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncMusic(music.toString(), volume));
		}
		
		playSong(music, volume);
	}
	
	// Resumes the current song.
	public void play() {
	    if (currentSong != null) {
	        currentSong.play();
	    }
	}

	// Pauses the current song.
	public void pause() {
	    if (currentSong != null) {
	        currentSong.pause();
	    }
	}

	// Stops the current song.
	public void stop() {
	    if (currentSong != null) {
	        currentSong.stop();
			currentSong.dispose(); 
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
}
