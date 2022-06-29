package com.mygdx.hadal.save;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.artifacts.*;
import com.mygdx.hadal.equip.modeMods.*;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.text.TextFilterUtil;

/**
 * An UnlockArtifact represents a single artifact in the game
 * @author Flarlsberg Flarracuda
 */
public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(new Number1BossMug(), "artifact_1bossmug",
			GameText.NUMBER_ONE_BOSS_MUG, GameText.NUMBER_ONE_BOSS_MUG_DESC, GameText.NUMBER_ONE_BOSS_MUG_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.HEAL),
	EIGHT_BALL(new EightBall(), "artifact_8ball",
			GameText.EIGHT_BALL, GameText.EIGHT_BALL_DESC, GameText.EIGHT_BALL_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	ABYSSAL_INSIGNIA(new AbyssalInsignia(), "artifact_abyssal",
			GameText.ABYSSAL_INSIGNIA, GameText.ABYSSAL_INSIGNIA_DESC, GameText.ABYSSAL_INSIGNIA_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	ADMINISTRATOR_CARD(new AdministratorCard(),
			GameText.ADMINISTRATOR_CARD, GameText.ADMINISTRATOR_CARD_DESC, GameText.ADMINISTRATOR_CARD_DESC_LONG,
			false, true, UnlockTag.GIMMICK),
	ALBATROSS_NECKLACE(new AlbatrossNecklace(),
			GameText.ALBATROSS_NECKLACE, GameText.ALBATROSS_NECKLACE_DESC, GameText.ALBATROSS_NECKLACE_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.GIMMICK),
	AMDAHLS_LOTUS(new AmdahlsLotus(),
			GameText.AMDAHLS_LOTUS, GameText.AMDAHLS_LOTUS_DESC, GameText.AMDAHLS_LOTUS_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.HEAL),
	ANARCHISTS_COOKBOOK(new AnarchistsCookbook(),
			GameText.ANARCHISTS_COOKBOOK, GameText.ANARCHISTS_COOKBOOK_DESC, GameText.ANARCHISTS_COOKBOOK_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	ANCHOR_AMULET(new AnchorTalisman(),
			GameText.ANCHOR_AMULET, GameText.ANCHOR_AMULET_DESC, GameText.ANCHOR_AMULET_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	ANCIENT_SYNAPSE(new AncientSynapse(),
			GameText.ANCIENT_SYNAPSE, GameText.ANCIENT_SYNAPSE_DESC, GameText.ANCIENT_SYNAPSE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	AU_COURANT(new AuCourant(),
			GameText.AU_COURANT, GameText.AU_COURANT_DESC, GameText.AU_COURANT_DESC_LONG,
			false, false, UnlockTag.AMMO),
	BACKPACK_BUDDY(new BackpackBuddy(),
			GameText.BACKPACK_BUDDY, GameText.BACKPACK_BUDDY_DESC, GameText.BACKPACK_BUDDY_DESC_LONG,
			false, true, UnlockTag.GIMMICK),
	BATTLE_BUOY(new BattleBuoy(),
			GameText.BATTLE_BUOY, GameText.BATTLE_BUOY_DESC, GameText.BATTLE_BUOY_DESC_LONG,
			false, false, UnlockTag.WEAPON_DAMAGE),
	BENTHIC_DESIRES(new BenthicDesires(),
			GameText.BENTHIC_DESIRES, GameText.BENTHIC_DESIRES_DESC, GameText.BENTHIC_DESIRES_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.HEAL),
	BLASTEMA(new Blastema(),
			GameText.BLASTEMA, GameText.BLASTEMA_DESC, GameText.BLASTEMA_DESC_LONG,
			false, false, UnlockTag.HEAL),
	BLOODWOODS_GLOVE(new BloodwoodsGlove(),
			GameText.BLOODWOODS_GLOVE, GameText.BLOODWOODS_GLOVE_DESC, GameText.BLOODWOODS_GLOVE_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	BOOK_OF_BURIAL(new BookofBurial(),
			GameText.BOOK_OF_BURIAL, GameText.BOOK_OF_BURIAL_DESC, GameText.BOOK_OF_BURIAL_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	BOTTOM_OF_THE_BARREL(new BottomoftheBarrel(),
			GameText.BOTTOM_OF_THE_BARREL, GameText.BOTTOM_OF_THE_BARREL_DESC, GameText.BOTTOM_OF_THE_BARREL_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	BRIGGLES_BLADED_BOOT(new BrigglesBladedBoot(),
			GameText.BRIGGLES_BLADED_BOOT, GameText.BRIGGLES_BLADED_BOOT_DESC, GameText.BRIGGLES_BLADED_BOOT_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.GIMMICK),
	BRITTLING_POWDER(new BrittlingPowder(),
			GameText.BRITTLING_POWDER, GameText.BRITTLING_POWDER_DESC, GameText.BRITTLING_POWDER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	BUCKET_OF_BATTERIES(new BucketofBatteries(),
			GameText.BUCKET_OF_BATTERIES, GameText.BUCKET_OF_BATTERIES_DESC, GameText.BUCKET_OF_BATTERIES_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	BUTTONMAN_BUTTONS(new ButtonmanButtons(),
			GameText.BUTTONMAN_BUTTONS, GameText.BUTTONMAN_BUTTONS_DESC, GameText.BUTTONMAN_BUTTONS_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	CALL_OF_THE_VOID(new CalloftheVoid(), GameText.CALL_OF_THE_VOID, GameText.CALL_OF_THE_VOID_DESC, GameText.CALL_OF_THE_VOID_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	CARLOCS_THESIS(new CarlocsThesis(),
			GameText.CARLOCS_THESIS, GameText.CARLOCS_THESIS_DESC, GameText.CARLOCS_THESIS_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	CASTAWAYS_TRAVELOGUE(new CastawaysTravelogue(),
			GameText.CASTAWAYS_TRAVELOGUE, GameText.CASTAWAYS_TRAVELOGUE_DESC, GameText.CASTAWAYS_TRAVELOGUE_DESC_LONG,
			false, false, UnlockTag.FUEL),
	CATALOG_OF_WANT(new CatalogofWant(),
			GameText.CATALOG_OF_WANT, GameText.CATALOG_OF_WANT_DESC, GameText.CATALOG_OF_WANT_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	CELESTIAL_ANOINTMENT(new CelestialAnointment(),
			GameText.CELESTIAL_ANOINTMENT, GameText.CELESTIAL_ANOINTMENT_DESC, GameText.CELESTIAL_ANOINTMENT_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	CHAOS_CONJURANT(new ChaosConjurant(),
			GameText.CHAOS_CONJURANT, GameText.CHAOS_CONJURANT_DESC, GameText.CHAOS_CONJURANT_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	CLAWS_OF_FESTUS(new ClawsofFestus(),
			GameText.CLAWS_OF_FESTUS, GameText.CLAWS_OF_FESTUS_DESC, GameText.CLAWS_OF_FESTUS_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	CLEPSYDRAE(new Clepsydrae(), GameText.CLEPSYDRAE, GameText.CLEPSYDRAE_DESC, GameText.CLEPSYDRAE_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	CLOCKWISE_CAGE(new ClockwiseCage(), GameText.CLOCKWISE_CAGE, GameText.CLOCKWISE_CAGE_DESC, GameText.CLOCKWISE_CAGE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	CODEX_OF_SALVORS_LAW(new CodexofSalvorsLaw(),
			GameText.CODEX_OF_SALVORS_LAW, GameText.CODEX_OF_SALVORS_LAW_DESC,GameText.CODEX_OF_SALVORS_LAW_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.OFFENSE),
	COMMUTERS_PARASOL(new CommutersParasol(),
			GameText.COMMUTERS_PARASOL, GameText.COMMUTERS_PARASOL_DESC, GameText.COMMUTERS_PARASOL_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	COMPOUND_VITREOUS(new CompoundVitreous(),
			GameText.COMPOUND_VITREOUS, GameText.COMPOUND_VITREOUS_DESC, GameText.COMPOUND_VITREOUS_DESC_LONG,
			false, false, UnlockTag.GIMMICK),
	CONFIDENCE(new Confidence(),
			GameText.CONFIDENCE, GameText.CONFIDENCE_DESC, GameText.CONFIDENCE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	CRIME_DISCOURAGEMENT_STICK(new CrimeDiscouragementStick(),
			GameText.CRIME_DISCOURAGEMENT_STICK, GameText.CRIME_DISCOURAGEMENT_STICK_DESC, GameText.CRIME_DISCOURAGEMENT_STICK_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	CROWN_OF_THORNS(new CrownofThorns(),
			GameText.CROWN_OF_THORNS, GameText.CROWN_OF_THORNS_DESC, GameText.CROWN_OF_THORNS_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	CURIOUS_SAUCE(new CuriousSauce(),
			GameText.CURIOUS_SAUCE, GameText.CURIOUS_SAUCE_DESC, GameText.CURIOUS_SAUCE_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	CURSED_CILICE(new CursedCilice(),
			GameText.CURSED_CILICE, GameText.CURSED_CILICE_DESC, GameText.CURSED_CILICE_DESC_LONG,
			false, false, UnlockTag.FUEL),
	DAS_BOOT(new DasBoot(),
			GameText.DAS_BOOT, GameText.DAS_BOOT_DESC, GameText.DAS_BOOT_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	DAY_AT_THE_FAIR(new DayattheFair(),
			GameText.DAY_AT_THE_FAIR, GameText.DAY_AT_THE_FAIR_DESC, GameText.DAY_AT_THE_FAIR_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.MOBILITY),
	DEAD_MANS_HAND(new DeadMansHand(),
			GameText.DEAD_MANS_HAND, GameText.DEAD_MANS_HAND_DESC, GameText.DEAD_MANS_HAND_DESC_LONG,
			false, false),
	DEPLORABLE_APPARATUS(new DeplorableApparatus(),
			GameText.DEPLORABLE_APPARATUS, GameText.DEPLORABLE_APPARATUS_DESC, GameText.DEPLORABLE_APPARATUS_DESC_LONG,
			false, false, UnlockTag.HEAL),
	DIATOMACEOUS_EARTH(new DiatomaceousEarth(),
			GameText.DIATOMACEOUS_EARTH, GameText.DIATOMACEOUS_EARTH_DESC, GameText.DIATOMACEOUS_EARTH_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.MAGIC),
	EELSKIN_COVER(new EelskinCover(),
			GameText.EELSKIN_COVER, GameText.EELSKIN_COVER_DESC, GameText.EELSKIN_COVER_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	EMAUDELINES_PRISM(new EmaudelinesPrism(),
			GameText.EMAUDELINES_PRISM, GameText.EMAUDELINES_PRISM_DESC, GameText.EMAUDELINES_PRISM_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	EPHEMERA_PERPETUA(new EphemeraPerpetua(),
			GameText.EPHEMERA_PERPETUA, GameText.EPHEMERA_PERPETUA_DESC, GameText.EPHEMERA_PERPETUA_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	ERSATZ_SMILE(new ErsatzSmile(),
			GameText.ERSATZ_SMILE, GameText.ERSATZ_SMILE_DESC, GameText.ERSATZ_SMILE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	EXTRA_ROW_OF_TEETH(new ExtraRowofTeeth(),
			GameText.EXTRA_ROW_OF_TEETH, GameText.EXTRA_ROW_OF_TEETH_DESC, GameText.EXTRA_ROW_OF_TEETH_DESC_LONG,
			false, false, UnlockTag.AMMO),
	FALLACY_OF_FLESH(new FallacyofFlesh(),
			GameText.FALLACY_OF_FLESH, GameText.FALLACY_OF_FLESH_DESC, GameText.FALLACY_OF_FLESH_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	FARADAYS_CAGE(new FaradaysCage(),
			GameText.FARADAYS_CAGE, GameText.FARADAYS_CAGE_DESC, GameText.FARADAYS_CAGE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	FEELING_OF_BEING_WATCHED(new FeelingofBeingWatched(),
			GameText.FEELING_OF_BEING_WATCHED, GameText.FEELING_OF_BEING_WATCHED_DESC, GameText.FEELING_OF_BEING_WATCHED_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	FENS_CLIPPED_WINGS(new FensClippedWings(),
			GameText.FENS_CLIPPED_WINGS, GameText.FENS_CLIPPED_WINGS_DESC, GameText.FENS_CLIPPED_WINGS_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	THE_FINGER(new TheFinger(),
			GameText.THE_FINGER, GameText.THE_FINGER_DESC, GameText.THE_FINGER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.GIMMICK),
	FISHEYE_CATARACT(new FisheyeCataract(),
			GameText.FISHEYE_CATARACT, GameText.FISHEYE_CATARACT_DESC, GameText.FISHEYE_CATARACT_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.GIMMICK),
	FORAGERS_HIVE(new ForagersHive(),
			GameText.FORAGERS_HIVE, GameText.FORAGERS_HIVE_DESC, GameText.FORAGERS_HIVE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	FRACTURE_PLATE(new FracturePlate(),
			GameText.FRACTURE_PLATE, GameText.FRACTURE_PLATE_DESC, GameText.FRACTURE_PLATE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	GEMMULE(new Gemmule(),
			GameText.GEMMULE, GameText.GEMMULE_DESC, GameText.GEMMULE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	GLUTTONOUS_GREY_GLOVE(new GluttonousGreyGlove(),
			GameText.GLUTTONOUS_GREY_GLOVE, GameText.GLUTTONOUS_GREY_GLOVE_DESC, GameText.GLUTTONOUS_GREY_GLOVE_DESC_LONG,
			false, false, UnlockTag.HEAL),
	GOMEZS_AMYGDALA(new GomezsAmygdala(),
			GameText.GOMEZS_AMYGDALA, GameText.GOMEZS_AMYGDALA_DESC, GameText.GOMEZS_AMYGDALA_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.MOBILITY),
	GOOD_HEALTH(new GoodHealth(),
			GameText.GOOD_HEALTH, GameText.GOOD_HEALTH_DESC, GameText.GOOD_HEALTH_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	HEART_OF_SPEROS(new HeartofSperos(),
			GameText.HEART_OF_SPEROS, GameText.HEART_OF_SPEROS_DESC, GameText.HEART_OF_SPEROS_DESC_LONG,
			false, false, UnlockTag.AMMO),
	HONEYED_TENEBRAE(new HoneyedTenebrae(),
			GameText.HONEYED_TENEBRAE, GameText.HONEYED_TENEBRAE_DESC, GameText.HONEYED_TENEBRAE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	HOOD_OF_HABIT(new HoodofHabit(),
			GameText.HOOD_OF_HABIT, GameText.HOOD_OF_HABIT_DESC, GameText.HOOD_OF_HABIT_DESC_LONG,
			false, false),
	HORNS_OF_AMMON(new HornsofAmmon(),
			GameText.HORNS_OF_AMMON, GameText.HORNS_OF_AMMON_DESC, GameText.HORNS_OF_AMMON_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	HUMANITY_MODULE(new HumanityModule(),
			GameText.HUMANITY_MODULE, GameText.HUMANITY_MODULE_DESC, GameText.HUMANITY_MODULE_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	ICE9(new Ice9(),
			GameText.ICE9, GameText.ICE9_DESC, GameText.ICE9_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	INFORMANTS_TIE(new InformantsTie(),
			GameText.INFORMANTS_TIE, GameText.INFORMANTS_TIE_DESC, GameText.INFORMANTS_TIE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	IRON_SIGHTS(new IronSights(),
			GameText.IRON_SIGHTS, GameText.IRON_SIGHTS_DESC, GameText.IRON_SIGHTS_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	JELLOFELLOW_COSPLAY(new JelloFellowCosplay(),
			GameText.JELLOFELLOW_COSPLAY, GameText.JELLOFELLOW_COSPLAY_DESC, GameText.JELLOFELLOW_COSPLAY_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.GIMMICK),
	JURY_RIGGED_BINDINGS(new JuryRiggedBindings(),
			GameText.JURY_RIGGED_BINDINGS, GameText.JURY_RIGGED_BINDINGS_DESC, GameText.JURY_RIGGED_BINDINGS_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	KERMUNGLER(new Kermungler(),
			GameText.KERMUNGLER, GameText.KERMUNGLER_DESC, GameText.KERMUNGLER_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.OFFENSE),
	KINEATER(new Kineater(),
			GameText.KINEATER, GameText.KINEATER_DESC, GameText.KINEATER_DESC_LONG,
			false, false),
	KINESIS_LENS(new KinesisLens(),
			GameText.KINESIS_LENS, GameText.KINESIS_LENS_DESC, GameText.KINESIS_LENS_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	KUMQUAT(new Kumquat(),
			GameText.KUMQUAT, GameText.KUMQUAT_DESC, GameText.KUMQUAT_DESC_LONG,
			false, false, UnlockTag.HEAL),
	LAMPREY_IDOL(new LampreyIdol(),
			GameText.LAMPREY_IDOL, GameText.LAMPREY_IDOL_DESC, GameText.LAMPREY_IDOL_DESC_LONG,
			false, false, UnlockTag.HEAL),
	LEATHERBACK(new Leatherback(),
			GameText.LEATHERBACK, GameText.LEATHERBACK_DESC, GameText.LEATHERBACK_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	LOAMSKIN_LOCKET(new LoamskinLocket(),
			GameText.LOAMSKIN_LOCKET, GameText.LOAMSKIN_LOCKET_DESC, GameText.LOAMSKIN_LOCKET_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	LOCH_SHIELD(new LochShield(),
			GameText.LOCH_SHIELD, GameText.LOCH_SHIELD_DESC, GameText.LOCH_SHIELD_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	LOSS_OF_SENSES(new LossOfSenses(),
			GameText.LOSS_OF_SENSES, GameText.LOSS_OF_SENSES_DESC, GameText.LOSS_OF_SENSES_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.GIMMICK),
	LOTUS_LANTERN(new LotusLantern(),
			GameText.LOTUS_LANTERN, GameText.LOTUS_LANTERN_DESC, GameText.LOTUS_LANTERN_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	LUMINOUS_ESCA(new LuminousEsca(),
			GameText.LUMINOUS_ESCA, GameText.LUMINOUS_ESCA_DESC, GameText.LUMINOUS_ESCA_DESC_LONG,
			false, false, UnlockTag.GIMMICK),
	MACHINE_GHOST(new MachineGhost(),
			GameText.MACHINE_GHOST, GameText.MACHINE_GHOST_DESC, GameText.MACHINE_GHOST_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	MANGROVE_SEED(new MangroveSeed(),
			GameText.MANGROVE_SEED, GameText.MANGROVE_SEED_DESC, GameText.MANGROVE_SEED_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	MASK_OF_SYMPATHY(new MaskofSympathy(),
			GameText.MASK_OF_SYMPATHY, GameText.MASK_OF_SYMPATHY_DESC, GameText.MASK_OF_SYMPATHY_DESC_LONG,
			false, false),
	MATTER_UNIVERSALIZER(new MatterUniversalizer(),
			GameText.MATTER_UNIVERSALIZER, GameText.MATTER_UNIVERSALIZER_DESC, GameText.MATTER_UNIVERSALIZER_DESC_LONG,
			false, false, UnlockTag.FUEL),
	MOON_FLUTHER(new MoonFluther(),
			GameText.MOON_FLUTHER, GameText.MOON_FLUTHER_DESC, GameText.MOON_FLUTHER_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	MOUTHBREATHER_CERTIFICATE(new MouthbreatherCertificate(),
			GameText.MOUTHBREATHER_CERTIFICATE, GameText.MOUTHBREATHER_CERTIFICATE_DESC, GameText.MOUTHBREATHER_CERTIFICATE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	MOUTHFUL_OF_BEES(new MouthfulofBees(),
			GameText.MOUTHFUL_OF_BEES, GameText.MOUTHFUL_OF_BEES_DESC, GameText.MOUTHFUL_OF_BEES_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	MUDDLING_CUP(new MuddlingCup(),
			GameText.MUDDLING_CUP, GameText.MUDDLING_CUP_DESC, GameText.MUDDLING_CUP_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	NACREOUS_RUDDER(new NacreousRudder(),
			GameText.NACREOUS_RUDDER, GameText.NACREOUS_RUDDER_DESC, GameText.NACREOUS_RUDDER_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	NICE_SHOES(new NiceShoes(),
			GameText.NICE_SHOES, GameText.NICE_SHOES_DESC, GameText.NICE_SHOES_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	NOCTILUCENT_PROMISE(new NoctilucentPromise(),
			GameText.NOCTILUCENT_PROMISE, GameText.NOCTILUCENT_PROMISE_DESC, GameText.NOCTILUCENT_PROMISE_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	NUCLEAR_PUNCH_THRUSTERS(new NuclearPunchThrusters(),
			GameText.NUCLEAR_PUNCH_THRUSTERS, GameText.NUCLEAR_PUNCH_THRUSTERS_DESC, GameText.NUCLEAR_PUNCH_THRUSTERS_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	NURDLER(new Nurdler(),
			GameText.NURDLER, GameText.NURDLER_DESC, GameText.NURDLER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	NUTRILOG_CRUNCHBAR_PLUS(new NutrilogCrunchbarPlus(),
			GameText.NUTRILOG_CRUNCHBAR_PLUS, GameText.NUTRILOG_CRUNCHBAR_PLUS_DESC, GameText.NUTRILOG_CRUNCHBAR_PLUS_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.GIMMICK),
	OBLIGE_LA_MORT(new ObligeLaMort(),
			GameText.OBLIGE_LA_MORT, GameText.OBLIGE_LA_MORT_DESC, GameText.OBLIGE_LA_MORT_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	OL_FAITHFUL(new OlFaithful(),
			GameText.OL_FAITHFUL, GameText.OL_FAITHFUL_DESC, GameText.OL_FAITHFUL_DESC_LONG,
			false, false),
	ORIGIN_COIL(new OriginCoil(),
			GameText.ORIGIN_COIL, GameText.ORIGIN_COIL_DESC, GameText.ORIGIN_COIL_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	OUR_GET_ALONG_SHIRT(new OurGetAlongShirt(),
			GameText.OUR_GET_ALONG_SHIRT, GameText.OUR_GET_ALONG_SHIRT_DESC, GameText.OUR_GET_ALONG_SHIRT_DESC_LONG,
			false, false, UnlockTag.GIMMICK),
	PAIN_SCALE(new PainScale(),
			GameText.PAIN_SCALE, GameText.PAIN_SCALE_DESC, GameText.PAIN_SCALE_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	PEACHWOOD_SWORD(new PeachwoodSword(),
			GameText.PEACHWOOD_SWORD, GameText.PEACHWOOD_SWORD_DESC, GameText.PEACHWOOD_SWORD_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	PEER_PRESSURE(new PeerPressure(),
			GameText.PEER_PRESSURE, GameText.PEER_PRESSURE_DESC, GameText.PEER_PRESSURE_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	PELICAN_PLUSH_TOY(new PelicanPlushToy(),
			GameText.PELICAN_PLUSH_TOY, GameText.PELICAN_PLUSH_TOY_DESC, GameText.PELICAN_PLUSH_TOY_DESC_LONG,
			false, false, UnlockTag.HEAL),
	PEPPER(new Pepper(),
			GameText.PEPPER, GameText.PEPPER_DESC, GameText.PEPPER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	PETRIFIED_PAYLOAD(new PetrifiedPayload(),
			GameText.PETRIFIED_PAYLOAD, GameText.PETRIFIED_PAYLOAD_DESC, GameText.PETRIFIED_PAYLOAD_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	PIFFLER(new Piffler(),
			GameText.PIFFLER, GameText.PIFFLER_DESC, GameText.PIFFLER_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER, UnlockTag.GIMMICK),
	PLUMPOWDER(new Plumpowder(),
			GameText.PLUMPOWDER, GameText.PLUMPOWDER_DESC, GameText.PLUMPOWDER_DESC_LONG,
			false, false, UnlockTag.MAGIC),
	RECYCLER_BOLUS(new RecyclerBolus(),
			GameText.RECYCLER_BOLUS, GameText.RECYCLER_BOLUS_DESC, GameText.RECYCLER_BOLUS_DESC_LONG,
			false, true, UnlockTag.DEFENSE),
	RED_TIDE_TALISMAN(new RedTideTalisman(),
			GameText.RED_TIDE_TALISMAN, GameText.RED_TIDE_TALISMAN_DESC, GameText.RED_TIDE_TALISMAN_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	RING_OF_TESTING(new RingofTesting(),
			GameText.RING_OF_TESTING, GameText.RING_OF_TESTING_DESC, GameText.RING_OF_TESTING_DESC_LONG,
			false, true),
	ROYAL_JUJUBE_BANG(new RoyalJujubeBang(),
			GameText.ROYAL_JUJUBE_BANG, GameText.ROYAL_JUJUBE_BANG_DESC, GameText.ROYAL_JUJUBE_BANG_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	SALIGRAM(new Saligram(),
			GameText.SALIGRAM, GameText.SALIGRAM_DESC, GameText.SALIGRAM_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	SAMURAI_SHARK(new SamuraiShark(),
			GameText.SAMURAI_SHARK, GameText.SAMURAI_SHARK_DESC, GameText.SAMURAI_SHARK_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	SEAFOAM_PERIAPT(new SeafoamPeriapt(),
			GameText.SEAFOAM_PERIAPT, GameText.SEAFOAM_PERIAPT_DESC, GameText.SEAFOAM_PERIAPT_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	SENESCENT_SHIELD(new SenescentShield(),
			GameText.SENESCENT_SHIELD, GameText.SENESCENT_SHIELD_DESC, GameText.SENESCENT_SHIELD_DESC_LONG,
			false, false, UnlockTag.DEFENSE, UnlockTag.PROJECTILE_MODIFIER),
	SHILLERS_DEATHCAP(new ShillersDeathcap(),
			GameText.SHILLERS_DEATHCAP, GameText.SHILLERS_DEATHCAP_DESC, GameText.SHILLERS_DEATHCAP_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PROJECTILE_MODIFIER),
	SHIP_IN_A_BOTTLE(new ShipinaBottle(),
			GameText.SHIP_IN_A_BOTTLE, GameText.SHIP_IN_A_BOTTLE_DESC, GameText.SHIP_IN_A_BOTTLE_DESC_LONG,
			false, false, UnlockTag.AMMO, UnlockTag.OFFENSE),
	SIMPLE_MIND(new SimpleMind(),
			GameText.SIMPLE_MIND, GameText.SIMPLE_MIND_DESC, GameText.SIMPLE_MIND_DESC_LONG,
			false, false, UnlockTag.GIMMICK, UnlockTag.PROJECTILE_MODIFIER),
	SINKING_FEELING(new SinkingFeeling(),
			GameText.SINKING_FEELING, GameText.SINKING_FEELING_DESC, GameText.SINKING_FEELING_DESC_LONG,
			false, false, UnlockTag.MOBILITY),
	SIREN_CHIME(new SirenChime(),
			GameText.SIREN_CHIME, GameText.SIREN_CHIME_DESC, GameText.SIREN_CHIME_DESC_LONG,
			false, false),
	SKIPPERS_BOX_OF_FUN(new SkippersBoxofFun(),
			GameText.SKIPPERS_BOX_OF_FUN, GameText.SKIPPERS_BOX_OF_FUN_DESC, GameText.SKIPPERS_BOX_OF_FUN_DESC_LONG,
			false, false, UnlockTag.GIMMICK),
	SOUND_OF_SEAGULLS(new SoundofSeagulls(),
			GameText.SOUND_OF_SEAGULLS, GameText.SOUND_OF_SEAGULLS_DESC, GameText.SOUND_OF_SEAGULLS_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.MOBILITY),
	SWORD_OF_SYZYGY(new SwordofSyzygy(),
			GameText.SWORD_OF_SYZYGY, GameText.SWORD_OF_SYZYGY_DESC, GameText.SWORD_OF_SYZYGY_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	TEMPEST_TEAPOT(new TempestTeapot(),
			GameText.TEMPEST_TEAPOT, GameText.TEMPEST_TEAPOT_DESC, GameText.TEMPEST_TEAPOT_DESC_LONG,
			false, false, UnlockTag.FUEL),
	TENUOUS_GRIP_ON_REALITY(new TenuousGripOnReality(),
			GameText.TENUOUS_GRIP_ON_REALITY, GameText.TENUOUS_GRIP_ON_REALITY_DESC, GameText.TENUOUS_GRIP_ON_REALITY_DESC_LONG,
			false, false),
	TOME_OF_PHILOPATRY(new TomeOfPhilopatry(),
			GameText.TOME_OF_PHILOPATRY, GameText.TOME_OF_PHILOPATRY_DESC, GameText.TOME_OF_PHILOPATRY_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),
	TRIGGERFISH_FINGER(new TriggerfishFinger(),
			GameText.TRIGGERFISH_FINGER, GameText.TRIGGERFISH_FINGER_DESC, GameText.TRIGGERFISH_FINGER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	TUNICATE_TUNIC(new TunicateTunic(),
			GameText.TUNICATE_TUNIC, GameText.TUNICATE_TUNIC_DESC, GameText.TUNICATE_TUNIC_DESC_LONG,
			false, false, UnlockTag.DEFENSE),
	TYPHON_FANG(new TyphonFang(),
			GameText.TYPHON_FANG, GameText.TYPHON_FANG_DESC, GameText.TYPHON_FANG_DESC_LONG,
			false, false, UnlockTag.AMMO),
	UNBREATHING_MEMBRANE(new UnbreathingMembrane(),
			GameText.UNBREATHING_MEMBRANE, GameText.UNBREATHING_MEMBRANE_DESC, GameText.UNBREATHING_MEMBRANE_DESC_LONG,
			false, false, UnlockTag.AMMO, UnlockTag.MOBILITY),
	VESTIGIAL_CHAMBER(new VestigialChamber(),
			GameText.VESTIGIAL_CHAMBER, GameText.VESTIGIAL_CHAMBER_DESC, GameText.VESTIGIAL_CHAMBER_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	VISE_OF_SHAME(new ViseOfShame(),
			GameText.VISE_OF_SHAME, GameText.VISE_OF_SHAME_DESC, GameText.VISE_OF_SHAME_DESC_LONG,
			false, false, UnlockTag.GIMMICK),
	VOID_HYPONOME(new VoidHyponome(),
			GameText.VOID_HYPONOME, GameText.VOID_HYPONOME_DESC, GameText.VOID_HYPONOME_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.MOBILITY),
	VOLATILE_DERMIS(new VolatileDermis(),
			GameText.VOLATILE_DERMIS, GameText.VOLATILE_DERMIS_DESC, GameText.VOLATILE_DERMIS_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	VOW_OF_EMPTY_HANDS(new VowofEmptyHands(),
			GameText.VOW_OF_EMPTY_HANDS, GameText.VOW_OF_EMPTY_HANDS_DESC, GameText.VOW_OF_EMPTY_HANDS_DESC_LONG,
			false, false, UnlockTag.OFFENSE),
	WHITE_SMOKER(new WhiteSmoker(),
			GameText.WHITE_SMOKER, GameText.WHITE_SMOKER_DESC, GameText.WHITE_SMOKER_DESC_LONG,
			false, false, UnlockTag.MOBILITY, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	WHITE_WHALE_CHARM(new WhiteWhaleCharm(),
			GameText.WHITE_WHALE_CHARM, GameText.WHITE_WHALE_CHARM_DESC, GameText.WHITE_WHALE_CHARM_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.WEAPON_DAMAGE),
	WRATH_OF_THE_FROGMAN(new WrathoftheFrogman(),
			GameText.WRATH_OF_THE_FROGMAN, GameText.WRATH_OF_THE_FROGMAN_DESC, GameText.WRATH_OF_THE_FROGMAN_DESC_LONG,
			false, false, UnlockTag.OFFENSE, UnlockTag.PASSIVE_DAMAGE),
	WRETCHED_REBREATHER(new WretchedRebreather(),
			GameText.WRETCHED_REBREATHER, GameText.WRETCHED_REBREATHER_DESC, GameText.WRETCHED_REBREATHER_DESC_LONG,
			false, false, UnlockTag.FUEL, UnlockTag.MAGIC),
	YIGHT_KITE(new YightKite(),
			GameText.YIGHT_KITE, GameText.YIGHT_KITE_DESC, GameText.YIGHT_KITE_DESC_LONG,
			false, false, UnlockTag.PROJECTILE_MODIFIER),

	//special artifacts for things like mode modifiers
	NOTHING(new NothingArtifact()),
	INFINITE_AMMO(new InfiniteAmmo(), true),
	PLAYER_BOUNCE(new PlayerBounce(), true),
	PLAYER_GIANT(new PlayerGiant(), true),
	PLAYER_INVISIBLE(new PlayerInvisible(), true),
	PLAYER_MINI(new PlayerMini(), true),
	PLAYER_SLIDE(new PlayerSlide(), true),
	VISIBLE_HP(new VisibleHp(), true),

	;
	
	//singleton artifact represented by this unlock
	private final Artifact artifactSingleton;
	
	//the artifact's information
	private final GameText name, desc, descLong;
	private final Array<UnlockTag> tags = new Array<>();

	//The string id of the artifact's icon in the artifact texture atlas
	private final String spriteId;

	//is this artifact hidden in the ui? used for mode modifications that are coded as artifacts
	private boolean invisible;

	UnlockArtifact(Artifact artifact) {
		this(artifact, "artifact", GameText.NOTHING, GameText.NOTHING, GameText.NOTHING, true, true);
	}

	UnlockArtifact(Artifact artifact, String spriteId, GameText name, GameText desc, GameText descLong,
				   boolean omitHub, boolean omitRandom, UnlockTag... tags) {
		this.artifactSingleton = artifact;
		this.spriteId = spriteId;
		this.name = name;
		this.desc = desc;
		this.descLong = descLong;
		this.invisible = false;

		if (!omitHub) {
			this.tags.add(UnlockTag.RELIQUARY);
		}
		if (!omitRandom) {
			this.tags.add(UnlockTag.RANDOM_POOL);
		}
		this.tags.addAll(tags);
	}

	UnlockArtifact(Artifact artifact, GameText name, GameText desc, GameText descLong,
				   boolean omitHub, boolean omitRandom, UnlockTag... tags) {
		this(artifact, "artifact", name, desc, descLong, omitHub, omitRandom, tags);
	}

	UnlockArtifact(Artifact artifact, boolean invisible) {
		this(artifact);
		this.invisible = invisible;
	}

	/**
	 * This returns the sprite representing this artifact in the ui
	 */
	public TextureRegion getFrame() {
		return ((TextureAtlas) HadalGame.assetManager.get(AssetList.ARTIFACT_ATL.toString())).findRegion(spriteId);
	}

	/**
	 * This acquires a list of all unlocked artifacts (if unlock is true. otherwise just return all artifacts that satisfy the tags)
	 */
	public static Array<UnlockArtifact> getUnlocks(PlayState state, boolean unlock, Array<UnlockTag> tags) {
		Array<UnlockArtifact> items = new Array<>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			
			boolean get = UnlockManager.checkTags(u.tags, tags);
			
			if (unlock && !UnlockManager.checkUnlock(state, UnlockType.ARTIFACT, u.toString())) {
				get = false;
			}
			if (get) {
				items.add(u);
			}
		}
		return items;
	}
	
	/**
	 * This method returns the name of a artifact randomly selected from the pool.
	 * @param pool: comma separated list of names of artifact to choose from. if set to "", return any artifact.
	 */
	public static UnlockArtifact getRandArtfFromPool(PlayState state, String pool) {

		Array<UnlockTag> defaultTags = new Array<>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockArtifact> unlocks = UnlockArtifact.getUnlocks(state, false, defaultTags);
			return unlocks.get(MathUtils.random(unlocks.size - 1));
		}

		Array<String> artifacts = new Array<>();
		artifacts.addAll(pool.split(","));
		return UnlockArtifact.getByName(artifacts.get(MathUtils.random(artifacts.size - 1)));
	}
	
	public Artifact getArtifact() { return artifactSingleton; }

	public boolean isInvisible() { return invisible; }

	public String getName() { return name.text(); }

	public String getDesc() { return desc.text(); }

	/**
	 * Get description and fill wildcards with item information
	 */
	public String getDescLong() { return TextFilterUtil.filterText(descLong.text(artifactSingleton.getDescFields())); }

	private static final ObjectMap<String, UnlockArtifact> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockArtifact u : UnlockArtifact.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockArtifact getByName(String s) {
		return UnlocksByName.get(s, NOTHING);
	}
}