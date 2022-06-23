package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;

import java.util.HashMap;

/**
 * An UnlockLevel represents a single level in the game
 * @author Yirlotte Yilyde
 */
public enum UnlockLevel {

	BOSS_FISH("Maps/Boss1.tmx", GameText.BOSS_FISH, GameText.BOSS_FISH_DESC, true,
			GameMode.BOSS),
	BOSS_KING_KAMABOKO("Maps/Boss2.tmx", GameText.BOSS_KING_KAMABOKO, GameText.BOSS_BOSS_KING_KAMABOKO_DESC, true,
			GameMode.BOSS),
	BOSS_FALSE_SUN("Maps/Boss4.tmx", GameText.BOSS_FALSE_SUN, GameText.BOSS_FALSE_SUN_DESC, true,
			GameMode.BOSS),
	BOSS_NEPTUNE_KING("Maps/Boss5.tmx", GameText.BOSS_NEPTUNE_KING, GameText.BOSS_NEPTUNE_KING_DESC, true,
			GameMode.BOSS),
	BOSS_GILT_SCALED_SERAPH("Maps/Boss6.tmx", GameText.BOSS_GILT_SCALED_SERAPH, GameText.BOSS_BOSS_GILT_SCALED_SERAPH_DESC, true,
			GameMode.BOSS),

