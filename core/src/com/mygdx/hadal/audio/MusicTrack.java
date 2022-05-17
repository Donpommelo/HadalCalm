package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * These are the music tracks in the game
 * @author Lurvudardus Labourne
 */
public enum MusicTrack {

	IRON_LUNGS("music/title.ogg", "Iron Lungs (Title)", 192),
	LOADED_UP("music/hub.ogg", "Loaded Up (Hub 1)", 131),
	FLOATED_UP("music/hub_v2.ogg", "Floated Up (Hub 2)", 161),
	BLOATED_UP("music/hub_v3.ogg", "Bloated Up (Hub 3)", 161),
	FISH_FOOD("music/fish_food.ogg", "Fish Food (Game Over)", 166),
	RED_BUBBLES("music/red_bubbles.ogg", "Red Bubbles (Victory)", 132),

	AURAL_FIXATION("music/aural_fixation.ogg", "Aural Fixation", 149),
	THE_BENDS("music/the_bends.ogg", "The Bends", 240),
	CONFIDENCE("music/confidence.ogg", "Confidence", 216),
	GOLDEN_GLOW("music/golden_glow.ogg", "Golden Glow", 106),
	GOLDEN_SCALES("music/golden_scales.ogg", "Golden Scales", 133),
	HADAL_FEAR("music/hadal_fear.ogg", "Hadal Fear", 148),
	HEAVY_BREATHING("music/heavy_breathing.ogg", "Heavy Breathing", 253),
	HOOKED_AND_HARPOONED("music/hookjaw.ogg", "Hooked and Harpooned", 102),
	HOOKJAW("music/hookjaw.ogg", "Hookjaw", 153),
	HULL_DAMAGE("music/hull_damage.ogg", "Hull Damage", 118),
	LIQUID_ASPIRATIONS("music/liquid_aspirations.ogg", "Liquid Aspirations", 158),
	ORGAN_GRINDER("music/organ_grinder.ogg", "Organ Grinder", 114),
	OXYGEN_DEPRIVED("music/oxygen_deprived.ogg", "Oxygen Deprived", 160),
	RATS_ON_A_SINKING_SHIP("music/rats_on_a_sinking_ship.ogg", "Rats on a Sinking Ship", 118),
	RED_EYE("music/red_eye.ogg", "Red Eye", 164),
	SAND_FANGS("music/sand_fangs.ogg", "Sand Fangs", 146),
	SHARKTOOTH("music/sharktooth.ogg", "Sharktooth", 156),
	SLEEPING_COGS("music/sleeping_cogs.ogg", "Sleeping Cogs", 176),
	SURRENDER("music/surrender.ogg", "Surrender", 141),
	THE_TINNITUS_GOSPEL("music/the_tinnitus_gospel.ogg", "The Tinnitus Gospel", 172),
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
