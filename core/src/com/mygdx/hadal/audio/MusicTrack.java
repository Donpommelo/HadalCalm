package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * We haven;t actually added music yet.
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	TITLE("music/title.ogg", "Iron Lungs (Title)", 192),
	HUB("music/hub.ogg", "Loaded Up (Hub 1)", 131),
	HUB_V2("music/hub_v2.ogg", "Floated Up (Hub 2)", 161),
	HUB_V3("music/hub_v3.ogg", "Bloated Up (Hub 3)", 161),

	AURAL_FIXATION("music/aural_fixation.ogg", "Aural Fixation", 149),
	CONFIDENCE("music/confidence.ogg", "Confidence", 216),
	ORGAN_GRINDER("music/organ_grinder.ogg", "Organ Grinder", 114),
	RED_EYE("music/red_eye.ogg", "Red Eye", 164),
	SLEEPING_COGS("music/sleeping_cogs.ogg", "Sleeping Cogs", 176),
	SURRENDER("music/surrender.ogg", "Surrender", 141),
	WHIPLASH("music/whiplash.ogg", "Whiplash", 85),

	;

	private final String musicFileName;
	private final String musicName;
	private final int trackLength;
	private Music music;

	MusicTrack(String musicFileName, String musicName, int trackLength) {
		this.musicFileName = musicFileName;
		this.musicName = musicName;
		this.trackLength = trackLength;
	}

	/**
	 * load the music if not already loaded and return it
	 */
	public Music getMusic() {
		
		if (music == null) {
			music = Gdx.audio.newMusic(Gdx.files.internal(musicFileName));
		}
		return music;
	}

	public String getMusicName() { return musicName; }

	public int getTrackLength() { return trackLength; }
}
