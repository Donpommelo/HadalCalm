package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.states.PlayState;

import java.util.Objects;

/**
 * This is a single sprite that can be drawn.
 * More importantly, this can be serialized and sent to the client to tell them what sprite to draw an illusion with.
 * @author Frungo Friblatt
 */
public enum Sprite {
	NOTHING(SpriteType.MISC, ""),
	
	ANCHOR(SpriteType.PROJECTILE, "anchor"),
	ARROW(SpriteType.PROJECTILE, "arrow"),
	BANANA(SpriteType.PROJECTILE, "banana"),
	BEAN(SpriteType.PROJECTILE, "bean"),
	BEE(SpriteType.PROJECTILE, "bee"),
	BOMB(SpriteType.PROJECTILE, "bomb", PlayMode.NORMAL, 0.6f),
	BOOM(SpriteType.EXPLOSION, "boom"),
	BOOMERANG(SpriteType.PROJECTILE, "boomerang"),
	BULLET(SpriteType.PROJECTILE, "bullet"),
	BUZZSAW(SpriteType.PROJECTILE, "bouncing_blade"),
	CABER(SpriteType.PROJECTILE, "caber"),
	CANNONBALL(SpriteType.PROJECTILE, "iron_ball"),
	CHARGE_BEAM(SpriteType.PROJECTILE, "charge_beam_a"),
	CLOSED_HAND(SpriteType.PROJECTILE, "closed_hand"),
	COLA(SpriteType.PROJECTILE, "cola"),
	CORK(SpriteType.PROJECTILE, "cork"),
	CROSSHAIR(SpriteType.PROJECTILE, "crosshair"),
	DIATOM_A(SpriteType.PROJECTILE, "diatom_a"),
	DIATOM_B(SpriteType.PROJECTILE, "diatom_b"),
	DIATOM_C(SpriteType.PROJECTILE, "diatom_c"),
	DIATOM_D(SpriteType.PROJECTILE, "diatom_d"),
	DIATOM_SHOT_A(SpriteType.PROJECTILE, "diatom_shot_a"),
	DIATOM_SHOT_B(SpriteType.PROJECTILE, "diatom_shot_b"),
	FLAIL(SpriteType.PROJECTILE, "flail"),
	FLASH_GRENADE(SpriteType.PROJECTILE, "flashgrenade"),
	FLOUNDER_A(SpriteType.PROJECTILE, "flounder_a"),
	FLOUNDER_B(SpriteType.PROJECTILE, "flounder_b"),
	FUGU(SpriteType.PROJECTILE, "fugu"),
	GOLDFISH(SpriteType.PROJECTILE, "goldfish"),
	DRILL(SpriteType.PROJECTILE, "underminer"),
	GRENADE(SpriteType.PROJECTILE, "fraggrenade", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
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
	SEED(SpriteType.PROJECTILE, "seed"),
	SPIT(SpriteType.PROJECTILE, "spit"),
	SPLITTER_A(SpriteType.PROJECTILE, "splitter1"),
	SPLITTER_B(SpriteType.PROJECTILE, "splitter0"),
	SPORE(SpriteType.PROJECTILE, "spore"),
	SPORE_CLUSTER(SpriteType.PROJECTILE, "spore_cluster"),
	SPORE_MILD(SpriteType.PROJECTILE, "spore_mild"),
	SPORE_CLUSTER_MILD(SpriteType.PROJECTILE, "spore_cluster_mild"),
	SPORE_YELLOW(SpriteType.PROJECTILE, "spore_yellow"),
	SPORE_CLUSTER_YELLOW(SpriteType.PROJECTILE, "spore_cluster_yellow"),
	STAR_BLUE(SpriteType.PROJECTILE, "blue_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_PURPLE(SpriteType.PROJECTILE, "purple_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_RED(SpriteType.PROJECTILE, "red_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_WHITE(SpriteType.PROJECTILE, "white_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STAR_YELLOW(SpriteType.PROJECTILE, "yellow_star", PlayMode.LOOP, PlayState.spriteAnimationSpeed),
	STICKYBOMB(SpriteType.PROJECTILE, "sticky"),
	TORPEDO(SpriteType.PROJECTILE, "torpedo"),
	TRICKBULLET(SpriteType.PROJECTILE, "trick"),
	VINE_A(SpriteType.PROJECTILE, "vine_a"),
	VINE_B(SpriteType.PROJECTILE, "vine_b"),
	VINE_C(SpriteType.PROJECTILE, "vine_c"),
	VINE_D(SpriteType.PROJECTILE, "vine_d"),

	FUEL(SpriteType.EVENT, "event_fuel"),
	MEDPAK(SpriteType.EVENT, "event_health"),
	AMMO(SpriteType.EVENT, "ammo"),

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

	NEPTUNE_KING_CORE(SpriteType.NEPTUNE_KING, "terrazza_core"),
	NEPTUNE_KING_BODY(SpriteType.NEPTUNE_KING, "terrazza_body"),
	NEPTUNE_KING_CROWN(SpriteType.NEPTUNE_KING, "terrazza_crown"),

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

	UI_RELOAD_METER(SpriteType.UI, "UI_reload_meter"),
	UI_RELOAD_BAR(SpriteType.UI, "UI_reload_bar"),

	NOTIFICATIONS_CHAT(SpriteType.NOTIFICATIONS, "talking"),
	NOTIFICATIONS_CLEAR_CIRCLE(SpriteType.NOTIFICATIONS, "clear_circle"),
	NOTIFICATIONS_DIRECTIONAL_ARROW(SpriteType.NOTIFICATIONS, "arrow_directional"),
	NOTIFICATIONS_ALERT(SpriteType.NOTIFICATIONS, "alert_red"),
	NOTIFICATIONS_ALERT_PING(SpriteType.NOTIFICATIONS, "arrow_ping_red", PlayMode.LOOP_PINGPONG, PlayState.spriteAnimationSpeed),
	CLEAR_CIRCLE_ALERT(SpriteType.NOTIFICATIONS, "clear_circle_alert"),
	CLEAR_CIRCLE_EGGPLANT(SpriteType.NOTIFICATIONS, "clear_circle_eggplant"),

	EMOTE_YES(SpriteType.EMOTE, "emote_yes", PlayMode.NORMAL, PlayState.spriteAnimationSpeedFast),
	EMOTE_NO(SpriteType.EMOTE, "emote_no", PlayMode.NORMAL, PlayState.spriteAnimationSpeedFast),
	EMOTE_RAGE(SpriteType.EMOTE, "emote_anger", PlayMode.LOOP, PlayState.spriteAnimationSpeedFast),
	EMOTE_LOVE(SpriteType.EMOTE, "emote_love", PlayMode.LOOP, PlayState.spriteAnimationSpeedFast),
	EMOTE_SLEEP(SpriteType.EMOTE, "emote_sleep", PlayMode.LOOP, PlayState.spriteAnimationSpeedFast),
	EMOTE_SWEAT(SpriteType.EMOTE, "emote_tears", PlayMode.LOOP, PlayState.spriteAnimationSpeedFast),
	EMOTE_DICE(SpriteType.EMOTE, "dice_roll", PlayMode.LOOP, PlayState.spriteAnimationSpeedFast),
	EMOTE_READY(SpriteType.EMOTE, "emote_ready", PlayMode.LOOP, 0.5f),

	TELEMACHUS_POINT(SpriteType.TELEMACHUS_POINT, ""),

	MOREAU_BUFF(SpriteType.CHARACTER_EXTRA, "moreau_buff"),
	MOREAU_SLUG(SpriteType.CHARACTER_EXTRA, "moreau_slug"),
	MAXIMILLIAN_BUFF(SpriteType.CHARACTER_EXTRA, "maximillian_buff"),
	MAXIMILLIAN_SLUG(SpriteType.CHARACTER_EXTRA, "maximillian_slug"),
	ROCLAIRE_BUFF(SpriteType.CHARACTER_EXTRA, "roclaire_buff"),
	ROCLAIRE_SLUG(SpriteType.CHARACTER_EXTRA, "roclaire_slug"),
	TAKANORI_BUFF(SpriteType.CHARACTER_EXTRA, "takanori_buff"),
	TAKANORI_SLUG(SpriteType.CHARACTER_EXTRA, "takanori_slug"),
	TELEMACHUS_BUFF(SpriteType.CHARACTER_EXTRA, "telemachus_buff"),
	TELEMACHUS_SLUG(SpriteType.CHARACTER_EXTRA, "telemachus_slug"),
	WANDA_BUFF(SpriteType.CHARACTER_EXTRA, "wanda_buff"),
	WANDA_SLUG(SpriteType.CHARACTER_EXTRA, "wanda_slug"),

	//complex sprites
	SPARKS(SpriteType.PROJECTILE, 0.02f, new SpriteRep("spark_0", 3),
			new SpriteRep("spark_1", 3), new SpriteRep("spark_2", 3),
			new SpriteRep("spark_3", 3), new SpriteRep("spark_4", 3)),

	//Misc stuff from totlc
	IMPACT(SpriteType.IMPACT, "impact"),

	;
	
	//this represents the atlas that we should read the sprite off of.
	private final SpriteType type;
	
	//this is the filename of the sprite
	private final String spriteId;
	
	//These are the frames of the sprite.
	private Array<AtlasRegion> frames;
	private SpriteRep[] complexFrames;

	//how is this sprite animated? and how fast?
	private PlayMode playMode = PlayMode.LOOP;
	private float animationSpeed = PlayState.spriteAnimationSpeedFast;

	//complex sprites are composed of multiple sprites, each repeated a set amount of time
	private boolean complex;

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
	 * This constructor is used for complex sprites that consist of multiple sprites
	 */
	Sprite(SpriteType type, float animationSpeed, SpriteRep... sprites) {
		this.type = type;
		this.animationSpeed = animationSpeed;
		this.complexFrames = sprites;
		this.spriteId = "";
		this.complex = true;
	}

	/**
	 * A SpriteRep consists of a single sprite being repeated a set amount of time
	 */
	public record SpriteRep(String spriteId, int repeat) {}

	/**
	 * This returns the frames of a given sprite
	 */
	public Array<? extends TextureRegion> getFrames() {

		if (this.equals(NOTHING)) {
			return Objects.requireNonNull(getAtlas(SpriteType.EVENT)).findRegions("eggplant");
		}

		if (frames == null) {

			//complex frames are made of several sprites, repeated, lined up back-to-back
			if (complex) {
				frames = new Array<>();
				for (SpriteRep sprite : complexFrames) {
					if ("".equals(sprite.spriteId)) {
						frames.addAll(Objects.requireNonNull(getAtlas(type)).getRegions());
					} else {
						for (int i = 0; i < sprite.repeat; i++) {
							frames.addAll(Objects.requireNonNull(getAtlas(type)).findRegions(sprite.spriteId));
						}
					}
				}
			} else {
				if ("".equals(spriteId)) {
					frames = Objects.requireNonNull(getAtlas(type)).getRegions();
				} else {
					frames = Objects.requireNonNull(getAtlas(type)).findRegions(spriteId);
				}
			}
		}
		return frames;
	}

	public TextureRegion getFrame() { return getFrames().get(0); }
	
	/**
	 * Sprite Types refers to which atlas is used to procure the frames.
	 */
	public static TextureAtlas getAtlas(SpriteType type) {
		return switch (type) {
			case EVENT -> HadalGame.assetManager.get(AssetList.EVENT_ATL.toString());
			case EXPLOSION -> HadalGame.assetManager.get(AssetList.BOOM_1_ATL.toString());
			case PROJECTILE -> HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString());
			case WEAPON -> HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
			case FISH -> HadalGame.assetManager.get(AssetList.FISH_ATL.toString());
			case TURRET -> HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
			case IMPACT -> HadalGame.assetManager.get(AssetList.IMPACT_ATL.toString());
			case UI -> HadalGame.assetManager.get(AssetList.UI_ATL.toString());
			case EMOTE -> HadalGame.assetManager.get(AssetList.EMOTE_ATL.toString());
			case NOTIFICATIONS -> HadalGame.assetManager.get(AssetList.NOTIFICATION_ATL.toString());
			case KAMABOKO -> HadalGame.assetManager.get(AssetList.KAMABOKO_ATL.toString());
			case KAMABOKO_CRAWL -> HadalGame.assetManager.get(AssetList.KAMABOKO_CRAWL_ATL.toString());
			case KAMABOKO_SWIM -> HadalGame.assetManager.get(AssetList.KAMABOKO_SWIM_ATL.toString());
			case DRONE -> HadalGame.assetManager.get(AssetList.DRONE_ATL.toString());
			case NEPTUNE_KING -> HadalGame.assetManager.get(AssetList.NEPTUNE_KING_ATL.toString());
			case TELEMACHUS_POINT -> HadalGame.assetManager.get(AssetList.TELEMACHUS_POINT.toString());
			case CHARACTER_EXTRA -> HadalGame.assetManager.get(AssetList.PLAYER_EXTRA_ATL.toString());
			default -> null;
		};
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
		UI,
		EMOTE,
		NOTIFICATIONS,
		KAMABOKO,
		KAMABOKO_CRAWL,
		KAMABOKO_SWIM,
		DRONE,
		NEPTUNE_KING,
		TELEMACHUS_POINT,
		CHARACTER_EXTRA,
	}
}