	AGGYDAGGY("Maps/dm_aggydaggy.tmx", GameText.AGGYDAGGY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	BOTTLENECK("Maps/dm_bottleneck.tmx", GameText.BOTTLENECK, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	BROUHAHA("Maps/dm_brouhaha.tmx", GameText.BROUHAHA, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	COQUELICOT("Maps/ctf_coquelicot.tmx", GameText.COQUELICOT, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	FACILITY("Maps/dm_facility.tmx", GameText.FACILITY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	FILTRATION("Maps/ctf_filtration.tmx", GameText.FILTRATION, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	FLOTSAM("Maps/dm_flotsam.tmx", GameText.FLOTSAM, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	GREEN("Maps/futbol_green.tmx", GameText.GREEN, GameText.NOTHING, true,
			GameMode.FOOTBALL, GameMode.DEATHMATCH),
	LAGAN("Maps/dm_lagan.tmx", GameText.LAGAN, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	THRESHOLD("Maps/dm_threshold.tmx", GameText.THRESHOLD, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	TOWERS("Maps/dm_towers.tmx", GameText.TOWERS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	UNDULATE("Maps/ctf_undulate.tmx", GameText.UNDULATE, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	WARP("Maps/dm_warp.tmx", GameText.WARP, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	QUADRANT("Maps/dm_quadrant.tmx", GameText.QUADRANTS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	WETWORKS("Maps/dm_wetworks.tmx", GameText.WETWORKS, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	ZIGGURAT("Maps/dm_ziggurat.tmx", GameText.ZIGGURAT, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),

	FORMOSAN_BLUE_MAGPIE("Maps/dm_formosan_blue_magpie.tmx", GameText.FORMOSAN_BLUE_MAGPIE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	FREE_RANGE("Maps/dm_free_range.tmx", GameText.FREE_RANGE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	GREY_HERON("Maps/dm_grey_heron.tmx", GameText.GREY_HERON, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	HUMMINGBIRD("Maps/dm_hummingbird.tmx", GameText.HUMMINGBIRD, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	JAPANESE_WHITEEYE("Maps/dm_japanese_whiteeye.tmx", GameText.JAPANESE_WHITEEYE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	LITTLE_BITTERN("Maps/dm_little_bittern.tmx", GameText.LITTLE_BITTERN, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	PELICAN("Maps/dm_pelican.tmx", GameText.PELICAN, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	SUPREME_CHAMPION("Maps/dm_supreme_champion.tmx", GameText.SUPREME_CHAMPION, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	
	FACING_CRABS("Maps/dm_crab.tmx", GameText.FACING_CRABS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	GALLERY("Maps/dm_gallery.tmx", GameText.GALLERY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	HORIZON("Maps/dm_horizon.tmx", GameText.HORIZON, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	REDROCK("Maps/dm_redrock.tmx", GameText.REDROCK, GameText.NOTHING, true,
			GameMode.DEATHMATCH),

	FALLDOWN("Maps/dm_falldown.tmx", GameText.FALLDOWN, GameText.NOTHING, true, GameMode.DEATHMATCH),
	MOONBOUNCE("Maps/dm_moonbounce.tmx", GameText.MOONBOUNCE, GameText.NOTHING, true, GameMode.DEATHMATCH),
	PINWHEEL("Maps/dm_pinwheel.tmx", GameText.PINWHEEL, GameText.NOTHING, true, GameMode.DEATHMATCH),
	PIVOT("Maps/dm_pivot.tmx", GameText.PIVOT, GameText.NOTHING, true, GameMode.DEATHMATCH),
	//	CAROUSEL("Maps/dm_carousel.tmx", GameMode.DEATHMATCH),
	//	GULLY("Maps/dm_gully.tmx", GameMode.DEATHMATCH),
	//	JUMP("Maps/dm_jump.tmx", GameMode.DEATHMATCH),
	//	ORIGINAL("Maps/dm_original.tmx", GameMode.DEATHMATCH),
	//	PILE("Maps/dm_pile.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	//	SAWMILL("Maps/dm_sawmill.tmx", GameMode.DEATHMATCH),
	//	SCALES("Maps/dm_scales.tmx", GameMode.DEATHMATCH),
	//	SEESAW("Maps/dm_seesaw.tmx", GameMode.DEATHMATCH),

	WRECK1("Maps/wreck1.tmx", GameMode.CAMPAIGN),
	WRECK2("Maps/wreck2.tmx", GameMode.CAMPAIGN),
	WRECK3("Maps/wreck3.tmx", GameMode.CAMPAIGN),
	WRECK4("Maps/wreck4.tmx", GameMode.CAMPAIGN),
	DERELICT1("Maps/derelict1.tmx", GameMode.CAMPAIGN),
	DERELICT2("Maps/derelict2.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER1("Maps/plenumchamber1.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER2("Maps/plenumchamber2.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER3("Maps/plenumchamber3.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER4("Maps/plenumchamber4.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER5("Maps/plenumchamber5.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER6("Maps/plenumchamber6.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERMAZE("Maps/plenumchambermaze.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERSHAFT("Maps/plenumchambershaft.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBEREXTRA1("Maps/plenumchamberextra1.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERTURBINE("Maps/plenumchamberturbine.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERBALCONY("Maps/plenumchamberbalcony.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA1("Maps/noiselesssea1.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA2("Maps/noiselesssea2.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA3("Maps/noiselesssea3.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA4("Maps/noiselesssea4.tmx", GameMode.CAMPAIGN),
	NOISELESSSEAEXTRA1("Maps/noiselessseaextra1.tmx", GameMode.CAMPAIGN),
	SLUICE1("Maps/sluice1.tmx", GameMode.CAMPAIGN),
	SLUICEBOSS("Maps/sluiceboss.tmx", GameMode.CAMPAIGN),
	PARTY("Maps/party.tmx", GameMode.CAMPAIGN),

	SANDBOX_HUB("Maps/sandboxhub.tmx", GameText.SANDBOX, GameText.SANDBOX_DESC, true, GameMode.SANDBOX),
	SANDBOX_ENEMY("Maps/sandboxenemy.tmx", GameMode.SANDBOX),
	SANDBOX_EVENTS("Maps/sandboxevent.tmx", GameMode.SANDBOX),

	SSTUNICATE1("Maps/sstunicate1.tmx", GameText.LEVEL_HUB, GameText.HUB_DESC, false, GameMode.HUB),
	HUB_MULTI("Maps/sstunicate2.tmx", GameText.LEVEL_HUB, GameText.HUB_DESC, true, GameMode.HUB),

	;
	
	//the level's filename
	private final String map;
	
	//information about the map
	private final GameText name, desc;
	private final Array<UnlockTag> tags = new Array<>();

	//these are modes that this map can be selected for
	private final GameMode[] modes;

	UnlockLevel(String map, GameText name, GameText desc, boolean multiplayer, GameMode... modes) {
		this.map = map;
		this.name = name;
		this.desc = desc;
		this.modes = modes;

		if (multiplayer) {
			tags.add(UnlockTag.MULTIPLAYER);
		} else {
			tags.add(UnlockTag.NAVIGATIONS);
		}
	}

	UnlockLevel(String map, GameMode... modes) {
		this(map, GameText.NOTHING, GameText.NOTHING, false, modes);
		tags.clear();
	}

	/**
	 * This acquires a list of all unlocked maps (if unlock is true. otherwise just return all maps that satisfy the tags)
	 */
	public static Array<UnlockLevel> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockLevel> items = new Array<>();
		
		for (UnlockLevel u : UnlockLevel.values()) {
			
			boolean get = UnlockManager.checkTags(u.tags, tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.LEVEL, u.toString())) {
				get = false;
			}
			if (get) {
				items.add(u);
			}
		}
		return items;
	}

	public String getMap() { return map; }

	public GameMode[] getModes() { return modes; }

	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	private static final HashMap<String, UnlockLevel> UnlocksByName = new HashMap<>();
	static {
		for (UnlockLevel u : UnlockLevel.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockLevel getByName(String s) {
		return UnlocksByName.getOrDefault(s, HUB_MULTI);
	}
}
