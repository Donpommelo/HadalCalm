package com.mygdx.hadal.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer {

	private final static int NUM_TRACKS = 1;
    private HashMap<String, String> trackListByName;
    private HashMap<Integer, String> trackListByNumber;
    
    public Music getCurrentSong() {
    	return currentSong;
    }

	 private Music currentSong = null;
	 private int currentTrackNumber = -1;
	    
	 public MusicPlayer() {
        trackListByName = new HashMap<String, String>(NUM_TRACKS);
        trackListByNumber = new HashMap<Integer, String>(NUM_TRACKS);
        
        trackListByName.put("test0", "sound/OfficeMusic.mp3");
        
        int count = 0;
        for (String name : trackListByName.keySet()){
            trackListByNumber.put(count, trackListByName.get(name));
            count++;
        }
	 }
	 
	 // Sets the current song.
	    public int setSong(String name){
	        currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByName.get(name)));
	        currentSong.setLooping(true);
	        return 0;
	    }

	    // Load and lay a non-tracklist song.
	    public void playSong(String name, float volume){
	        if (currentSong != null){
	            currentSong.stop();
	        }
	        currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByName.get(name)));
	        currentSong.setVolume(volume);
	        currentSong.play();
	    }

	    // Play next song.
	    public void playNext(){
	        if (currentSong == null || currentTrackNumber == -1 || currentTrackNumber == trackListByNumber.size() - 1){
	            currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByNumber.get(0)));
	            currentTrackNumber = 0;
	        } else {
	            currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByNumber.get(currentTrackNumber++)));
	            currentTrackNumber++;
	        }
	    }

	    // Play previous song.
	    public void playPrevious(){
	        if (currentSong == null || currentTrackNumber == -1 || currentTrackNumber == 0){
	            currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByNumber.get(trackListByNumber.size() - 1)));
	            currentTrackNumber = trackListByNumber.size() - 1;
	        } else {
	            currentSong = Gdx.audio.newMusic(Gdx.files.internal(trackListByNumber.get(currentTrackNumber--)));
	            currentTrackNumber--;
	        }
	    }

	    // Resumes the current song.
	    public void play(){
	        if (currentSong != null){
	            currentSong.play();
	        }
	    }

	    // Pauses the current song.
	    public void pause(){
	        if (currentSong != null){
	            currentSong.pause();
	        }
	    }

	    // Stops the current song.
	    public void stop(){
	        if (currentSong != null){
	            currentSong.stop();
	        }
	    }

	    public void setVolume(float vol) {
	        if (currentSong != null) {
	            currentSong.setVolume(vol);
	        }
	    }

	    // Clean up and dispose of player. Called when game closes.
	    public void dispose(){
	        currentSong.dispose();
	    }
}
