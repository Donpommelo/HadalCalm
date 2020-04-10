package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class SpringLoader extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 3.0f;
	
	private final static Vector2 springRadius = new Vector2(96, 16);
	private final static float springPower = 60.0f;
	private final static float springDuration = 6.0f;
	
	public SpringLoader(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.SPRING.playUniversal(state, user.getPlayer().getMouse().getPixelPosition(), 0.4f, false);
		new Spring(state, user.getPlayer().getMouse().getPixelPosition(), springRadius, new Vector2(0, springPower), springDuration);
		new ParticleEntity(state, user.getPlayer().getMouse().getPixelPosition(), Particle.MOMENTUM, 1.0f, true, particleSyncType.CREATESYNC);

	}
}
