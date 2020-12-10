package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * We haven;t actually added music yet.
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	TITLE("music/title.mp3"),
	TITLE_V2("music/title_v2.mp3"),
	;
	
	private final String musicFileName;
	private Music music;
	
	MusicTrack(String musicFileName) {
		this.musicFileName = musicFileName;
	}
	
	public Music getMusic() {
		
		if (music == null) {
			music = Gdx.audio.newMusic(Gdx.files.internal(musicFileName));
		}
		return music;
	}
}
