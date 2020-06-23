package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class DeathRagdoll extends Status {
	
	private static final float duration = 0.5f;
	private static final float gravity = 1.0f;
	
	private Sprite sprite;
	private Vector2 size = new Vector2();
	
	public DeathRagdoll(PlayState state, BodyData p, Sprite sprite, Vector2 size) {
		super(state, p);
		this.sprite = sprite;
		this.size.set(size);
	}
	
	@Override
	public void onDeath(BodyData perp) {
		new Ragdoll(state, inflicted.getSchmuck().getPixelPosition(), size, sprite, inflicted.getSchmuck().getLinearVelocity(), duration, gravity, true, false, true);
	}
}
