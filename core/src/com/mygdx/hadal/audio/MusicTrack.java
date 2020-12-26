package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * We haven;t actually added music yet.
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	TITLE("music/title.ogg"),
	HUB("music/hub.ogg"),
	HUB_V2("music/hub_v2.ogg"),
	HUB_V3("music/hub_v3.ogg"),
	FIGHT1("music/fight1.ogg"),

	CONFIDENCE("music/confidence.ogg"),
	ORGAN_GRINDER("music/organ_grinder.ogg"),
	SURRENDER("music/surrender.ogg"),
	WHIPLASH("music/whiplash.ogg"),

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
