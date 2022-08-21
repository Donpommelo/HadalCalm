package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.text.UIText;

import java.util.HashMap;

/**
 * An UnlockLevel represents a single level in the game
 * @author Yirlotte Yilyde
 */
public enum UnlockLevel {

	BOSS_FISH("Boss1", GameText.BOSS_FISH, GameText.BOSS_FISH_DESC, true, MapSize.MEDIUM,
			GameMode.BOSS),
	BOSS_KING_KAMABOKO("Boss2", GameText.BOSS_KING_KAMABOKO, GameText.BOSS_BOSS_KING_KAMABOKO_DESC, true, MapSize.MEDIUM,
			GameMode.BOSS),
	BOSS_FALSE_SUN("Boss4", GameText.BOSS_FALSE_SUN, GameText.BOSS_FALSE_SUN_DESC, true, MapSize.MEDIUM,
			GameMode.BOSS),
	BOSS_NEPTUNE_KING("Boss5", GameText.BOSS_NEPTUNE_KING, GameText.BOSS_NEPTUNE_KING_DESC, true, MapSize.MEDIUM,
			GameMode.BOSS),
	BOSS_GILT_SCALED_SERAPH("Boss6", GameText.BOSS_GILT_SCALED_SERAPH, GameText.BOSS_BOSS_GILT_SCALED_SERAPH_DESC, true, MapSize.MEDIUM,
			GameMode.BOSS),

