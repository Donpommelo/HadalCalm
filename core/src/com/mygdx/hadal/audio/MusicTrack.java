package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	TITLE("music/title.ogg", "Iron Lungs (Title)", 192),
	HUB("music/hub.ogg", "Loaded Up (Hub 1)", 131),
	HUB_V2("music/hub_v2.ogg", "Floated Up (Hub 2)", 161),
	HUB_V3("music/hub_v3.ogg", "Bloated Up (Hub 3)", 161),

	AURAL_FIXATION("music/aural_fixation.ogg", "Aural Fixation", 149),
	CONFIDENCE("music/confidence.ogg", "Confidence", 216),
	GOLDEN_GLOW("music/golden_glow.ogg", "Golden Glow", 106),
	GOLDEN_SCALES("music/golden_scales.ogg", "Golden Scales", 133),
	ORGAN_GRINDER("music/organ_grinder.ogg", "Organ Grinder", 114),
	RED_EYE("music/red_eye.ogg", "Red Eye", 164),
	SHARKTOOTH("music/sharktooth.ogg", "Sharktooth", 156),
	SLEEPING_COGS("music/sleeping_cogs.ogg", "Sleeping Cogs", 176),
	SURRENDER("music/surrender.ogg", "Surrender", 141),
	WAKE_DANCER("music/wake_dancer.ogg", "Wake Dancer", 214),
	WHIPLASH("music/whiplash.ogg", "Whiplash", 85),

	;

	private final String musicFileName;

	//this is the name that will appear in the music icon pop in ui.
	private final String musicName;

	//time in seconds. Used to control track position in sound room
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

	/**
	 * This is called when a play state is initiated
	 * It disposes of music to free up the memory
	 */
	public static void clearMusic(MusicPlayer musicPlayer) {
		for (MusicTrack track: MusicTrack.values()) {

			//we don't want to dispose the current track because a playstate can be created while music stays the same
			if (track.music != null && track != musicPlayer.getCurrentTrack()) {
				track.music.dispose();
				track.music = null;
			}
		}
	}

	public String getMusicName() { return musicName; }

	public int getTrackLength() { return trackLength; }
}
