package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Wrudaus Wibanfoo
 */
public class SpringLoader extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 3.0f;
	
	private static final Vector2 springRadius = new Vector2(96, 16);
	private static final float springPower = 75.0f;
	private static final float springDuration = 6.0f;
	
	public SpringLoader(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.SPRING.playUniversal(state, user.getPlayer().getMouse().getPixelPosition(), 0.4f, false);
		new Spring(state, user.getPlayer().getMouse().getPixelPosition(), springRadius, new Vector2(0, springPower), springDuration);
		new ParticleEntity(state, user.getPlayer().getMouse().getPixelPosition(), Particle.MOMENTUM, 1.0f, true, SyncType.CREATESYNC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) springDuration)};
	}
}
