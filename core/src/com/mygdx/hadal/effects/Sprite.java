package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.states.PlayState;

/**
 * This is a single sprite that can be drawn
 * @author Zachary Tu
 */
public enum Sprite {
	NOTHING(SpriteType.MISC, ""),
	
	ANCHOR(SpriteType.PROJECTILE, "anchor"),
	ARROW(SpriteType.PROJECTILE, "arrow"),
	BANANA(SpriteType.PROJECTILE, "banana"),
	BEE(SpriteType.PROJECTILE, "bee"),
	BOOM(SpriteType.EXPLOSION, "boom"),
	BOOMERANG(SpriteType.PROJECTILE, "boomerang"),
	BULLET(SpriteType.PROJECTILE, "bullet"),
	BUZZSAW(SpriteType.PROJECTILE, "bouncing_blade"),
	CANNONBALL(SpriteType.PROJECTILE, "iron_ball"),
	CHARGE_BEAM(SpriteType.PROJECTILE, "charge_beam_a"),
	CLOSED_HAND(SpriteType.PROJECTILE, "closed_hand"),
	COLA(SpriteType.PROJECTILE, "cola"),
	CORK(SpriteType.PROJECTILE, "cork"),
	CROSSHAIR(SpriteType.PROJECTILE, "crosshair"),
	FLAIL(SpriteType.PROJECTILE, "flail"),
	FLOUNDER_A(SpriteType.PROJECTILE, "flounder_a"),
	FLOUNDER_B(SpriteType.PROJECTILE, "flounder_b"),
	FUGU(SpriteType.PROJECTILE, "fugu"),
	DRILL(SpriteType.PROJECTILE, "underminer"),
	GRENADE(SpriteType.PROJECTILE, "grenade"),
	HARPOON(SpriteType.PROJECTILE, "harpoon"),
	HURRICANE(SpriteType.PROJECTILE, "storm"),
	ICEBERG(SpriteType.PROJECTILE, "iceberg"),
	LAND_MINE(SpriteType.PROJECTILE, "landmine"),
	LASER(SpriteType.PROJECTILE, "laser_projectile"),
	LASER_BEAM(SpriteType.PROJECTILE, "laser_beam"),
	LASER_BLUE(SpriteType.PROJECTILE, "laser_beam_blue"),
	LASER_GREEN(SpriteType.PROJECTILE, "laser_beam_green"),
	LASER_ORANGE(SpriteType.PROJECTILE, "laser_beam_orange"),
	LASER_PURPLE(SpriteType.PROJECTILE, "laser_beam_purple"),
	LASER_TURQUOISE(SpriteType.PROJECTILE, "laser_beam_turquoise"),
	LIGHTNING(SpriteType.PROJECTILE, "vajra"),
	METEOR_A(SpriteType.PROJECTILE, "meteor_a"),
	METEOR_B(SpriteType.PROJECTILE, "meteor_b"),
	METEOR_C(SpriteType.PROJECTILE, "meteor_c"),
	METEOR_D(SpriteType.PROJECTILE, "meteor_d"),
	METEOR_E(SpriteType.PROJECTILE, "meteor_e"),
	METEOR_F(SpriteType.PROJECTILE, "meteor_f"),
	MISSILE_A(SpriteType.PROJECTILE, "missile_a"),
	MISSILE_B(SpriteType.PROJECTILE, "missile_b"),
	MISSILE_C(SpriteType.PROJECTILE, "missile_c"),
	NAVAL_MINE(SpriteType.PROJECTILE, "navalmine"),
	NEMATOCYTE(SpriteType.PROJECTILE, "nematocyte", PlayMode.LOOP_PINGPONG, PlayState.spriteAnimationSpeed),
	ORB_BLUE(SpriteType.PROJECTILE, "orb_blue"),
	ORB_PINK(SpriteType.PROJECTILE, "orb_pink"),
	ORB_ORANGE(SpriteType.PROJECTILE, "orb_orange"),
	ORB_RED(SpriteType.PROJECTILE, "orb_red"),
	ORB_YELLOW(SpriteType.PROJECTILE, "orb_yellow"),
	OPEN_HAND(SpriteType.PROJECTILE, "open_hand"),
	PEARL(SpriteType.PROJECTILE, "pearl"),
	POPPER(SpriteType.PROJECTILE, "party"),
	PUNCH(SpriteType.PROJECTILE, "punch"),
	PYLON(SpriteType.PROJECTILE, "tesla", PlayMode.NORMAL, PlayState.spriteAnimationSpeed),
	SCRAP_A(SpriteType.PROJECTILE, "debris_a"),
	SCRAP_B(SpriteType.PROJECTILE, "debris_b"),
	SCRAP_C(SpriteType.PROJECTILE, "debris_c"),
	SCRAP_D(SpriteType.PROJECTILE, "debris_d"),
	SLAG(SpriteType.PROJECTILE, "slag"),
	SPIT(SpriteType.PROJECTILE, "spit"),
	SPLITTER_A(SpriteType.PROJECTILE, "splitter1"),
	SPLITTER_B(SpriteType.PROJECTILE, "splitter0"),
	STAR_BLUE(SpriteType.PROJECTILE, "blue_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_PURPLE(SpriteType.PROJECTILE, "purple_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_RED(SpriteType.PROJECTILE, "red_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_YELLOW(SpriteType.PROJECTILE, "yellow_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STICKYBOMB(SpriteType.PROJECTILE, "sticky"),
	TORPEDO(SpriteType.PROJECTILE, "torpedo"),
	TRICKBULLET(SpriteType.PROJECTILE, "trick"),
	
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
	KAMABOKO_BODY(SpriteType.KAMABOKO, "king_kamaboko"),
	KAMABOKO_CRAWL(SpriteType.KAMABOKO_CRAWL, "crawlaboko"),
	KAMABOKO_SWIM(SpriteType.KAMABOKO_SWIM, "swimaboko"),
	KAMABOKO_FACE(SpriteType.KAMABOKO, "large_face"),
	DRONE_ARM_BACK(SpriteType.DRONE, "drone_arm_back"),
	DRONE_ARM_FRONT(SpriteType.DRONE, "drone_arm_front"),
	DRONE_BODY(SpriteType.DRONE, "drone_body"),
	DRONE_EYE(SpriteType.DRONE, "drone_eye"),
	DRONE_DOT(SpriteType.DRONE, "drone_dot"),
	
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

	NOTIFICATIONS_CHAT(SpriteType.NOTIFICATIONS, "talking"),
	NOTIFICATIONS_CLEAR_CIRCLE(SpriteType.NOTIFICATIONS, "clear_circle"),
	NOTIFICATIONS_DIRECTIONAL_ARROW(SpriteType.NOTIFICATIONS, "arrow_directional"),
	NOTIFICATIONS_ALERT(SpriteType.NOTIFICATIONS, "alert_red"),
	NOTIFICATIONS_ALERT_PING(SpriteType.NOTIFICATIONS, "arrow_ping_red", PlayMode.LOOP_PINGPONG, PlayState.spriteAnimationSpeed),
	CLEAR_CIRCLE_ALERT(SpriteType.NOTIFICATIONS, "clear_circle_alert"),
	CLEAR_CIRCLE_EGGPLANT(SpriteType.NOTIFICATIONS, "clear_circle_eggplant"),

	MAXIMILLIAN_RUN(SpriteType.MAXIMILLIAN, "body_run"),
	MAXIMILLIAN_STILL(SpriteType.MAXIMILLIAN, "body_stand"),
	MAXIMILLIAN_BACK(SpriteType.MAXIMILLIAN, "body_background"),
	MAXIMILLIAN_HEAD(SpriteType.MAXIMILLIAN, "head"),
	MAXIMILLIAN_ARM(SpriteType.MAXIMILLIAN, "arm"),
	MAXIMILLIAN_GEM_ON(SpriteType.MAXIMILLIAN, "gem_active"),
	MAXIMILLIAN_GEM_OFF(SpriteType.MAXIMILLIAN, "gem_inactive"),
	
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
	
	MOREAU_PARTY_RUN(SpriteType.MOREAU_PARTY, "body_run"),
	MOREAU_PARTY_STILL(SpriteType.MOREAU_PARTY, "body_stand"),
	MOREAU_PARTY_BACK(SpriteType.MOREAU_PARTY, "body_background"),
	MOREAU_PARTY_HEAD(SpriteType.MOREAU_PARTY, "head"),
	MOREAU_PARTY_ARM(SpriteType.MOREAU_PARTY, "arm"),
	MOREAU_PARTY_GEM_ON(SpriteType.MOREAU_PARTY, "gem_active"),
	MOREAU_PARTY_GEM_OFF(SpriteType.MOREAU_PARTY, "gem_inactive"),
	
	TELEMACHUS_POINT(SpriteType.TELEMACHUS_POINT, null),
	
	//Misc stuff from totlc
	IMPACT(SpriteType.IMPACT, "impact"),
	EXCLAMATION(SpriteType.EXCLAMATION, "exclamation", PlayMode.NORMAL, PlayState.spriteAnimationSpeedSlow),
	STAR(SpriteType.STAR, "starshot"),
	;
	
	//this represents the atlas that we should read the sprite off of.
	private SpriteType type;
	
	//this is the filename of the sprite
	private String spriteId;
	
	//These are the frames of the sprite.
	private Array<? extends TextureRegion> frames;
	
	//how is this sprite animated? and how fast?
	private PlayMode playMode = PlayMode.LOOP;
	private float animationSpeed = PlayState.spriteAnimationSpeedFast;
	
	/**
	 * This constructor is used for sprites that are animated differently.
	 */
	Sprite(SpriteType type, String spriteId, PlayMode playMode, float animationSpeed) {
		this(type, spriteId);
		this.playMode = playMode;
		this.animationSpeed = animationSpeed;
	}
	
	Sprite(SpriteType type, String spriteId) {
		this.type = type;
		this.spriteId = spriteId;
	}

	/**
	 * This returns the frames of a given sprite
	 */
	public Array<? extends TextureRegion> getFrames() {

		if (this.equals(NOTHING)) {
			return null;
		}
		
		if (frames == null) {
			if (spriteId == null) {
				frames = getAtlas(type).getRegions();
			} else {
				frames = getAtlas(type).findRegions(spriteId);
			}
		}
		
		return frames;
	}
	
	public TextureRegion getFrame() {
		return getFrames().get(0);
	}
	
	/**
	 * Sprite Types refers to which atlas is used to procure the frames.
	 */
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
		case NOTIFICATIONS:
			return HadalGame.assetManager.get(AssetList.NOTIFICATION_ATL.toString());
		case MAXIMILLIAN:
			return HadalGame.assetManager.get(AssetList.PLAYER_MAXIMILLIAN_ATL.toString());
		case MOREAU:
			return HadalGame.assetManager.get(AssetList.PLAYER_MOREAU_ATL.toString());
		case TAKANORI:
			return HadalGame.assetManager.get(AssetList.PLAYER_TAKA_ATL.toString());
		case TELEMACHUS:
			return HadalGame.assetManager.get(AssetList.PLAYER_TELE_ATL.toString());
		case MOREAU_FESTIVE:
			return HadalGame.assetManager.get(AssetList.PLAYER_MOREAU_FESTIVE_ATL.toString());
		case MOREAU_PARTY:
			return HadalGame.assetManager.get(AssetList.PLAYER_MOREAU_PARTY_ATL.toString());
		case KAMABOKO:
			return HadalGame.assetManager.get(AssetList.KAMABOKO_ATL.toString());
		case KAMABOKO_CRAWL:
			return HadalGame.assetManager.get(AssetList.KAMABOKO_CRAWL_ATL.toString());
		case KAMABOKO_SWIM:
			return HadalGame.assetManager.get(AssetList.KAMABOKO_SWIM_ATL.toString());
		case DRONE:
			return HadalGame.assetManager.get(AssetList.DRONE_ATL.toString());
		case TELEMACHUS_POINT:
			return HadalGame.assetManager.get(AssetList.TELEMACHUS_POINT.toString());
		default:
			return null;
		}
	}
	
	/**
	 * This is used to get the different sprite parts of the player character
	 * @param character: the character we are getting the part of
	 * @param part: this string is the name of the body part.
	 */
	public static Sprite getCharacterSprites(SpriteType character, String part) {
		for (Sprite s: Sprite.values() ) {
			
			if (s.type.equals(character) && s.spriteId.equals(part)) {
				return s;
			}
		}
		return null;
	}
	
	public PlayMode getPlayMode() {	return playMode; }

	public float getAnimationSpeed() {	return animationSpeed; }

	public enum SpriteType {
		MISC,
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
		NOTIFICATIONS,
		KAMABOKO,
		KAMABOKO_CRAWL,
		KAMABOKO_SWIM,
		DRONE,
		TELEMACHUS_POINT,
		
		MAXIMILLIAN,
		MOREAU,
		TAKANORI,
		TELEMACHUS,
		MOREAU_FESTIVE,
		MOREAU_PARTY
	}
}
