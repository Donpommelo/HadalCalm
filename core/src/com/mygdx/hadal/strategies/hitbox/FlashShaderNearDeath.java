package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Constants;

/**
 * this strategy makes a hitbox flash when its lifespan is below a specified threshold. It is usually used for explosive projectiles
 * @author Hugdanoff Hapodilla
 */
public class FlashShaderNearDeath extends HitboxStrategy {

	//the hbox will start flashing when its hp falls below this
	private final float flashLifespan;

	public FlashShaderNearDeath(PlayState state, Hitbox proj, BodyData user, float flashLifespan) {
		super(state, proj, user);
		this.flashLifespan = flashLifespan;
	}
	
	@Override
	public void controller(float delta) {
		if (hbox.getLifeSpan() <= flashLifespan) {
			if (hbox.getShaderHelper().getShaderStaticCount() < -Constants.FLASH) {
				hbox.getShaderHelper().setStaticShader(Shader.WHITE, Constants.FLASH);
			}
		}
	}
}
