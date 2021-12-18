package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * this strategy makes a hitbox flash when its lifespan is below a specified threshold. It is usually used for explosive projectiles
 * @author Hugdanoff Hapodilla
 */
public class FlashNearDeath extends HitboxStrategy {
	
	//the hbox will start flashing when its hp falls below this
	private final float flashLifespan;
	
	//the duration of each flash
	private static final float flashDuration = 0.1f;

	//Does the server notify the client of this flash?
	private final boolean synced;

	public FlashNearDeath(PlayState state, Hitbox proj, BodyData user, float flashLifespan, boolean synced) {
		super(state, proj, user);
		this.flashLifespan = flashLifespan;
		this.synced = synced;
	}
	
	@Override
	public void controller(float delta) {
		if (hbox.getLifeSpan() <= flashLifespan) {
			if (hbox.getShaderCount() < -flashDuration) {
				hbox.setShader(Shader.WHITE, flashDuration, synced);
			}
		}
	}
}
