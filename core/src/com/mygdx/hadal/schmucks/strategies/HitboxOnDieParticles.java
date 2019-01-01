package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnDieParticles extends HitboxStrategy{
	
	private String effect;
	
	public HitboxOnDieParticles(PlayState state, Hitbox proj, BodyData user, String effect) {
		super(state, proj, user);
		this.effect = effect;
	}
	
	@Override
	public void die() {
		new ParticleEntity(state, 
				(int)(this.hbox.getBody().getPosition().x * PPM), 
				(int)(this.hbox.getBody().getPosition().y * PPM),
				effect, 1.0f, true);
	}
}
