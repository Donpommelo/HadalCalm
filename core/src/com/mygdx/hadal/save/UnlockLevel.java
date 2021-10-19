package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.map.GameMode;
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

	DM_BOTTLENECK("Maps/dm_bottleneck.tmx", GameMode.DEATHMATCH),
	DM_BROUHAHA("Maps/dm_brouhaha.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_FACILITY("Maps/dm_facility.tmx", GameMode.DEATHMATCH),
	DM_FLOTSAM("Maps/dm_flotsam.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_LAGAN("Maps/dm_lagan.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_THRESHOLD("Maps/dm_threshold.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_TOWERS("Maps/dm_towers.tmx", GameMode.DEATHMATCH),
	DM_WARP("Maps/dm_warp.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_QUADRANT("Maps/dm_quadrant.tmx", GameMode.DEATHMATCH),
	DM_WETWORKS("Maps/dm_wetworks.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),

	CTF_COQUELICOT("Maps/ctf_coquelicot.tmx", GameMode.CTF, GameMode.DEATHMATCH),
	CTF_FILTRATION("Maps/ctf_filtration.tmx", GameMode.CTF, GameMode.DEATHMATCH),

	BOSS1("Maps/Boss1.tmx", GameMode.BOSS),
	BOSS2("Maps/Boss2.tmx", GameMode.BOSS),
	BOSS3("Maps/Boss3.tmx", GameMode.BOSS),
	BOSS4("Maps/Boss4.tmx", GameMode.BOSS),
	BOSS5("Maps/Boss5.tmx", GameMode.BOSS),
	BOSS6("Maps/Boss6.tmx", GameMode.BOSS),

	DM_AGGYDAGGY("Maps/dm_aggydaggy.tmx", GameMode.DEATHMATCH),
	DM_CAROUSEL("Maps/dm_carousel.tmx", GameMode.DEATHMATCH),
	DM_FALLDOWN("Maps/dm_falldown.tmx", GameMode.DEATHMATCH),
	DM_GULLY("Maps/dm_gully.tmx", GameMode.DEATHMATCH),
	DM_JUMP("Maps/dm_jump.tmx", GameMode.DEATHMATCH),
	DM_MOONBOUNCE("Maps/dm_moonbounce.tmx", GameMode.DEATHMATCH),
	DM_ORIGINAL("Maps/dm_original.tmx", GameMode.DEATHMATCH),
	DM_PILE("Maps/dm_pile.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_PINWHEEL("Maps/dm_pinwheel.tmx", GameMode.DEATHMATCH),
	DM_PIVOT("Maps/dm_pivot.tmx", GameMode.DEATHMATCH),
	DM_SAWMILL("Maps/dm_sawmill.tmx", GameMode.DEATHMATCH),
	DM_SCALES("Maps/dm_scales.tmx", GameMode.DEATHMATCH),
	DM_SEESAW("Maps/dm_seesaw.tmx", GameMode.DEATHMATCH),
	DM_ZIGGURAT("Maps/dm_ziggurat.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),

	DM_FORMOSAN_BLUE_MAGPIE("Maps/dm_formosan_blue_magpie.tmx", GameMode.DEATHMATCH),
	DM_FREE_RANGE("Maps/dm_free_range.tmx", GameMode.DEATHMATCH),
	DM_GREY_HERON("Maps/dm_grey_heron.tmx", GameMode.DEATHMATCH),
	DM_HUMMINGBIRD("Maps/dm_hummingbird.tmx", GameMode.DEATHMATCH),
	DM_JAPANESE_WHITEEYE("Maps/dm_japanese_whiteeye.tmx", GameMode.DEATHMATCH),
	DM_LITTLE_BITTERN("Maps/dm_little_bittern.tmx", GameMode.DEATHMATCH),
	DM_PELICAN("Maps/dm_pelican.tmx", GameMode.DEATHMATCH),
	DM_SUPREME_CHAMPION("Maps/dm_supreme_champion.tmx", GameMode.DEATHMATCH),
	
	DM_FACING_CRABS("Maps/dm_crab.tmx", GameMode.DEATHMATCH),
	DM_GALLERY("Maps/dm_gallery.tmx", GameMode.DEATHMATCH),
	DM_HORIZON("Maps/dm_horizon.tmx", GameMode.DEATHMATCH, GameMode.SURVIVAL),
	DM_REDROCK("Maps/dm_redrock.tmx", GameMode.DEATHMATCH),

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

	SANDBOX_HUB("Maps/sandboxhub.tmx", GameMode.SANDBOX),
	SANDBOX_ENEMY("Maps/sandboxenemy.tmx", GameMode.SANDBOX),
	SANDBOX_EVENTS("Maps/sandboxevent.tmx", GameMode.SANDBOX),

	FOOTBALL_GREEN("Maps/futbol_green.tmx", GameMode.FOOTBALL),

	SSTUNICATE1("Maps/sstunicate1.tmx", GameMode.HUB),
	HUB_MULTI("Maps/sstunicate2.tmx", GameMode.HUB),

	;
	
	//the level's filename
	private final String map;
	
	//information about the map
	private InfoItem info;

	//these are modes that this map can be selected for
	private final GameMode[] modes;

	UnlockLevel(String map, GameMode... modes) {
		this.map = map;
		this.modes = modes;
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
	 * Due to the number of single-player campaign "maps", info items for levels might be null.
	 * In these cases, we want to return an empty info item with the name for ui purposes
	 */
	public InfoItem getInfo() {
		if (info == null) {
			info = new InfoItem();
			info.setName(toString());
			info.setDescription("");
			info.setTags(new ArrayList<>());
		}
		return info;
	}

	public void setInfo(InfoItem info) { this.info = info; }
	
	public String getMap() { return map; }

	public GameMode[] getModes() { return modes; }

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
