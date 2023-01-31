package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Wrudaus Wibanfoo
 */
public class SpringLoader extends ActiveItem {

	private static final float MAX_CHARGE = 3.0f;
	
	private static final Vector2 SPRING_RADIUS = new Vector2(96, 16);
	private static final float SPRING_POWER = 75.0f;
	private static final float SPRING_DURATION = 6.0f;
	
	public SpringLoader(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.SPRING.playUniversal(state, user.getPlayer().getMouseHelper().getPixelPosition(), 0.4f, false);
		new Spring(state, user.getPlayer().getMouseHelper().getPixelPosition(), SPRING_RADIUS, new Vector2(0, SPRING_POWER), SPRING_DURATION);
		new ParticleEntity(state, user.getPlayer().getMouseHelper().getPixelPosition(), Particle.MOMENTUM, 1.0f, true, SyncType.CREATESYNC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) SPRING_DURATION)};
	}
}
