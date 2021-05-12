package com.mygdx.hadal.save;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An UnlockLevel represents a single level in the game
 * @author Yirlotte Yilyde
 */
public enum UnlockLevel {

	DM_BOTTLENECK("Maps/dm_bottleneck.tmx", true),
	DM_BROUHAHA("Maps/dm_brouhaha.tmx", true, "dm"),
	DM_COQUELICOT("Maps/ctf_coquelicot.tmx", true, "dm"),
	DM_FACILITY("Maps/dm_facility.tmx", true),
	DM_FLOTSAM("Maps/dm_flotsam.tmx", true, "dm"),
	DM_LAGAN("Maps/dm_lagan.tmx, true", "dm"),
	DM_THRESHOLD("Maps/dm_threshold.tmx", true, "dm"),
	DM_TOWERS("Maps/dm_towers.tmx", true),
	DM_WARP("Maps/dm_warp.tmx", true, "dm"),
	DM_QUADRANT("Maps/dm_quadrant.tmx", true),
	DM_WETWORKS("Maps/dm_wetworks.tmx", true, "dm"),

	CTF_COQUELICOT("Maps/ctf_coquelicot.tmx",true, false, false, false, false, 1, "ctf"),

	BOSS1("Maps/Boss1.tmx"),
	BOSS2("Maps/Boss2.tmx"),
	BOSS3("Maps/Boss3.tmx"),
	BOSS4("Maps/Boss4.tmx"),
	BOSS5("Maps/Boss5.tmx"),
	BOSS6("Maps/Boss6.tmx"),

	RANDOM("Maps/sstunicate1.tmx"),

	ARENA_BROUHAHA("Maps/dm_brouhaha.tmx", "arena"),
	ARENA_FLOTSAM("Maps/dm_flotsam.tmx", "arena"),
	ARENA_HORIZON("Maps/dm_horizon.tmx", "arena"),
	ARENA_LAGAN("Maps/dm_lagan.tmx", "arena"),
	ARENA_PILE("Maps/dm_pile.tmx", "arena"),
	ARENA_THRESHOLD("Maps/dm_threshold.tmx", "arena"),
	ARENA_WARP("Maps/dm_warp.tmx", "arena"),
	ARENA_WETWORKS("Maps/dm_wetworks.tmx", "arena"),
	ARENA_ZIGGURAT("Maps/dm_ziggurat.tmx", "arena"),

	DM_AGGYDAGGY("Maps/dm_aggydaggy.tmx", true),
	DM_CAROUSEL("Maps/dm_carousel.tmx", true),
	DM_FALLDOWN("Maps/dm_falldown.tmx", true),
	DM_GULLY("Maps/dm_gully.tmx", true),
	DM_JUMP("Maps/dm_jump.tmx", true),
	DM_MOONBOUNCE("Maps/dm_moonbounce.tmx", true),
	DM_ORIGINAL("Maps/dm_original.tmx", true),
	DM_PILE("Maps/dm_pile.tmx", true, "dm"),
	DM_PINWHEEL("Maps/dm_pinwheel.tmx", true),
	DM_PIVOT("Maps/dm_pivot.tmx", true),
	DM_SAWMILL("Maps/dm_sawmill.tmx", true),
	DM_SCALES("Maps/dm_scales.tmx", true),
	DM_SEESAW("Maps/dm_seesaw.tmx", true),
	DM_ZIGGURAT("Maps/dm_ziggurat.tmx", true, "dm"),

	DM_FORMOSAN_BLUE_MAGPIE("Maps/dm_formosan_blue_magpie.tmx", true),
	DM_FREE_RANGE("Maps/dm_free_range.tmx", true),
	DM_GREY_HERON("Maps/dm_grey_heron.tmx", true),
	DM_HUMMINGBIRD("Maps/dm_hummingbird.tmx", true),
	DM_JAPANESE_WHITEEYE("Maps/dm_japanese_whiteeye.tmx", true),
	DM_LITTLE_BITTERN("Maps/dm_little_bittern.tmx", true),
	DM_PELICAN("Maps/dm_pelican.tmx", true),
	DM_SUPREME_CHAMPION("Maps/dm_supreme_champion.tmx", true),
	
	DM_FACING_CRABS("Maps/dm_crab.tmx", true),
	DM_GALLERY("Maps/dm_gallery.tmx", true),
	DM_HORIZON("Maps/dm_horizon.tmx", true, "dm"),
	DM_REDROCK("Maps/dm_redrock.tmx", true),

