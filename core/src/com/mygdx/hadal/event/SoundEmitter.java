package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**

 * @author Zachary Tu
 *
 */
public class SoundEmitter extends Event {
	
	private SoundEffect sound;
	private float volume;
	private boolean global, universal;
	
	public SoundEmitter(PlayState state, Vector2 startPos, Vector2 size, String sound, float volume, boolean global, boolean universal) {
		super(state, startPos, size);
		this.sound = SoundEffect.valueOf(sound);
		this.volume = volume;
		this.global = global;
		this.universal = universal;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (global) {
					if (universal) {
						sound.playUniversal(state, null, volume);
					} else {
						sound.playExclusive(state, null, p, volume);
					}
				} else {
					if (universal) {
						sound.playUniversal(state, event.getPixelPosition(), volume);
					} else {
						sound.playExclusive(state, event.getPixelPosition(), p, volume);
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}	
}
