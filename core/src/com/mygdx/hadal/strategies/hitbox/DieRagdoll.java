package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.managers.RagdollManager;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a ragdoll when its hbox dies.
 * the ragdoll will have the same sprite as the hbox itself
 * @author Ferroway Frasteban
 *
 */
public class DieRagdoll extends HitboxStrategy {

	private static final float RAGDOLL_DURATION = 0.75f;

	public DieRagdoll(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void die() {
		RagdollManager.getRagdoll(state, new RagdollCreate()
				.setSprite(hbox.getSprite())
				.setPosition(this.hbox.getPixelPosition())
				.setSize(hbox.getSize())
				.setLifespan(RAGDOLL_DURATION)
				.setGravity(1.0f)
				.setFade());
	}
}
