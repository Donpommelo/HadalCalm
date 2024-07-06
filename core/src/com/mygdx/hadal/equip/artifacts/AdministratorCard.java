package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class AdministratorCard extends Artifact {

	private static final int SLOT_COST = 3;
	private static final int NUM_ARTIFACTS = 3;
	
	public AdministratorCard() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			private final Array<UnlockArtifact> unlocks = new Array<>();
			@Override
			public void playerCreate(boolean reset) {
				if (state.getMode().isHub()) { return; }
				p.getPlayer().getArtifactIconHelper().addArtifactFlash(UnlockArtifact.ADMINISTRATOR_CARD);

				int artifactsAdded = 0;

				while (artifactsAdded < NUM_ARTIFACTS) {
					UnlockArtifact artifact = UnlockArtifact.getRandArtfFromPool("");
					if (p.getPlayer().getArtifactHelper().addArtifact(artifact, true, false)) {
						unlocks.add(artifact);
						artifactsAdded++;
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				for (UnlockArtifact unlock : unlocks) {
					p.getPlayer().getArtifactHelper().removeArtifact(unlock, true);
				}
				unlocks.clear();
			}
		}).setServerOnly(true);
	}
}
