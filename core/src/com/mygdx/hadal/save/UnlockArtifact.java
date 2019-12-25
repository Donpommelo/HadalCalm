package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(new Number1BossMug()),
	EIGHT_BALL(new EightBall()),
	ABYSSAL_INSIGNIA(new AbyssalInsignia()),
	ADMINISTRATOR_CARD(new AdministratorCard()),
	AMMO_DRUM(new AmmoDrum()),
	ANARCHISTS_COOKBOOK(new AnarchistsCookbook()),
	ANCHOR_TALISMAN(new AnchorTalisman()),
	BLOODWOODS_GLOVE(new BloodwoodsGlove()),
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
	ORIGIN_COIL(new OriginCoil()),
	PELICAN_PLUSH_TOY(new PelicanPlushToy()),
	PEPPER(new Pepper()),
	PIFFLER(new Piffler()),
	PREHISTORIC_SYNAPSE(new PrehistoricSynapse()),
	RED_TIDE_TALISMAN(new RedTideTalisman()),
	RING_OF_TESTING(new RingofTesting()),
	RING_OF_THE_LAMPREY(new RingoftheLamprey()),
	TUNICATE_TUNIC(new TunicateTunic()),
	ROYAL_JUJUBE_BANG(new RoyalJujubeBang()),
	SAMURAI_SHARK(new SamuraiShark()),
	SEAFOAM_TALISMAN(new SeafoamTalisman()),
	SENESCENT_SHIELD(new SenescentShield()),
	SHILLERS_BASIDIA(new ShillersBasidia()),
	SIMPLE_MIND(new SimpleMind()),
	SIREN_CHIME(new SirenChime()),
	SKIPPERS_BOX_OF_FUN(new SkippersBoxofFun()),
	SWORD_OF_SYZYGY(new SwordofSyzygy()),
	TEMPEST_TEAPOT(new TempestTeapot()),
	TRIGGER_FINGER(new TriggerFinger()),
	TYPHON_FANG(new TyphonFang()),
	UNBREATHING_MEMBRANE(new UnbreathingMembrane()),
	VOID_HYPONOME(new VoidHyponome()),

	NOTHING(new NothingArtifact()),
	;
	
	private Artifact artifactSingleton; 
	private InfoItem info;
	
	UnlockArtifact(Artifact artifact) {
		this.artifactSingleton = artifact;
		artifactSingleton.setUnlock(this);
	}
	
	public static Array<UnlockArtifact> getUnlocks(boolean unlock, UnlockTag... tags) {
		Array<UnlockArtifact> items = new Array<UnlockArtifact>();
		
		for (UnlockArtifact u : UnlockArtifact.values()) {
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.getTags().size(); j++) {
					if (tags[i].equals(u.getTags().get(j))) {
						get = true;
					}
				}
			}
			
			if (unlock && !u.isUnlocked()) {
				get = false;
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public Artifact getArtifact() { return artifactSingleton; }
	
	public InfoItem getInfo() {	return info; }
	
	public void setInfo(InfoItem info) {this.info = info; }
	
	public boolean isUnlocked() { return info.isUnlocked(); }
	
	public ArrayList<UnlockTag> getTags() {	return info.getTags(); }
	
	public String getName() { return info.getName(); }
	
	public String getDescr() { return info.getDescription(); }
	
	public String getDescrLong() { return info.getDescriptionLong(); }
	
	public int getCost() { return info.getCost(); }
	
	public void setUnlocked(boolean unlock) { info.setUnlocked(unlock); }
}