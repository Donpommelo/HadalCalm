package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
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

	//is the creation of this ragdoll communicated to the client or processed independently
	private final boolean synced;

	public DieRagdoll(PlayState state, Hitbox proj, BodyData user, boolean synced) {
		super(state, proj, user);
		this.synced = synced;
	}
	
	@Override
	public void die() {
		Ragdoll ragdoll = new Ragdoll(state, this.hbox.getPixelPosition(), hbox.getSize(), hbox.getSprite(), new Vector2(),
				RAGDOLL_DURATION, 1.0f, false, false, synced).setFade();

		if (!state.isServer()) {
			((ClientState) state).addEntity(ragdoll.getEntityID(), ragdoll, false, ClientState.ObjectLayer.HBOX);
		}
	}
}
