package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Sound Emitter emits a specific sound when activated. Note that this event has a body so that the sound can be traced to a location for determining pan.
 * <p>
 * Triggered Behavior: When triggered, this will play a chosen sound
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * sound: THe string enum name of the sound played
 * volume: 0.0f - 1.0f- of how loud the sound is
 * global: boolean if the sound is played from a specified location or not (for determining pan)
 * universal: boolean if the sound is played for all players or not
 * 
 * @author Honjo Himeister
 */
public class SoundEmitter extends Event {

	private final SoundEffect sound;
	private final float volume;
	private final boolean global, universal;
	
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
						sound.playUniversal(state, null, volume, false);
					} else {
						sound.playExclusive(state, null, p, volume, false);
					}
				} else {
					if (universal) {
						sound.playUniversal(state, event.getPixelPosition(), volume, false);
					} else {
						sound.playExclusive(state, event.getPixelPosition(), p, volume, false);
					}
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
	}	
}