	AGGYDAGGY("dm_aggydaggy", GameText.AGGYDAGGY, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	BOTTLENECK("dm_bottleneck", GameText.BOTTLENECK, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	BROUHAHA("dm_brouhaha", GameText.BROUHAHA, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	COQUELICOT("ctf_coquelicot", GameText.COQUELICOT, GameText.NOTHING, true, MapSize.EXTRA_LARGE,
			GameMode.CTF, GameMode.DEATHMATCH),
	FACILITY("dm_facility", GameText.FACILITY, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	FILTRATION("ctf_filtration", GameText.FILTRATION, GameText.NOTHING, true, MapSize.EXTRA_LARGE,
			GameMode.CTF, GameMode.DEATHMATCH),
	FLOTSAM("dm_flotsam", GameText.FLOTSAM, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	GREEN("futbol_green", GameText.GREEN, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.FOOTBALL, GameMode.DEATHMATCH),
	LAGAN("dm_lagan", GameText.LAGAN, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	THRESHOLD("dm_threshold", GameText.THRESHOLD, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	TOWERS("dm_towers", GameText.TOWERS, GameText.NOTHING, true, MapSize.LARGE,
			GameMode.DEATHMATCH),
	UNDULATE("ctf_undulate", GameText.UNDULATE, GameText.NOTHING, true, MapSize.EXTRA_LARGE,
			GameMode.CTF, GameMode.DEATHMATCH),
	WARP("dm_warp", GameText.WARP, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	QUADRANT("dm_quadrant", GameText.QUADRANTS, GameText.NOTHING, true, MapSize.LARGE,
			GameMode.DEATHMATCH),
	WETWORKS("dm_wetworks", GameText.WETWORKS, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	ZIGGURAT("dm_ziggurat", GameText.ZIGGURAT, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),

	FORMOSAN_BLUE_MAGPIE("dm_formosan_blue_magpie", GameText.FORMOSAN_BLUE_MAGPIE, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	FREE_RANGE("dm_free_range", GameText.FREE_RANGE, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	GREY_HERON("dm_grey_heron", GameText.GREY_HERON, GameText.NOTHING, true, MapSize.SMALL,
			GameMode.DEATHMATCH),
	HUMMINGBIRD("dm_hummingbird", GameText.HUMMINGBIRD, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	JAPANESE_WHITEEYE("dm_japanese_whiteeye", GameText.JAPANESE_WHITEEYE, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	LITTLE_BITTERN("dm_little_bittern", GameText.LITTLE_BITTERN, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	PELICAN("dm_pelican", GameText.PELICAN, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	SUPREME_CHAMPION("dm_supreme_champion", GameText.SUPREME_CHAMPION, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	
	FACING_CRABS("dm_crab", GameText.FACING_CRABS, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	GALLERY("dm_gallery", GameText.GALLERY, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),
	HORIZON("dm_horizon", GameText.HORIZON, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	REDROCK("dm_redrock", GameText.REDROCK, GameText.NOTHING, true, MapSize.MEDIUM,
			GameMode.DEATHMATCH),

	FALLDOWN("dm_falldown", GameText.FALLDOWN, GameText.NOTHING, true, MapSize.SMALL, GameMode.DEATHMATCH),
	MOONBOUNCE("dm_moonbounce", GameText.MOONBOUNCE, GameText.NOTHING, true, MapSize.MEDIUM, GameMode.DEATHMATCH),
	PINWHEEL("dm_pinwheel", GameText.PINWHEEL, GameText.NOTHING, true, MapSize.SMALL, GameMode.DEATHMATCH),
	PIVOT("dm_pivot", GameText.PIVOT, GameText.NOTHING, true, MapSize.SMALL, GameMode.DEATHMATCH),
	//	CAROUSEL("dm_carousel", GameMode.DEATHMATCH),
	//	GULLY("dm_gully", GameMode.DEATHMATCH),
	//	JUMP("dm_jump", GameMode.DEATHMATCH),
	//	ORIGINAL("dm_original", GameMode.DEATHMATCH),
	//	PILE("dm_pile", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	//	SAWMILL("dm_sawmill", GameMode.DEATHMATCH),
	//	SCALES("dm_scales", GameMode.DEATHMATCH),
	//	SEESAW("dm_seesaw", GameMode.DEATHMATCH),

	WRECK1("wreck1", GameMode.CAMPAIGN),
	WRECK2("wreck2", GameMode.CAMPAIGN),
	WRECK3("wreck3", GameMode.CAMPAIGN),
	WRECK4("wreck4", GameMode.CAMPAIGN),
	DERELICT1("derelict1", GameMode.CAMPAIGN),
	DERELICT2("derelict2", GameMode.CAMPAIGN),
	PLENUMCHAMBER1("plenumchamber1", GameMode.CAMPAIGN),
	PLENUMCHAMBER2("plenumchamber2", GameMode.CAMPAIGN),
	PLENUMCHAMBER3("plenumchamber3", GameMode.CAMPAIGN),
	PLENUMCHAMBER4("plenumchamber4", GameMode.CAMPAIGN),
	PLENUMCHAMBER5("plenumchamber5", GameMode.CAMPAIGN),
	PLENUMCHAMBER6("plenumchamber6", GameMode.CAMPAIGN),
	PLENUMCHAMBERMAZE("plenumchambermaze", GameMode.CAMPAIGN),
	PLENUMCHAMBERSHAFT("plenumchambershaft", GameMode.CAMPAIGN),
	PLENUMCHAMBEREXTRA1("plenumchamberextra1", GameMode.CAMPAIGN),
	PLENUMCHAMBERTURBINE("plenumchamberturbine", GameMode.CAMPAIGN),
	PLENUMCHAMBERBALCONY("plenumchamberbalcony", GameMode.CAMPAIGN),
	NOISELESSSEA1("noiselesssea1", GameMode.CAMPAIGN),
	NOISELESSSEA2("noiselesssea2", GameMode.CAMPAIGN),
	NOISELESSSEA3("noiselesssea3", GameMode.CAMPAIGN),
	NOISELESSSEA4("noiselesssea4", GameMode.CAMPAIGN),
	NOISELESSSEAEXTRA1("noiselessseaextra1", GameMode.CAMPAIGN),
	SLUICE1("sluice1", GameMode.CAMPAIGN),
	SLUICEBOSS("sluiceboss", GameMode.CAMPAIGN),
	PARTY("party", GameMode.CAMPAIGN),

	SANDBOX_HUB("sandboxhub", GameText.SANDBOX, GameText.SANDBOX_DESC, true, MapSize.MEDIUM, GameMode.SANDBOX),
	SANDBOX_ENEMY("sandboxenemy", GameMode.SANDBOX),
	SANDBOX_EVENTS("sandboxevent", GameMode.SANDBOX),

	SSTUNICATE1("sstunicate1", GameText.LEVEL_HUB, GameText.HUB_DESC, false, MapSize.MEDIUM, GameMode.HUB),
	HUB_MULTI("sstunicate2", GameText.LEVEL_HUB, GameText.HUB_DESC, true, MapSize.MEDIUM, GameMode.HUB),

	;
	
	//the level's filename
	private final String map;

	//information about the map
	private final GameText name, desc;
	private final Array<UnlockTag> tags = new Array<>();

	private final String imageFile;

	//these are modes that this map can be selected for
	private final GameMode[] modes;

	private final MapSize size;

	UnlockLevel(String map, GameText name, GameText desc, boolean multiplayer, MapSize size, GameMode... modes) {
		this.map = getMapFileName(map);
		this.name = name;
		this.desc = desc;
		this.imageFile = map;
		this.size = size;
		this.modes = modes;

		if (multiplayer) {
			tags.add(UnlockTag.MULTIPLAYER);
		} else {
			tags.add(UnlockTag.NAVIGATIONS);
		}
	}

	UnlockLevel(String map, GameMode... modes) {
		this(map, GameText.NOTHING, GameText.NOTHING, false, MapSize.MEDIUM, modes);
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

	public TextureRegion getIcon() {
		return ((TextureAtlas) HadalGame.assetManager.get(AssetList.MAP_ICONS.toString())).findRegion(imageFile);
	}

	private String getMapFileName(String filename) {
		return "maps/" + filename + ".tmx";
	}

	public String getMap() { return map; }

	public GameMode[] getModes() { return modes; }

	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	public MapSize getSize() { return size; }

	private static final HashMap<String, UnlockLevel> UnlocksByName = new HashMap<>();
	static {
		for (UnlockLevel u : UnlockLevel.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockLevel getByName(String s) {
		return UnlocksByName.getOrDefault(s, HUB_MULTI);
	}

	public enum MapSize {

		TINY(UIText.TINY, 2),
		SMALL(UIText.SMALL, 4),
		MEDIUM(UIText.MEDIUM, 6),
		LARGE(UIText.LARGE, 8),
		EXTRA_LARGE(UIText.EXTRA_LARGE, 10),
		GIANT(UIText.GIANT, 12)

		;

		private final String sizeName;
		private final int preferredPlayers;

		MapSize(UIText sizeName, int preferredPlayers) {
			this.sizeName = sizeName.text();
			this.preferredPlayers = preferredPlayers;
		}

		public String getSizeName() { return sizeName; }

		public int getPreferredPlayers() { return preferredPlayers;	}
	}
}
