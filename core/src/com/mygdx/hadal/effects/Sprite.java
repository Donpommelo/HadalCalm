package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;

public enum Sprite {

	BOOM(SpriteType.EXPLOSION, "boom"),
	ORB_BLUE(SpriteType.PROJECTILE, "orb_blue"),
	ORB_PINK(SpriteType.PROJECTILE, "orb_pink"),
	ORB_ORANGE(SpriteType.PROJECTILE, "orb_orange"),
	ORB_RED(SpriteType.PROJECTILE, "orb_red"),
	ORB_YELLOW(SpriteType.PROJECTILE, "orb_yellow"),
	SPIT(SpriteType.PROJECTILE, "spit"),
	HARPOON(SpriteType.PROJECTILE, "harpoon"),
	BULLET(SpriteType.PROJECTILE, "bullet"),
	SCRAP_A(SpriteType.PROJECTILE, "debris_a"),
	SCRAP_B(SpriteType.PROJECTILE, "debris_b"),
	SCRAP_C(SpriteType.PROJECTILE, "debris_c"),
	SCRAP_D(SpriteType.PROJECTILE, "debris_d"),
	GRENADE(SpriteType.PROJECTILE, "grenade"),
	TORPEDO(SpriteType.PROJECTILE, "torpedo"),
	BUZZSAW(SpriteType.PROJECTILE, "bouncing_blade"),
	BOOMERANG(SpriteType.PROJECTILE, "boomerang"),
	CANNONBALL(SpriteType.PROJECTILE, "iron_ball"),
	BEE(SpriteType.PROJECTILE, "bee"),
	
	FUEL(SpriteType.EVENT, "event_fuel"),
	MEDPAK(SpriteType.EVENT, "event_health"),

	NASU(SpriteType.EVENT, "eggplant"),
	PYRAMID(SpriteType.EVENT, "event_pyramid"),
	CUBE(SpriteType.EVENT, "event_cube"),
	BASE(SpriteType.EVENT, "event_base"),
	SPRING(SpriteType.EVENT, "event_spring"),
	PORTAL(SpriteType.EVENT, "portal"),
	LEVER(SpriteType.EVENT, "lever"),
	BASE_RED(SpriteType.EVENT, "lever_base_red"),
	BASE_GREEN(SpriteType.EVENT, "lever_base_green"),

	MT_DEFAULT(SpriteType.WEAPON, "default"),
	MT_SCRAPRIPPER(SpriteType.WEAPON, "scrapripper"),
	MT_BEEGUN(SpriteType.WEAPON, "beegun"),
	MT_BOILER(SpriteType.WEAPON, "boiler"),
	MT_BOOMERANG(SpriteType.WEAPON, "boomeranglauncher"),
	MT_BLADEGUN(SpriteType.WEAPON, "bladegun"),
	MT_CHAINLIGHTNING(SpriteType.WEAPON, "tractorbeam"),
	MT_CHARGEBEAM(SpriteType.WEAPON, "chargebeam"),
	MT_GRENADE(SpriteType.WEAPON, "grenadelauncher"),
	MT_ICEBERG(SpriteType.WEAPON, "iceberggun"),
	MT_IRONBALL(SpriteType.WEAPON, "cannon"),
	MT_LASERROCKET(SpriteType.WEAPON, "smartrocketlauncher"),
	MT_LASERRIFLE(SpriteType.WEAPON, "laserrifle"),
	MT_MACHINEGUN(SpriteType.WEAPON, "machinegun"),
	MT_NEMATOCYTEARM(SpriteType.WEAPON, "nematocytearm"),
	MT_SHOTGUN(SpriteType.WEAPON, "scattergun"),
	MT_SLODGEGUN(SpriteType.WEAPON, "slodgegun"),
	MT_SPEARGUN(SpriteType.WEAPON, "speargun"),
	MT_STICKYBOMB(SpriteType.WEAPON, "stickybomblauncher"),
	MT_STORMCALLER(SpriteType.WEAPON, "stormcaller"),
	MT_TORPEDO(SpriteType.WEAPON, "torpedolauncher"),

