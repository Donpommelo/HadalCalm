package com.mygdx.hadal.audio;

import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

public enum MusicTrackType {

    NOTHING(),
    TITLE(MusicTrack.IRON_LUNGS),
    HUB(MusicTrack.LOADED_UP, MusicTrack.FLOATED_UP, MusicTrack.BLOATED_UP),
    MATCH(MusicTrack.AURAL_FIXATION, MusicTrack.THE_BENDS, MusicTrack.CONFIDENCE, MusicTrack.GOLDEN_GLOW,
            MusicTrack.GOLDEN_SCALES, MusicTrack.HADAL_FEAR, MusicTrack.HOOKED_AND_HARPOONED, MusicTrack.HOOKJAW,
            MusicTrack.ORGAN_GRINDER, MusicTrack.OXYGEN_DEPRIVED, MusicTrack.RED_EYE,
            MusicTrack.SHARKTOOTH, MusicTrack.SLEEPING_COGS, MusicTrack.SURRENDER, MusicTrack.WAKE_DANCER, MusicTrack.WHIPLASH),
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


    FREE(),
    SOUND_ROOM()

    ;

    private final MusicTrack[] tracks;

    MusicTrackType(MusicTrack... tracks) {
        this.tracks = tracks;
    }

    public MusicTrack getTrack() {
        if (tracks.length != 0) {
            return tracks[MathUtils.random(tracks.length - 1)];
        } else {
            return null;
        }
    }

    private static final HashMap<String, MusicTrackType> TracksByName = new HashMap<>();
    static {
        for (MusicTrackType u: MusicTrackType.values()) {
            TracksByName.put(u.toString(), u);
        }
    }
    public static MusicTrackType getByName(String s) {
        return TracksByName.getOrDefault(s, TITLE);
    }
}
