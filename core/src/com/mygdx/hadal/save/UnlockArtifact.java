package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(new Number1BossMug()),
	EIGHT_BALL(new EightBall()),
	ABYSSAL_INSIGNIA(new AbyssalInsignia()),
	ADMINISTRATOR_CARD(new AdministratorCard()),
	ALBATROSS_NECKLACE(new AlbatrossNecklace()),
	ANARCHISTS_COOKBOOK(new AnarchistsCookbook()),
	ANCHOR_TALISMAN(new AnchorTalisman()),
	ANCIENT_SYNAPSE(new AncientSynapse()),
	BACKPACK_BUDDY(new BackpackBuddy()),
	BLASTEMA(new Blastema()),
	BLOODWOODS_GLOVE(new BloodwoodsGlove()),
	BOTTOM_OF_THE_BARREL(new BottomoftheBarrel()),
	BRIGGLES_BLADED_BOOT(new BrigglesBladedBoot()),
	BRITTLING_POWDER(new BrittlingPowder()),
	BUCKET_OF_BATTERIES(new BucketofBatteries()),
	CALL_OF_THE_VOID(new CalloftheVoid()),
	CATALOG_OF_WANT(new CatalogofWant()),
	CLAWS_OF_FESTUS(new ClawsofFestus()),
	CLOCKWISE_CAGE(new ClockwiseCage()),
	COMMUTERS_PARASOL(new CommutersParasol()),
	CONFIDENCE(new Confidence()),
	CROWN_OF_THORNS(new CrownofThorns()),
	CURIOUS_SAUCE(new CuriousSauce()),
	CURSED_CILICE(new CursedCilice()),
	DEAD_MANS_HAND(new DeadMansHand()),
	DEPLORABLE_APPARATUS(new DeplorableApparatus()),
	EELSKIN_COVER(new EelskinCover()),
	EMAUDELINES_PRISM(new EmaudelinesPrism()),
	EXTRA_ROW_OF_TEETH(new ExtraRowofTeeth()),
	FARADAYS_CAGE(new FaradaysCage()),
	FENS_CLIPPED_WINGS(new FensClippedWings()),
	FORAGERS_HIVE(new ForagersHive()),
	FRACTURE_PLATE(new FracturePlate()),
	GLUTTONOUS_GREY_GLOVE(new GluttonousGreyGlove()),
	GOMEZS_AMYGDALA(new GomezsAmygdala()),
	GOOD_HEALTH(new GoodHealth()),
	HEART_OF_SPEROS(new HeartofSperos()),
	HORNS_OF_AMMON(new HornsofAmmon()),
	HUMANITY_MODULE(new HumanityModule()),
	INFORMANTS_TIE(new InformantsTie()),
	IRON_SIGHTS(new IronSights()),
	JELLOFELLOW_COSPLAY(new JelloFellowCosplay()),
	KINEATER(new Kineater()),
	KINESIS_LENS(new KinesisLens()),
	LOCH_SHIELD(new LochShield()),
	MACHINE_GHOST(new MachineGhost()),
	MANGROVE_TALISMAN(new MangroveTalisman()),
	MASK_OF_SYMPATHY(new MaskofSympathy()),
	MATTER_UNIVERSALIZER(new MatterUniversalizer()),
	MOON_FLUTHER(new MoonFluther()),
	MOUTHBREATHER_TALISMAN(new MouthbreatherTalisman()),
	MUDDLING_CUP(new MuddlingCup()),
	NICE_SHOES(new NiceShoes()),
	NUCLEAR_PUNCH_THRUSTERS(new NuclearPunchThrusters()),
	NURDLER(new Nurdler()),
	OL_FAITHFUL(new OlFaithful()),
	ORIGIN_COIL(new OriginCoil()),
	PAIN_SCALE(new PainScale()),
	PELICAN_PLUSH_TOY(new PelicanPlushToy()),
	PEPPER(new Pepper()),
	PETRIFIED_PAYLOAD(new PetrifiedPayload()),
	PIFFLER(new Piffler()),
	RED_TIDE_TALISMAN(new RedTideTalisman()),
	RING_OF_TESTING(new RingofTesting()),
	RING_OF_THE_LAMPREY(new RingoftheLamprey()),
	TUNICATE_TUNIC(new TunicateTunic()),
	ROYAL_JUJUBE_BANG(new RoyalJujubeBang()),
	SALIGRAM(new Saligram()),
	SAMURAI_SHARK(new SamuraiShark()),
	SEAFOAM_TALISMAN(new SeafoamTalisman()),
	SENESCENT_SHIELD(new SenescentShield()),
	SHILLERS_DEATHCAP(new ShillersDeathcap()),
	SHIP_IN_A_BOTTLE(new ShipinaBottle()),
	SIMPLE_MIND(new SimpleMind()),
	SIREN_CHIME(new SirenChime()),
	SKIPPERS_BOX_OF_FUN(new SkippersBoxofFun()),
	SWORD_OF_SYZYGY(new SwordofSyzygy()),
	TEMPEST_TEAPOT(new TempestTeapot()),
	TOME_OF_PHILOPATRY(new TomeOfPhilopatry()),
	TRIGGERFISH_FINGER(new TriggerfishFinger()),
	TYPHON_FANG(new TyphonFang()),
	UNBREATHING_MEMBRANE(new UnbreathingMembrane()),
	VOID_HYPONOME(new VoidHyponome()),
	WHITE_SMOKER(new WhiteSmoker()),
	WHITE_WHALE_CHARM(new WhiteWhaleCharm()),
	WRATH_OF_THE_FROGMAN(new WrathoftheFrogman()),
	YIGHT_KITE(new YightKite()),
	
	NOTHING(new NothingArtifact()),
	;
	
	private Artifact artifactSingleton; 
	private InfoItem info;
	
	UnlockArtifact(Artifact artifact) {
		this.artifactSingleton = artifact;
		artifactSingleton.setUnlock(this);
	}
	
	public static Array<UnlockArtifact> getUnlocks(PlayState state, boolean unlock, ArrayList<UnlockTag> tags) {
		Array<UnlockArtifact> items = new Array<UnlockArtifact>();
		
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
	 * @return
	 */
	public static String getRandArtfFromPool(PlayState state, String pool) {
		
		ArrayList<UnlockTag> defaultTags = new ArrayList<UnlockTag>();
		defaultTags.add(UnlockTag.RANDOM_POOL);
		
		if (pool.equals("")) {
			Array<UnlockArtifact> unlocks = UnlockArtifact.getUnlocks(state, false, defaultTags);
			return unlocks.get(GameStateManager.generator.nextInt(unlocks.size)).name();
		}
		
		ArrayList<String> artifacts = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			artifacts.add(id);
		}
		return artifacts.get(GameStateManager.generator.nextInt(artifacts.size()));
	}
	
	public Artifact getArtifact() { return artifactSingleton; }
	
	public InfoItem getInfo() {	return info; }
	
	public void setInfo(InfoItem info) {this.info = info; }
}