	P_DEFAULT(SpriteType.EVENT, "event_default"),
	P_SCRAPRIPPER(SpriteType.EVENT, "event_scrapripper"),
	P_BEEGUN(SpriteType.EVENT, "event_beegun"),
	P_BOILER(SpriteType.EVENT, "event_boiler"),
	P_BOOMERANG(SpriteType.EVENT, "event_boomerang"),
	P_BLADEGUN(SpriteType.EVENT, "event_bladegun"),
	P_CHAINLIGHTNING(SpriteType.EVENT, "event_tractorbeam"),
	P_CHARGEBEAM(SpriteType.EVENT, "event_chargebeam"),
	P_GRENADE(SpriteType.EVENT, "event_grenadelauncher"),
	P_ICEBERG(SpriteType.EVENT, "event_iceberggun"),
	P_IRONBALL(SpriteType.EVENT, "event_cannon"),
	P_LASERROCKET(SpriteType.EVENT, "event_smartrocket"),
	P_LASERRIFLE(SpriteType.EVENT, "event_laserrifle"),
	P_MACHINEGUN(SpriteType.EVENT, "event_machinegun"),
	P_NEMATOCYTEARM(SpriteType.EVENT, "event_nematocytearm"),
	P_SHOTGUN(SpriteType.EVENT, "event_doublebarrel"),
	P_SLODGEGUN(SpriteType.EVENT, "event_slodgegun"),
	P_SPEARGUN(SpriteType.EVENT, "event_speargun"),
	P_STICKYBOMB(SpriteType.EVENT, "event_stickybomb"),
	P_STORMCALLER(SpriteType.EVENT, "event_stormcaller"),
	P_TORPEDO(SpriteType.EVENT, "event_torpedo"),

	FISH_SCISSOR(SpriteType.FISH, "scissorfish_swim"),
	FISH_SPITTLE(SpriteType.FISH, "spittlefish_swim"),
	FISH_TORPEDO(SpriteType.FISH, "torpedofish_swim"),
	
	TURRET_BASE(SpriteType.TURRET, "base"),
	TURRET_FLAK(SpriteType.TURRET, "flak"),
	TURRET_VOLLEY(SpriteType.TURRET, "volley"),
	
	//ui
	UI_MAIN_OVERLAY(SpriteType.UI, "UI_main_overlay"),
	UI_MAIN_RELOAD(SpriteType.UI, "UI_main_reloading"),
	UI_MAIN_HEALTHBAR(SpriteType.UI, "UI_main_healthbar"),
	UI_MAIN_HEALTH_LOW(SpriteType.UI, "UI_main_health_low"),
	UI_MAIN_HEALTH_MISSING(SpriteType.UI, "UI_main_healthmissing"),
	UI_MAIN_FUELBAR(SpriteType.UI, "UI_main_fuelbar"),
	UI_MAIN_FUEL_CUTOFF(SpriteType.UI, "UI_main_fuel_cutoff"),
	UI_MAIN_NULL(SpriteType.UI, "UI_main_null"),
	UI_MAIN_SELECTED(SpriteType.UI, "UI_main_selected"),
	UI_MAIN_UNSELECTED(SpriteType.UI, "UI_main_unselected"),

	UI_MO_BASE(SpriteType.UI, "UI_momentum_base"),
	UI_MO_READY(SpriteType.UI, "UI_momentum_ready"),
	UI_MO_OVERLAY(SpriteType.UI, "UI_momentum_overlay"),
	UI_MO_ARROW(SpriteType.UI, "UI_momentum_arrow"),

	UI_RELOAD(SpriteType.UI, "UI_reload"),
	UI_RELOAD_METER(SpriteType.UI, "UI_reload_meter"),
	UI_RELOAD_BAR(SpriteType.UI, "UI_reload_bar"),

	MOREAU_RUN(SpriteType.MOREAU, "body_run"),
	MOREAU_STILL(SpriteType.MOREAU, "body_stand"),
	MOREAU_BACK(SpriteType.MOREAU, "body_background"),
	MOREAU_HEAD(SpriteType.MOREAU, "head"),
	MOREAU_ARM(SpriteType.MOREAU, "arm"),
	MOREAU_GEM_ON(SpriteType.MOREAU, "gem_active"),
	MOREAU_GEM_OFF(SpriteType.MOREAU, "gem_inactive"),
	
	TELEMACHUS_RUN(SpriteType.TELEMACHUS, "body_run"),
	TELEMACHUS_STILL(SpriteType.TELEMACHUS, "body_stand"),
	TELEMACHUS_BACK(SpriteType.TELEMACHUS, "body_background"),
	TELEMACHUS_HEAD(SpriteType.TELEMACHUS, "head"),
	TELEMACHUS_ARM(SpriteType.TELEMACHUS, "arm"),
	TELEMACHUS_GEM_ON(SpriteType.TELEMACHUS, "gem_active"),
	TELEMACHUS_GEM_OFF(SpriteType.TELEMACHUS, "gem_inactive"),
	
