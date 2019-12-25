package com.mygdx.hadal.equip.artifacts;

import java.util.ArrayList;

import com.mygdx.hadal.event.PickupArtifact;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class AdministratorCard extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	public AdministratorCard() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
			private ArrayList<UnlockArtifact> unlocks = new ArrayList<UnlockArtifact>();
			
			@Override
			public void levelStart() {
				
				if (inflicted.getSchmuck() instanceof Player) {
					for (int i = 0; i < 3; i++) {
						UnlockArtifact artifact = UnlockArtifact.valueOf(PickupArtifact.getRandArtfFromPool(""));
						
						unlocks.add(artifact);
//						artifacts.add(((Player)inflicted.getSchmuck()).getPlayerData().addArtifact(artifact));
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				for (int i = 0; i < artifacts.size(); i++) {
//					((Player)inflicted.getSchmuck()).getPlayerData().removeArtifact(unlocks.get(i), artifacts.get(i));
				}
				unlocks.clear();
				artifacts.clear();
			}
		});
		return enchantment;
	}
}
