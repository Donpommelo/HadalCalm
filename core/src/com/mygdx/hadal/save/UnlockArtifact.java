package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(new Number1BossMug()),
	EIGHT_BALL(new EightBall()),
	ABYSSAL_INSIGNIA(new AbyssalInsignia()),
	ADMINISTRATOR_CARD(new AdministratorCard()),
	AMMO_DRUM(new AmmoDrum()),
	ANARCHISTS_COOKBOOK(new AnarchistsCookbook()),
	ANCHOR_TALISMAN(new AnchorTalisman()),
	BACKPACK_BUDDY(new BackpackBuddy()),
	BLOODWOODS_GLOVE(new BloodwoodsGlove()),
	BOTTOM_OF_THE_BARREL(new BottomoftheBarrel()),
	BRIGGLES_BLADED_BOOT(new BrigglesBladedBoot()),
	BUCKET_OF_BATTERIES(new BucketofBatteries()),
	CALL_OF_THE_VOID(new CalloftheVoid()),
	CLAWS_OF_FESTUS(new ClawsofFestus()),
	CLOCKWISE_CAGE(new ClockwiseCage()),
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
	INFORMANTS_TIE(new InformantsTie()),
	LOAMSKIN_AMULET(new LoamskinAmulet()),
	MANGROVE_TALISMAN(new MangroveTalisman()),
	MASK_OF_SYMPATHY(new MaskofSympathy()),
	MATTER_UNIVERSALIZER(new MatterUniversalizer()),
	MODERATOR_CARD(new ModeratorCard()),
	MOON_FLUTHER(new MoonFluther()),
	MOUTHBREATHER_TALISMAN(new MouthbreatherTalisman()),
	MUDDLING_CUP(new MuddlingCup()),
	NICE_SHOES(new NiceShoes()),
	NUCLEAR_PUNCH_THRUSTERS(new NuclearPunchThrusters()),
	NURDLER(new Nurdler()),
	ORIGIN_COIL(new OriginCoil()),
	PAIN_SCALE(new PainScale()),
	PELICAN_PLUSH_TOY(new PelicanPlushToy()),
	PEPPER(new Pepper()),
	PIFFLER(new Piffler()),
	PETRIFIED_SYNAPSE(new PetrifiedSynapse()),
	RED_TIDE_TALISMAN(new RedTideTalisman()),
	RING_OF_TESTING(new RingofTesting()),
	RING_OF_THE_LAMPREY(new RingoftheLamprey()),
	TUNICATE_TUNIC(new TunicateTunic()),
	ROYAL_JUJUBE_BANG(new RoyalJujubeBang()),
	SAMURAI_SHARK(new SamuraiShark()),
	SEAFOAM_TALISMAN(new SeafoamTalisman()),
	SENESCENT_SHIELD(new SenescentShield()),
	SHILLERS_DEATHCAP(new ShillersDeathcap()),
	SIMPLE_MIND(new SimpleMind()),
	SIREN_CHIME(new SirenChime()),
	SKIPPERS_BOX_OF_FUN(new SkippersBoxofFun()),
	SWORD_OF_SYZYGY(new SwordofSyzygy()),
	TEMPEST_TEAPOT(new TempestTeapot()),
	TRIGGERFISH_FINGER(new TriggerfishFinger()),
	TYPHON_FANG(new TyphonFang()),
	UNBREATHING_MEMBRANE(new UnbreathingMembrane()),
	VOID_HYPONOME(new VoidHyponome()),
	WHITE_SMOKER(new WhiteSmoker()),
	WRATH_OF_THE_FROGMAN(new WrathoftheFrogman()),
	
	NOTHING(new NothingArtifact()),
	;
	
	private Artifact artifactSingleton; 
	private InfoItem info;
	
	UnlockArtifact(Artifact artifact) {
		this.artifactSingleton = artifact;
		artifactSingleton.setUnlock(this);
	}
	
	public static Array<UnlockArtifact> getUnlocks(boolean unlock, Record record, UnlockTag... tags) {
		Array<UnlockArtifact> items = new Array<UnlockArtifact>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getInfo().getTags().size(); j++) {
					if (tags[i].equals(u.getInfo().getTags().get(j))) {
						get = true;
					}
				}
			}
			
			if (unlock && !UnlockManager.checkUnlock(record, UnlockType.ARTIFACT, u.toString())) {
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
	public static String getRandArtfFromPool(Record record, String pool) {
		
		if (pool.equals("")) {
			Array<UnlockArtifact> unlocks = UnlockArtifact.getUnlocks(false, record, UnlockTag.RANDOM_POOL);
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