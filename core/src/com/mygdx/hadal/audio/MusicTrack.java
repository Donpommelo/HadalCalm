package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public enum MusicTrack {

	OFFICE("music/OfficeMusic.mp3"),
	;
	
	private String musicFileName;
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
