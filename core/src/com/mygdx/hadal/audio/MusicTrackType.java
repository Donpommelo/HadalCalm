package com.mygdx.hadal.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A Music Track Type describes the different pools of songs that play at different parts of the game
 * @author Flangdoof Frawbmaker
 */
public enum MusicTrackType {

    NOTHING(),
    TITLE(MusicTrack.IRON_LUNGS),
    HUB(MusicTrack.LOADED_UP, MusicTrack.FLOATED_UP, MusicTrack.BLOATED_UP),
    MATCH(MusicTrack.AURAL_FIXATION, MusicTrack.THE_BENDS, MusicTrack.CONFIDENCE, MusicTrack.GOLDEN_GLOW,
            MusicTrack.GOLDEN_SCALES, MusicTrack.HADAL_FEAR, MusicTrack.HOOKED_AND_HARPOONED, MusicTrack.HOOKJAW,
            MusicTrack.HULL_DAMAGE, MusicTrack.ORGAN_GRINDER, MusicTrack.OXYGEN_DEPRIVED, MusicTrack.RATS_ON_A_SINKING_SHIP,
            MusicTrack.RED_EYE, MusicTrack.SAND_FANGS, MusicTrack.SHARKTOOTH, MusicTrack.SLEEPING_COGS, MusicTrack.SURRENDER,
            MusicTrack.THE_TINNITUS_GOSPEL, MusicTrack.WAKE_DANCER, MusicTrack.WHIPLASH),
    VICTORY(MusicTrack.RED_BUBBLES),
    GAME_OVER(MusicTrack.FISH_FOOD),

    BOSS_PLENUM_CHAMBER(MusicTrack.SHARKTOOTH),
    BOSS_KAMABOKOYA(MusicTrack.ORGAN_GRINDER),
    BOSS_GOLDFISH_PURGATORY(MusicTrack.GOLDEN_GLOW),
    BOSS_NEPTUNE_KING_GARDEN(MusicTrack.HADAL_FEAR),
    BOSS_SHADOW_OF_THE_FALSE_SUN(MusicTrack.AURAL_FIXATION),

    LEVEL_WRECK(MusicTrack.SAND_FANGS),
    LEVEL_DERELICT(MusicTrack.HULL_DAMAGE),
    LEVEL_PLENUM_CHAMBER(MusicTrack.SLEEPING_COGS),
    LEVEL_PLENUM_MAZE(MusicTrack.HADAL_FEAR),
    LEVEL_SLUICE(MusicTrack.SAND_FANGS),
    LEVEL_NOISELESS_SEA(MusicTrack.HEAVY_BREATHING),
    EXTRA_ROOM(MusicTrack.LIQUID_ASPIRATIONS),

    ARCADE_HUB(MusicTrack.LIQUID_ASPIRATIONS),

    FREE(),
    SOUND_ROOM()

    ;

    //List of tracks that can play at this situation
    private final MusicTrack[] tracks;

    MusicTrackType(MusicTrack... tracks) {
        this.tracks = tracks;
    }

    /**
     * @return a random track from thte available pool
     */
    public MusicTrack getTrack() {
        if (0 != tracks.length) {
            return tracks[MathUtils.random(tracks.length - 1)];
        } else {
            return null;
        }
    }

    private static final ObjectMap<String, MusicTrackType> TRACKS_BY_NAME = new ObjectMap<>();
    static {
        for (MusicTrackType u: MusicTrackType.values()) {
            TRACKS_BY_NAME.put(u.toString(), u);
        }
    }
    public static MusicTrackType getByName(String s) {
        return TRACKS_BY_NAME.get(s, TITLE);
    }
}
