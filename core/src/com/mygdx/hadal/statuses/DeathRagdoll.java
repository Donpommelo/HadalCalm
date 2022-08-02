package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This status makes units spawn a ragdoll upon death. This is used by certain enemies
 * @author Rameyer Relforpo
 */
public class DeathRagdoll extends Status {
	
	private static final float DURATION = 1.0f;
	private static final float GRAVITY = 1.0f;
	
	//this is the sprite of the ragdoll to be spawned and the size of the ragdoll
	private final Sprite sprite;
	private final Vector2 size = new Vector2();
	
	public DeathRagdoll(PlayState state, BodyData p, Sprite sprite, Vector2 size) {
		super(state, p);
		this.sprite = sprite;
		this.size.set(size);
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source) {
		new Ragdoll(state, inflicted.getSchmuck().getPixelPosition(), size, sprite, inflicted.getSchmuck().getLinearVelocity(),
				DURATION, GRAVITY, true, false, true, true);
	}
}
