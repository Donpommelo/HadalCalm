package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 *  @author Bichnold Boppigginbotham
 */
public class ResetVelocity extends Status {

	private final Vector2 originalVelo = new Vector2();

	public ResetVelocity(PlayState state, float i, BodyData p, BodyData v, Vector2 originalVelo) {
		super(state, i, false, p, v);
		originalVelo.set(originalVelo);
	}
	
	@Override
	public void onRemove() {
		inflicted.getSchmuck().setLinearVelocity(originalVelo);
	}
}
