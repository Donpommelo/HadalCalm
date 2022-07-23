package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
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

	BOSS_FISH("maps/Boss1.tmx", GameText.BOSS_FISH, GameText.BOSS_FISH_DESC, true,
			GameMode.BOSS),
	BOSS_KING_KAMABOKO("maps/Boss2.tmx", GameText.BOSS_KING_KAMABOKO, GameText.BOSS_BOSS_KING_KAMABOKO_DESC, true,
			GameMode.BOSS),
	BOSS_FALSE_SUN("maps/Boss4.tmx", GameText.BOSS_FALSE_SUN, GameText.BOSS_FALSE_SUN_DESC, true,
			GameMode.BOSS),
	BOSS_NEPTUNE_KING("maps/Boss5.tmx", GameText.BOSS_NEPTUNE_KING, GameText.BOSS_NEPTUNE_KING_DESC, true,
			GameMode.BOSS),
	BOSS_GILT_SCALED_SERAPH("maps/Boss6.tmx", GameText.BOSS_GILT_SCALED_SERAPH, GameText.BOSS_BOSS_GILT_SCALED_SERAPH_DESC, true,
			GameMode.BOSS),

	AGGYDAGGY("maps/dm_aggydaggy.tmx", GameText.AGGYDAGGY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	BOTTLENECK("maps/dm_bottleneck.tmx", GameText.BOTTLENECK, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	BROUHAHA("maps/dm_brouhaha.tmx", GameText.BROUHAHA, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	COQUELICOT("maps/ctf_coquelicot.tmx", GameText.COQUELICOT, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	FACILITY("maps/dm_facility.tmx", GameText.FACILITY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	FILTRATION("maps/ctf_filtration.tmx", GameText.FILTRATION, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	FLOTSAM("maps/dm_flotsam.tmx", GameText.FLOTSAM, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	GREEN("maps/futbol_green.tmx", GameText.GREEN, GameText.NOTHING, true,
			GameMode.FOOTBALL, GameMode.DEATHMATCH),
	LAGAN("maps/dm_lagan.tmx", GameText.LAGAN, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	THRESHOLD("maps/dm_threshold.tmx", GameText.THRESHOLD, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	TOWERS("maps/dm_towers.tmx", GameText.TOWERS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	UNDULATE("maps/ctf_undulate.tmx", GameText.UNDULATE, GameText.NOTHING, true,
			GameMode.CTF, GameMode.DEATHMATCH),
	WARP("maps/dm_warp.tmx", GameText.WARP, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	QUADRANT("maps/dm_quadrant.tmx", GameText.QUADRANTS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	WETWORKS("maps/dm_wetworks.tmx", GameText.WETWORKS, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	ZIGGURAT("maps/dm_ziggurat.tmx", GameText.ZIGGURAT, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),

	FORMOSAN_BLUE_MAGPIE("maps/dm_formosan_blue_magpie.tmx", GameText.FORMOSAN_BLUE_MAGPIE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	FREE_RANGE("maps/dm_free_range.tmx", GameText.FREE_RANGE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	GREY_HERON("maps/dm_grey_heron.tmx", GameText.GREY_HERON, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	HUMMINGBIRD("maps/dm_hummingbird.tmx", GameText.HUMMINGBIRD, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	JAPANESE_WHITEEYE("maps/dm_japanese_whiteeye.tmx", GameText.JAPANESE_WHITEEYE, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	LITTLE_BITTERN("maps/dm_little_bittern.tmx", GameText.LITTLE_BITTERN, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	PELICAN("maps/dm_pelican.tmx", GameText.PELICAN, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	SUPREME_CHAMPION("maps/dm_supreme_champion.tmx", GameText.SUPREME_CHAMPION, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	
	FACING_CRABS("maps/dm_crab.tmx", GameText.FACING_CRABS, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	GALLERY("maps/dm_gallery.tmx", GameText.GALLERY, GameText.NOTHING, true,
			GameMode.DEATHMATCH),
	HORIZON("maps/dm_horizon.tmx", GameText.HORIZON, GameText.NOTHING, true,
			GameMode.DEATHMATCH, GameMode.SURVIVAL),
	REDROCK("maps/dm_redrock.tmx", GameText.REDROCK, GameText.NOTHING, true,
			GameMode.DEATHMATCH),

	FALLDOWN("maps/dm_falldown.tmx", GameText.FALLDOWN, GameText.NOTHING, true, GameMode.DEATHMATCH),
	MOONBOUNCE("maps/dm_moonbounce.tmx", GameText.MOONBOUNCE, GameText.NOTHING, true, GameMode.DEATHMATCH),
	PINWHEEL("maps/dm_pinwheel.tmx", GameText.PINWHEEL, GameText.NOTHING, true, GameMode.DEATHMATCH),
	PIVOT("maps/dm_pivot.tmx", GameText.PIVOT, GameText.NOTHING, true, GameMode.DEATHMATCH),
	//	CAROUSEL("maps/dm_carousel.tmx", GameMode.DEATHMATCH),
	//	GULLY("maps/dm_gully.tmx", GameMode.DEATHMATCH),
	//	JUMP("maps/dm_jump.tmx", GameMode.DEATHMATCH),
	//	ORIGINAL("maps/dm_original.tmx", GameMode.DEATHMATCH),
	//	PILE("maps/dm_pile.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	//	SAWMILL("maps/dm_sawmill.tmx", GameMode.DEATHMATCH),
	//	SCALES("maps/dm_scales.tmx", GameMode.DEATHMATCH),
	//	SEESAW("maps/dm_seesaw.tmx", GameMode.DEATHMATCH),

	WRECK1("maps/wreck1.tmx", GameMode.CAMPAIGN),
	WRECK2("maps/wreck2.tmx", GameMode.CAMPAIGN),
	WRECK3("maps/wreck3.tmx", GameMode.CAMPAIGN),
	WRECK4("maps/wreck4.tmx", GameMode.CAMPAIGN),
	DERELICT1("maps/derelict1.tmx", GameMode.CAMPAIGN),
	DERELICT2("maps/derelict2.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER1("maps/plenumchamber1.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER2("maps/plenumchamber2.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER3("maps/plenumchamber3.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER4("maps/plenumchamber4.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER5("maps/plenumchamber5.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBER6("maps/plenumchamber6.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERMAZE("maps/plenumchambermaze.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERSHAFT("maps/plenumchambershaft.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBEREXTRA1("maps/plenumchamberextra1.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERTURBINE("maps/plenumchamberturbine.tmx", GameMode.CAMPAIGN),
	PLENUMCHAMBERBALCONY("maps/plenumchamberbalcony.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA1("maps/noiselesssea1.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA2("maps/noiselesssea2.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA3("maps/noiselesssea3.tmx", GameMode.CAMPAIGN),
	NOISELESSSEA4("maps/noiselesssea4.tmx", GameMode.CAMPAIGN),
	NOISELESSSEAEXTRA1("maps/noiselessseaextra1.tmx", GameMode.CAMPAIGN),
	SLUICE1("maps/sluice1.tmx", GameMode.CAMPAIGN),
	SLUICEBOSS("maps/sluiceboss.tmx", GameMode.CAMPAIGN),
	PARTY("maps/party.tmx", GameMode.CAMPAIGN),

	SANDBOX_HUB("maps/sandboxhub.tmx", GameText.SANDBOX, GameText.SANDBOX_DESC, true, GameMode.SANDBOX),
	SANDBOX_ENEMY("maps/sandboxenemy.tmx", GameMode.SANDBOX),
	SANDBOX_EVENTS("maps/sandboxevent.tmx", GameMode.SANDBOX),

	SSTUNICATE1("maps/sstunicate1.tmx", GameText.LEVEL_HUB, GameText.HUB_DESC, false, GameMode.HUB),
	HUB_MULTI("maps/sstunicate2.tmx", GameText.LEVEL_HUB, GameText.HUB_DESC, true, GameMode.HUB),

	;
	
	//the level's filename
	private final String map;

	//information about the map
	private final GameText name, desc;
	private final Array<UnlockTag> tags = new Array<>();

	private final String imageFile;
	private TextureRegion imageIcon;

	//these are modes that this map can be selected for
	private final GameMode[] modes;

	UnlockLevel(String map, GameText name, GameText desc, boolean multiplayer, String imageFile, GameMode... modes) {
		this.map = map;
		this.name = name;
		this.desc = desc;
		this.imageFile = imageFile;
		this.modes = modes;

		if (multiplayer) {
			tags.add(UnlockTag.MULTIPLAYER);
		} else {
			tags.add(UnlockTag.NAVIGATIONS);
		}

		imageIcon = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.MAP_TEMP.toString()));
	}
	UnlockLevel(String map, GameText name, GameText desc, boolean multiplayer, GameMode... modes) {
		this(map, name, desc, multiplayer, "", modes);
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

	public TextureRegion getIcon() {
		return imageIcon;
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
