package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

/**
 * An UnlockLevel represents a single level in the game
 * @author Yirlotte Yilyde
 */
public enum UnlockLevel {

	DM_BOTTLENECK("Maps/dm_bottleneck.tmx"),
	DM_BROUHAHA("Maps/dm_brouhaha.tmx"),
	DM_FACILITY("Maps/dm_facility.tmx"),
	DM_FLOTSAM("Maps/dm_flotsam.tmx"),
	DM_LAGAN("Maps/dm_lagan.tmx"),
	DM_THRESHOLD("Maps/dm_threshold.tmx"),
	DM_TOWERS("Maps/dm_towers.tmx"),
	DM_WARP("Maps/dm_warp.tmx"),
	DM_QUADRANT("Maps/dm_quadrant.tmx"),
	DM_WETWORKS("Maps/dm_wetworks.tmx"),

	BOSS1("Maps/Boss1.tmx"),
	BOSS2("Maps/Boss2.tmx"),
	BOSS3("Maps/Boss3.tmx"),
	BOSS4("Maps/Boss4.tmx"),
	BOSS5("Maps/Boss5.tmx"),

	RANDOM("Maps/sstunicate1.tmx"),
	
	ARENA_FLOTSAM("Maps/arena_flotsam.tmx"),
	ARENA_HORIZON("Maps/arena_horizon.tmx"),
	ARENA_LAGAN("Maps/arena_lagan.tmx"),
	ARENA_PILE("Maps/arena_pile.tmx"),
	ARENA_WARP("Maps/arena_warp.tmx"),
	ARENA_WETWORKS("Maps/arena_wetworks.tmx"),
	ARENA_ZIGGURAT("Maps/arena_ziggurat.tmx"),

	DM_CAROUSEL("Maps/dm_carousel.tmx"),
	DM_FALLDOWN("Maps/dm_falldown.tmx"),
	DM_GULLY("Maps/dm_gully.tmx"),
	DM_JUMP("Maps/dm_jump.tmx"),
	DM_MOONBOUNCE("Maps/dm_moonbounce.tmx"),
	DM_ORIGINAL("Maps/dm_original.tmx"),
	DM_PILE("Maps/dm_pile.tmx"),
	DM_PINWHEEL("Maps/dm_pinwheel.tmx"),
	DM_PIVOT("Maps/dm_pivot.tmx"),
	DM_SAWMILL("Maps/dm_sawmill.tmx"),
	DM_SCALES("Maps/dm_scales.tmx"),
	DM_SEESAW("Maps/dm_seesaw.tmx"),
	DM_ZIGGURAT("Maps/dm_ziggurat.tmx"),

	DM_FORMOSAN_BLUE_MAGPIE("Maps/dm_formosan_blue_magpie.tmx"),
	DM_FREE_RANGE("Maps/dm_free_range.tmx"),
	DM_GREY_HERON("Maps/dm_grey_heron.tmx"),
	DM_HUMMINGBIRD("Maps/dm_hummingbird.tmx"),
	DM_JAPANESE_WHITEEYE("Maps/dm_japanese_whiteeye.tmx"),
	DM_LITTLE_BITTERN("Maps/dm_little_bittern.tmx"),
	DM_PELICAN("Maps/dm_pelican.tmx"),
	DM_SUPREME_CHAMPION("Maps/dm_supreme_champion.tmx"),
	
	DM_FACING_CRABS("Maps/dm_crab.tmx"),
	DM_GALLERY("Maps/dm_gallery.tmx"),
	DM_HORIZON("Maps/dm_horizon.tmx"),
	DM_REDROCK("Maps/dm_redrock.tmx"),

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

	SANDBOX_HUB("Maps/sandboxhub.tmx"),
	SANDBOX_ENEMY("Maps/sandboxenemy.tmx"),
	SANDBOX_EVENTS("Maps/sandboxevent.tmx"),

	SSTUNICATE1("Maps/sstunicate1.tmx"),
	HUB_MULTI("Maps/sstunicate2.tmx"),

	;
	
	//the level's filename
	private final String map;
	
	//information about the map
	private InfoItem info;
	
	//does the map have any preset loadout information?
	private UnlockEquip[] multitools;
	private UnlockArtifact[] artifacts;
	private UnlockActives activeItem;
	
	UnlockLevel(String map, UnlockArtifact[] artifacts, UnlockActives activeItem, UnlockEquip[] multitools) {
		this(map);
		this.artifacts = artifacts;
		this.activeItem = activeItem;
		this.multitools = multitools;
	}
	
	UnlockLevel(String map) {
		this.map = map;
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
			UnlockLevel level = dm.get(GameStateManager.generator.nextInt(dm.size));
			
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
	
	public UnlockEquip[] getMultitools() { return multitools; }

	public void setMultitools(UnlockEquip[] multitools) { this.multitools = multitools; }

	public UnlockArtifact[] getArtifacts() { return artifacts; }

	public void setArtifacts(UnlockArtifact[] artifacts) { this.artifacts = artifacts; }

	public UnlockActives getActiveItem() { return activeItem; }

	public void setActiveItem(UnlockActives activeItem) { this.activeItem = activeItem; }	
}