	WRECK1("Maps/wreck1.tmx"),
	WRECK2("Maps/wreck2.tmx"),
	WRECK3("Maps/wreck3.tmx"),
	WRECK4("Maps/wreck4.tmx"),
	DERELICT1("Maps/derelict1.tmx"),
	DERELICT2("Maps/derelict2.tmx"),
	PLENUMCHAMBER1("Maps/plenumchamber1.tmx"),
	PLENUMCHAMBER2("Maps/plenumchamber2.tmx"),
	PLENUMCHAMBER3("Maps/plenumchamber3.tmx"),
	PLENUMCHAMBER4("Maps/plenumchamber4.tmx"),
	PLENUMCHAMBER5("Maps/plenumchamber5.tmx"),
	PLENUMCHAMBER6("Maps/plenumchamber6.tmx"),
	PLENUMCHAMBERMAZE("Maps/plenumchambermaze.tmx"),
	PLENUMCHAMBERSHAFT("Maps/plenumchambershaft.tmx"),
	PLENUMCHAMBEREXTRA1("Maps/plenumchamberextra1.tmx"),
	PLENUMCHAMBERTURBINE("Maps/plenumchamberturbine.tmx"),
	PLENUMCHAMBERBALCONY("Maps/plenumchamberbalcony.tmx"),
	NOISELESSSEA1("Maps/noiselesssea1.tmx"),
	NOISELESSSEA2("Maps/noiselesssea2.tmx"),
	NOISELESSSEA3("Maps/noiselesssea3.tmx"),
	NOISELESSSEA4("Maps/noiselesssea4.tmx"),
	NOISELESSSEAEXTRA1("Maps/noiselessseaextra1.tmx"),
	SLUICE1("Maps/sluice1.tmx"),
	SLUICEBOSS("Maps/sluiceboss.tmx"),
	PARTY("Maps/party.tmx"),

	SANDBOX_HUB("Maps/sandboxhub.tmx", true, false, true, false, true, -1),
	SANDBOX_ENEMY("Maps/sandboxenemy.tmx", true, false, true, false, true, -1),
	SANDBOX_EVENTS("Maps/sandboxevent.tmx", true, false, true, false, true, -1),

	FOOTBALL_GREEN("Maps/futbol_green.tmx", true, false, false, true, false, 1),

	SSTUNICATE1("Maps/sstunicate1.tmx", false, true, true, false, true, -1),
	HUB_MULTI("Maps/sstunicate2.tmx", false, true, true, false, true, -1),

	;
	
	//the level's filename
	private final String map;
	
	//information about the map
	private InfoItem info;

	//this is a list of event layers that this map will parse when loaded. Used for maps with multiple valid modes
	private final String[] extraLayers;

	//the settings of the map are a field of the unlock so the same map can have multiple modes
	private final boolean pvp, hub, unlimitedLives, killScore, noDamage;

	//is there a default team mode for this map? (-1 means it goes with the server settings)
	private final int teamType;

	UnlockLevel(String map, String... extraLayers) {
		this(map, false, extraLayers);
	}

	UnlockLevel(String map, boolean pvp, String... extraLayers) {
		this(map, pvp, false, false, false, true, -1, extraLayers);
	}

	UnlockLevel(String map, boolean pvp, boolean hub, boolean unlimitedLives, boolean noDamage, boolean killScore, int teamType,
				String... extraLayers) {
		this.map = map;
		this.pvp = pvp;
		this.hub = hub;
		this.unlimitedLives = unlimitedLives;
		this.noDamage = noDamage;
		this.killScore = killScore;
		this.teamType = teamType;
		this.extraLayers = extraLayers;
	}

	/**
	 * This acquires a list of all unlocked maps (if unlock is true. otherwise just return all maps that satisfy the tags)
	 */
	public static Array<UnlockLevel> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockLevel> items = new Array<>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.LEVEL, u.toString())) {
				get = false;
			}
			if (get) {
				items.add(u);
			}
		}
		return items;
	}
	
	/**
	 * Acquire a random map that satisfies the input tags
	 */
	public static UnlockLevel getRandomMap(PlayState state, ArrayList<UnlockTag> tags) {
		
		Array<UnlockLevel> dm = getUnlocks(state, false, tags);

		if (dm.size > 0) {
			UnlockLevel level = dm.get(MathUtils.random(dm.size - 1));
			
			//ensures we do not random the random map
			if (level != UnlockLevel.RANDOM) {
				return level;
			} else {
				return getRandomMap(state, tags);
			}
		} else {
			return getRandomMap(state, new ArrayList<>());
		}
	}
		
	public InfoItem getInfo() { return info; }

	public void setInfo(InfoItem info) { this.info = info; }
	
	public String getMap() { return map; }

	public String[] getExtraLayers() { return extraLayers; }

	public boolean isPvp() { return pvp; }

	public boolean isHub() { return hub; }

	public boolean isUnlimitedLives() { return unlimitedLives; }

	public boolean isKillScore() { return killScore; }

	public boolean isNoDamage() { return noDamage; }

	public int getTeamType() { return teamType; }

	private static final HashMap<String, UnlockLevel> UnlocksByName = new HashMap<>();
	static {
		for (UnlockLevel u: UnlockLevel.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockLevel getByName(String s) {
		return UnlocksByName.getOrDefault(s, HUB_MULTI);
	}
}
