package com.mygdx.hadal.save;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.equip.artifacts.*;

public enum UnlockArtifact {
	
	NUMBER_ONE_BOSS_MUG(Number1BossMug.class),
	EIGHT_BALL(EightBall.class),
	ABYSSAL_INSIGNIA(AbyssalInsignia.class),
	ADMINISTRATOR_CARD(AdministratorCard.class),
	ANARCHISTS_COOKBOOK(AnarchistsCookbook.class),
	ANCHOR_TALISMAN(AnchorTalisman.class),
	BLOODWOODS_GLOVE(BloodwoodsGlove.class),
	BRIGGLES_BLADED_BOOT(BrigglesBladedBoot.class),
	BUCKET_OF_BATTERIES(BucketofBatteries.class),
	CALL_OF_THE_VOID(CalloftheVoid.class),
	CLAWS_OF_FESTUS(ClawsofFestus.class),
	CLOCKWISE_CAGE(ClockwiseCage.class),
	CONFIDENCE(Confidence.class),
	CROWN_OF_THORNS(CrownofThorns.class),
	CURIOUS_SAUCE(CuriousSauce.class),
	CURSED_CILICE(CursedCilice.class),
	DEPLORABLE_APPARATUS(DeplorableApparatus.class),
	EELSKIN_COVER(EelskinCover.class),
	EMAUDELINES_PRISM(EmaudelinesPrism.class),
	EXTRA_ROW_OF_TEETH(ExtraRowofTeeth.class),
	FARADAYS_CAGE(FaradaysCage.class),
	FENS_CLIPPED_WINGS(FensClippedWings.class),
	FORAGERS_HIVE(ForagersHive.class),
	FRACTURE_PLATE(FracturePlate.class),
	GLUTTONOUS_GREY_GLOVE(GluttonousGreyGlove.class),
	GOMEZS_AMYGDALA(GomezsAmygdala.class),
	GOOD_HEALTH(GoodHealth.class),
	HORNS_OF_AMMON(HornsofAmmon.class),
	INFORMANTS_TIE(InformantsTie.class),
	LOAMSKIN_AMULET(LoamskinAmulet.class),
	MANGROVE_TALISMAN(MangroveTalisman.class),
	MASK_OF_SYMPATHY(MaskofSympathy.class),
	MATTER_UNIVERSALIZER(MatterUniversalizer.class),
	MODERATOR_CARD(ModeratorCard.class),
	MOON_FLUTHER(MoonFluther.class),
	MOUTHBREATHER_TALISMAN(MouthbreatherTalisman.class),
	MUDDLING_CUP(MuddlingCup.class),
	NICE_SHOES(NiceShoes.class),
	NUCLEAR_PUNCH_THRUSTERS(NuclearPunchThrusters.class),
	ORIGIN_COIL(OriginCoil.class),
	PELICAN_PLUSH_TOY(PelicanPlushToy.class),
	PEPPER(Pepper.class),
	PIFFLER(Piffler.class),
	PREHISTORIC_SYNAPSE(PrehistoricSynapse.class),
	RED_TIDE_TALISMAN(RedTideTalisman.class),
	RING_OF_TESTING(RingofTesting.class),
	RING_OF_THE_LAMPREY(RingoftheLamprey.class),
	TUNICATE_TUNIC(TunicateTunic.class),
	ROYAL_JUJUBE_BANG(RoyalJujubeBang.class),
	SAMURAI_SHARK(SamuraiShark.class),
	SEAFOAM_TALISMAN(SeafoamTalisman.class),
	SENESCENT_SHIELD(SenescentShield.class),
	SHILLERS_BASIDIA(ShillersBasidia.class),
	SIMPLE_MIND(SimpleMind.class),
	SIREN_CHIME(SirenChime.class),
	SKIPPERS_BOX_OF_FUN(SkippersBoxofFun.class),
	SWORD_OF_SYZYGY(SwordofSyzygy.class),
	TEMPEST_TEAPOT(TempestTeapot.class),
	TRIGGER_FINGER(TriggerFinger.class),
	TYPHON_FANG(TyphonFang.class),
	UNBREATHING_MEMBRANE(UnbreathingMembrane.class),
	VOID_HYPONOME(VoidHyponome.class),

	NOTHING(NothingArtifact.class),
	;
	
	private Class<? extends Artifact> artifact;
	private InfoItem info;
	
	UnlockArtifact(Class<? extends Artifact> artifact) {
		this.artifact = artifact;
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
	
	public Class<? extends Artifact> getArtifact() {
		return artifact;
	}
	
	public InfoItem getInfo() {
		return info;
	}
	
	public void setInfo(InfoItem info) {
		this.info = info;
	}
	
	public boolean isUnlocked() {
		return info.isUnlocked();
	}
	
	public ArrayList<UnlockTag> getTags() {
		return info.getTags();
	}
	
	public String getName() {
		return info.getName();
	}
	
	public String getDescr() {
		return info.getDescription();
	}
	
	public String getDescrLong() {
		return info.getDescriptionLong();
	}
	
	public int getCost() {
		return info.getCost();
	}
	
	public void setUnlocked(boolean unlock) {
		info.setUnlocked(unlock);
	}
	
}


