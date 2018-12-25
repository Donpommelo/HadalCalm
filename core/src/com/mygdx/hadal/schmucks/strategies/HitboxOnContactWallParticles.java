package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnContactWallParticles extends HitboxStrategy{
	
	private String effect;
	
	public HitboxOnContactWallParticles(PlayState state, Hitbox proj, BodyData user, String effect) {
		super(state, proj, user);
		this.effect = effect;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			new ParticleEntity(state, hbox, effect, 1.0f, 0, true);
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			new ParticleEntity(state, hbox, effect, 1.0f, 0, true);
		}
	}
}