	TAKANORI_RUN(SpriteType.TAKANORI, "body_run"),
	TAKANORI_STILL(SpriteType.TAKANORI, "body_stand"),
	TAKANORI_BACK(SpriteType.TAKANORI, "body_background"),
	TAKANORI_HEAD(SpriteType.TAKANORI, "head"),
	TAKANORI_ARM(SpriteType.TAKANORI, "arm"),
	TAKANORI_GEM_ON(SpriteType.TAKANORI, "gem_active"),
	TAKANORI_GEM_OFF(SpriteType.TAKANORI, "gem_inactive"),
	
	MOREAU_FESTIVE_RUN(SpriteType.MOREAU_FESTIVE, "body_run"),
	MOREAU_FESTIVE_STILL(SpriteType.MOREAU_FESTIVE, "body_stand"),
	MOREAU_FESTIVE_BACK(SpriteType.MOREAU_FESTIVE, "body_background"),
	MOREAU_FESTIVE_HEAD(SpriteType.MOREAU_FESTIVE, "head"),
	MOREAU_FESTIVE_ARM(SpriteType.MOREAU_FESTIVE, "arm"),
	MOREAU_FESTIVE_GEM_ON(SpriteType.MOREAU_FESTIVE, "gem_active"),
	MOREAU_FESTIVE_GEM_OFF(SpriteType.MOREAU_FESTIVE, "gem_inactive"),
	
	//Misc stuff from totlc
	IMPACT(SpriteType.IMPACT, "impact"),
	EXCLAMATION(SpriteType.EXCLAMATION, "exclamation"),
	STAR(SpriteType.STAR, "starshot"),
	;
	
	private SpriteType type;
	private String spriteId;
	private Array<? extends TextureRegion> frames;
	
	Sprite(SpriteType type, String spriteId) {
		this.type = type;
		this.spriteId = spriteId;
	}

	public Array<? extends TextureRegion> getFrames() {
		
		if (frames == null) {
			frames = getAtlas(type).findRegions(spriteId);
		}
		
		return frames;
	}
	
	public TextureRegion getFrame() {
		return getFrames().get(0);
	}
	
	public static TextureAtlas getAtlas(SpriteType type) {
		switch (type) {
		case EVENT:
			return HadalGame.assetManager.get(AssetList.EVENT_ATL.toString());
		case EXPLOSION:
			return HadalGame.assetManager.get(AssetList.BOOM_1_ATL.toString());
		case PROJECTILE:
			return HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString());
		case WEAPON:
			return HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		case FISH:
			return HadalGame.assetManager.get(AssetList.FISH_ATL.toString());
		case TURRET:
			return HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
		case IMPACT:
			return HadalGame.assetManager.get(AssetList.IMPACT_ATLAS.toString());
		case EXCLAMATION:
			return HadalGame.assetManager.get(AssetList.EXCLAMATION_ATLAS.toString());
		case STAR:
			return HadalGame.assetManager.get(AssetList.STAR_SHOT_ATLAS.toString());
		case UI:
			return HadalGame.assetManager.get(AssetList.UI_ATL.toString());
		case MOREAU:
			return HadalGame.assetManager.get(AssetList.PLAYER_MOREAU_ATL.toString());
		case TAKANORI:
			return HadalGame.assetManager.get(AssetList.PLAYER_TAKA_ATL.toString());
		case TELEMACHUS:
			return HadalGame.assetManager.get(AssetList.PLAYER_TELE_ATL.toString());
		case MOREAU_FESTIVE:
			return HadalGame.assetManager.get(AssetList.PLAYER_MOREAU_FESTIVE_ATL.toString());
		default:
			return null;
		}
	}
	
	public static Sprite getCharacterSprites(SpriteType character, String part) {
		for (Sprite s: Sprite.values() ) {
			if (s.type.equals(character) && s.spriteId.equals(part)) {
				return s;
			}
		}
		return null;
	}
	
	public enum SpriteType {
		PROJECTILE,
		EXPLOSION,
		EVENT,
		WEAPON,
		FISH,
		TURRET,
		IMPACT,
		EXCLAMATION,
		STAR,
		UI,
		
		MOREAU,
		TAKANORI,
		TELEMACHUS,
		MOREAU_FESTIVE
	}
}
