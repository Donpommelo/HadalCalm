package com.mygdx.hadal.statuses;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Invisible units cannot be seen by other players, ai enemies and themselves.
 * Invisibility is removed upon attacking
 * @author Derkhammer Dankabourne
 */
public class Invisibility extends Status {
	
	//fade time determines the window of time where the player can attack before the invisibility status is removed
	private static final float fadeTime = 0.5f;
	private float fadeCount;
	
	public Invisibility(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, SyncType.CREATESYNC).setScale(0.5f);
		
		//set unit's invisibility to true. this is used to turn off movement particles
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setInvisible(2);
		}
		
		fadeCount = fadeTime;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (fadeCount >= 0) {
			fadeCount -= delta;
		}
	}
	
	@Override
	public void onInflict() {
		SoundEffect.MAGIC27_EVIL.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 1.0f, false);
	}
	
	@Override
	public void onRemove() {
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, SyncType.CREATESYNC).setScale(0.5f);
		
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setInvisible(0);
		}
	}
	
	@Override
	public void onShoot(Equippable tool) {
		if (fadeCount <= 0) {
			inflicted.removeStatus(this);
		}
	}
	
	@Override
	public void whileAttacking(float delta, Equippable tool) {
		if (fadeCount <= 0) {
			if (tool instanceof MeleeWeapon) {
				inflicted.removeStatus(this);
			}
		}
	}
	
	@Override
	public void onDeath(BodyData perp) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setInvisible(0);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
