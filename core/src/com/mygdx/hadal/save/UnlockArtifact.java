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

/**
 * An UnlockArtifact represents a single artifact in the game
 * @author Flarlsberg Flarracuda
 */
public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(new Number1BossMug(), "artifact_1bossmug"),
	EIGHT_BALL(new EightBall(), "artifact_8ball"),
	ABYSSAL_INSIGNIA(new AbyssalInsignia(), "artifact_abyssal"),
	ADMINISTRATOR_CARD(new AdministratorCard()),
	ALBATROSS_NECKLACE(new AlbatrossNecklace()),
	AMDAHLS_LOTUS(new AmdahlsLotus()),
	ANARCHISTS_COOKBOOK(new AnarchistsCookbook()),
	ANCHOR_AMULET(new AnchorTalisman()),
	ANCIENT_SYNAPSE(new AncientSynapse()),
	AU_COURANT(new AuCourant()),
	BACKPACK_BUDDY(new BackpackBuddy()),
	BENTHIC_DESIRES(new BenthicDesires()),
	BLASTEMA(new Blastema()),
	BLOODWOODS_GLOVE(new BloodwoodsGlove()),
	BOOK_OF_BURIAL(new BookofBurial()),
	BOTTOM_OF_THE_BARREL(new BottomoftheBarrel()),
	BRIGGLES_BLADED_BOOT(new BrigglesBladedBoot()),
	BRITTLING_POWDER(new BrittlingPowder()),
	BUCKET_OF_BATTERIES(new BucketofBatteries()),
	BUTTONMAN_BUTTONS(new ButtonmanButtons()),
	CALL_OF_THE_VOID(new CalloftheVoid()),
	CASTAWAYS_TRAVELOGUE(new CastawaysTravelogue()),
	CATALOG_OF_WANT(new CatalogofWant()),
	CELESTIAL_ANOINTMENT(new CelestialAnointment()),
	CHAOS_CONJURANT(new ChaosConjurant()),
	CLAWS_OF_FESTUS(new ClawsofFestus()),
	CLOCKWISE_CAGE(new ClockwiseCage()),
	CODEX_OF_SALVORS_LAW(new CodexofSalvorsLaw()),
	COMMUTERS_PARASOL(new CommutersParasol()),
	COMPOUND_VITREOUS(new CompoundVitreous()),
	CONFIDENCE(new Confidence()),
	CROWN_OF_THORNS(new CrownofThorns()),
	CURIOUS_SAUCE(new CuriousSauce()),
	CURSED_CILICE(new CursedCilice()),
	DAS_BOOT(new DasBoot()),
	DAY_AT_THE_FAIR(new DayattheFair()),
	DEAD_MANS_HAND(new DeadMansHand()),
	DEPLORABLE_APPARATUS(new DeplorableApparatus()),
	DIATOMACEOUS_EARTH(new DiatomaceousEarth()),
	EELSKIN_COVER(new EelskinCover()),
	EMAUDELINES_PRISM(new EmaudelinesPrism()),
	EPHEMERA_PERPETUA(new EphemeraPerpetua()),
	EXTRA_ROW_OF_TEETH(new ExtraRowofTeeth()),
	FALLACY_OF_FLESH(new FallacyofFlesh()),
	FARADAYS_CAGE(new FaradaysCage()),
	FEELING_OF_BEING_WATCHED(new FeelingofBeingWatched()),
	FENS_CLIPPED_WINGS(new FensClippedWings()),
	THE_FINGER(new TheFinger()),
	FORAGERS_HIVE(new ForagersHive()),
	FRACTURE_PLATE(new FracturePlate()),
	GEMMULE(new Gemmule()),
	GLUTTONOUS_GREY_GLOVE(new GluttonousGreyGlove()),
	GOMEZS_AMYGDALA(new GomezsAmygdala()),
	GOOD_HEALTH(new GoodHealth()),
	HEART_OF_SPEROS(new HeartofSperos()),
	HONEYED_TENEBRAE(new HoneyedTenebrae()),
	HOOD_OF_HABIT(new HoodofHabit()),
	HORNS_OF_AMMON(new HornsofAmmon()),
	HUMANITY_MODULE(new HumanityModule()),
	ICE9(new Ice9()),
	INFORMANTS_TIE(new InformantsTie()),
	IRON_SIGHTS(new IronSights()),
	JELLOFELLOW_COSPLAY(new JelloFellowCosplay()),
	KERMUNGLER(new Kermungler()),
	KINEATER(new Kineater()),
	KINESIS_LENS(new KinesisLens()),
	KUMQUAT(new Kumquat()),
	LAMPREY_IDOL(new LampreyIdol()),
	LOAMSKIN_LOCKET(new LoamskinLocket()),
	LOCH_SHIELD(new LochShield()),
	LOTUS_LANTERN(new LotusLantern()),
	LUMINOUS_ESCA(new LuminousEsca()),
	MACHINE_GHOST(new MachineGhost()),
	MANGROVE_SEED(new MangroveSeed()),
	MASK_OF_SYMPATHY(new MaskofSympathy()),
	MATTER_UNIVERSALIZER(new MatterUniversalizer()),
	MOON_FLUTHER(new MoonFluther()),
	MOUTHBREATHER_CERTIFICATE(new MouthbreatherCertificate()),
	MOUTHFUL_OF_BEES(new MouthfulofBees()),
	MUDDLING_CUP(new MuddlingCup()),
	NACREOUS_RUDDER(new NacreousRudder()),
	NICE_SHOES(new NiceShoes()),
	NOCTILUCENT_PROMISE(new NoctilucentPromise()),
	NUCLEAR_PUNCH_THRUSTERS(new NuclearPunchThrusters()),
	NURDLER(new Nurdler()),
	OL_FAITHFUL(new OlFaithful()),
	ORIGIN_COIL(new OriginCoil()),
	OUR_GET_ALONG_SHIRT(new OurGetAlongShirt()),
	PAIN_SCALE(new PainScale()),
	PEACHWOOD_SWORD(new PeachwoodSword()),
	PEER_PRESSURE(new PeerPressure()),
	PELICAN_PLUSH_TOY(new PelicanPlushToy()),
	PEPPER(new Pepper()),
	PETRIFIED_PAYLOAD(new PetrifiedPayload()),
	PIFFLER(new Piffler()),
	PLUMPOWDER(new Plumpowder()),
	RECYCLER_BOLUS(new RecyclerBolus()),
	RED_TIDE_TALISMAN(new RedTideTalisman()),
	RING_OF_TESTING(new RingofTesting()),
	ROYAL_JUJUBE_BANG(new RoyalJujubeBang()),
	SALIGRAM(new Saligram()),
	SAMURAI_SHARK(new SamuraiShark()),
	SEAFOAM_PERIAPT(new SeafoamPeriapt()),
	SENESCENT_SHIELD(new SenescentShield()),
	SHILLERS_DEATHCAP(new ShillersDeathcap()),
	SHIP_IN_A_BOTTLE(new ShipinaBottle()),
	SIMPLE_MIND(new SimpleMind()),
	SINKING_FEELING(new SinkingFeeling()),
	SIREN_CHIME(new SirenChime()),
	SKIPPERS_BOX_OF_FUN(new SkippersBoxofFun()),
	SOUND_OF_SEAGULLS(new SoundofSeagulls()),
	SWORD_OF_SYZYGY(new SwordofSyzygy()),
	TEMPEST_TEAPOT(new TempestTeapot()),
	TENUOUS_GRIP_ON_REALITY(new TenuousGripOnReality()),
	TOME_OF_PHILOPATRY(new TomeOfPhilopatry()),
	TRIGGERFISH_FINGER(new TriggerfishFinger()),
	TUNICATE_TUNIC(new TunicateTunic()),
	TYPHON_FANG(new TyphonFang()),
	UNBREATHING_MEMBRANE(new UnbreathingMembrane()),
	VESTIGIAL_CHAMBER(new VestigialChamber()),
	VOID_HYPONOME(new VoidHyponome()),
	VOLATILE_DERMIS(new VolatileDermis()),
	WHITE_SMOKER(new WhiteSmoker()),
	WHITE_WHALE_CHARM(new WhiteWhaleCharm()),
	WRATH_OF_THE_FROGMAN(new WrathoftheFrogman()),
	YIGHT_KITE(new YightKite()),

	//special artifacts for things like mode modifiers
	NOTHING(new NothingArtifact()),
	INFINITE_AMMO(new InfiniteAmmo(), true),
	PLAYER_BOUNCE(new PlayerBounce(), true),
	PLAYER_SLIDE(new PlayerSlide(), true),
	PLAYER_INVISIBLE(new PlayerInvisible(), true),
	VISIBLE_HP(new VisibleHp(), true),
	;
	
	//singleton artifact represented by this unlock
	private final Artifact artifactSingleton;
	
	//the artifact's information
	private InfoItem info;

	//The string id of the artifact's icon in the artifact texture atlas
	private final String spriteId;

	//is this artifact hidden in the ui? used for mode modifications that are coded as artifacts
	private boolean invisible;

	UnlockArtifact(Artifact artifact) {
		this(artifact, "artifact");
	}

	UnlockArtifact(Artifact artifact, String spriteId) {
		this.artifactSingleton = artifact;
		this.spriteId = spriteId;
		this.invisible = false;
	}

	UnlockArtifact(Artifact artifact, boolean invisible) {
		this(artifact);
		this.invisible = invisible;
		setInfo(new InfoItem());
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
			
			boolean get = UnlockManager.checkTags(u.getInfo(), tags);
			
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
	
	public InfoItem getInfo() {	return info; }
	
	public void setInfo(InfoItem info) {this.info = info; }

	public boolean isInvisible() { return invisible; }

	private static final ObjectMap<String, UnlockArtifact> UnlocksByName = new ObjectMap<>();
	static {
		for (UnlockArtifact u: UnlockArtifact.values()) {
			UnlocksByName.put(u.toString(), u);
		}
	}
	public static UnlockArtifact getByName(String s) {
		return UnlocksByName.get(s, NOTHING);
	}
}