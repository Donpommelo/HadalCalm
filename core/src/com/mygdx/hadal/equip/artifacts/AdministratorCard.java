package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

import java.util.ArrayList;

public class AdministratorCard extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private static final int numArtifacts = 3;
	
	public AdministratorCard() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private final ArrayList<UnlockArtifact> unlocks = new ArrayList<>();
			
			@Override
			public void playerCreate() {

				if (state.isHub()) {
					return;
				}
				
				if (inflicted.getSchmuck() instanceof Player) {
					int artifactsAdded = 0;

					while (artifactsAdded < numArtifacts) {
						UnlockArtifact artifact = UnlockArtifact.getByName(UnlockArtifact.getRandArtfFromPool(state, ""));

						if (((Player) inflicted.getSchmuck()).getPlayerData().addArtifact(artifact, true, false)) {
							unlocks.add(artifact);
							artifactsAdded++;
						}
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				for (UnlockArtifact unlock : unlocks) {
					((Player) inflicted.getSchmuck()).getPlayerData().removeArtifact(unlock);
				}
				unlocks.clear();
			}
		});
		return enchantment;
	}
}
