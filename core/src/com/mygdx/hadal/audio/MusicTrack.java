package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * We haven;t actually added music yet.
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	TITLE("music/title.ogg", 192),
	HUB("music/hub.ogg", 131),
	HUB_V2("music/hub_v2.ogg", 161),
	HUB_V3("music/hub_v3.ogg", 161),
	FIGHT1("music/fight1.ogg", 164),

	CONFIDENCE("music/confidence.ogg", 216),
	ORGAN_GRINDER("music/organ_grinder.ogg", 114),
	SURRENDER("music/surrender.ogg", 141),
	WHIPLASH("music/whiplash.ogg", 85),

	;
	
	private final String musicFileName;
	private final int trackLength;
	private Music music;

	MusicTrack(String musicFileName, int trackLength) {
		this.musicFileName = musicFileName;
		this.trackLength = trackLength;
	}
	
	public Music getMusic() {
		
		if (music == null) {
			music = Gdx.audio.newMusic(Gdx.files.internal(musicFileName));
		}
		return music;
	}

	public int getTrackLength() { return trackLength; }
}
