package com.mygdx.hadal.audio;

import com.badlogic.gdx.audio.Music;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * The music player manages music tracks including fade transitions
 * @author Dichysoisse Duckheart
 */
public class MusicPlayer {

	private final GameStateManager gsm;
	
	//this is the song currently playing
    private Music currentSong;
    private MusicTrack currentTrack;
	
    //this is the song to be played next
	private MusicTrack nextTrack;

	//the "mode" of the music. This determines behavior when trying to switch songs
	private MusicState currentTrackType;

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

				if (nextTrack != null) {
					stop();

					currentSong = nextTrack.getMusic();
					currentTrack = nextTrack;
					currentSong.setLooping(true);
					currentSong.play();

					maxVolume = nextVolume;
					volume = 0.0f;
					
					fade = defaultFadeInSpeed;
					
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
	
	// Play a song. music can be null to indicate we want to stop the current song.
	public void playSong(MusicTrack music, float volume) {

    	//if we are playing another track, we make it fade out first
    	if (currentSong != null) {
			fade = defaultFadeOutSpeed;
			nextTrack = music;
			nextVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
		} else {
			//if starting a new song, set designated track as next song and fade in
    		if (music != null) {
				currentSong = music.getMusic();
				currentTrack = music;
				currentSong.setLooping(true);
				currentSong.play();
				maxVolume = volume * gsm.getSetting().getMusicVolume() * gsm.getSetting().getMasterVolume();
			}
			fade = defaultFadeInSpeed;
		}
	}

	//these arrays hold different types of songs. Whe na song is played, it will be chosen randomly from one list
	private static final MusicTrack[] titleTracks = {MusicTrack.TITLE};
	private static final MusicTrack[] hubTracks = {MusicTrack.HUB, MusicTrack.HUB_V2, MusicTrack.HUB_V3};
	private static final MusicTrack[] matchTracks = {MusicTrack.AURAL_FIXATION, MusicTrack.CONFIDENCE, MusicTrack.RED_EYE,
		MusicTrack.SLEEPING_COGS, MusicTrack.SURRENDER, MusicTrack.WHIPLASH, MusicTrack.ORGAN_GRINDER};

	public MusicTrack playSong(MusicState type, float volume) {

		MusicTrack track = null;

		//in "free" mode, the player chose a song in the sound room to persist even after level transitions
		if (currentTrackType == MusicState.FREE && currentSong != null) { return null; }

		//otherwise play a random track that matches the designated "mode"
		if (currentTrackType != type) {
			currentTrackType = type;
			int randomIndex;
			switch (type) {
				case MENU:
					track = titleTracks[0];
					playSong(track, volume);
					break;
				case HUB:
					randomIndex = GameStateManager.generator.nextInt(hubTracks.length);
					track = hubTracks[randomIndex];
					playSong(track, volume);
					break;
				case MATCH:
					randomIndex = GameStateManager.generator.nextInt(matchTracks.length);
					track = matchTracks[randomIndex];
					playSong(track, volume);
					break;
				case NOTHING:
					playSong((MusicTrack) null, volume);
					break;
			}
		}
		return track;
	}

	/**
	 * 	server plays a song and tells all clients to play the same song
	 * 	Tentatively, this is not used; clients and servers play music independently.
	 */
	public void syncSong(PlayState state, MusicTrack music, float volume) {
		
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncMusic(music.toString(), volume));
		}
		
		playSong(music, volume);
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
			currentSong.dispose();
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

	public void setMusicState(MusicState state) { currentTrackType = state; }

	public void setMusicPosition(float position) {
		if (currentSong != null) {
			currentSong.setPosition(position);
		}
	}

	public Music getCurrentSong() { return currentSong; }

	public MusicTrack getCurrentTrack() { return currentTrack; }

	public enum MusicState {
    	MENU,
		HUB,
		MATCH,
		NOTHING,
		FREE,
		SOUND_ROOM,
	}
}
