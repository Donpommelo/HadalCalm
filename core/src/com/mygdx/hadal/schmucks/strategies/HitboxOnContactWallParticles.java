package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnContactWallParticles extends HitboxStrategy {
	
	private Particle effect;
	
	public HitboxOnContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			new ParticleEntity(state, 
					(int)(this.hbox.getBody().getPosition().x * PPM), 
					(int)(this.hbox.getBody().getPosition().y * PPM),
					effect, 0.1f, true, particleSyncType.CREATESYNC);
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			new ParticleEntity(state, 
					(int)(this.hbox.getBody().getPosition().x * PPM), 
					(int)(this.hbox.getBody().getPosition().y * PPM),
					effect, 0.1f, true, particleSyncType.CREATESYNC);
		}
	}
}
