package com.mygdx.hadal.equip.artifacts;

import java.util.ArrayList;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class AdministratorCard extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static int numArtifacts = 3;
	
	public AdministratorCard() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private ArrayList<UnlockArtifact> unlocks = new ArrayList<UnlockArtifact>();
			
			@Override
			public void playerCreate() {

				if (state.isHub()) {
					return;
				}
				
				if (inflicted.getSchmuck() instanceof Player) {
					int artifactsAdded = 0;

					while (artifactsAdded < numArtifacts) {
						UnlockArtifact artifact = UnlockArtifact.valueOf(UnlockArtifact.getRandArtfFromPool(state, ""));

						if (((Player) inflicted.getSchmuck()).getPlayerData().addArtifact(artifact, true)) {
							unlocks.add(artifact);
							artifactsAdded++;
						}
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				for (int i = 0; i < unlocks.size(); i++) {
					((Player) inflicted.getSchmuck()).getPlayerData().removeArtifact(unlocks.get(i));
				}
				unlocks.clear();
			}
		});
		return enchantment;
	}
}
