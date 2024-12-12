package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Spring is an event that, when touched, will push an entity in a set direction
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * vec: vector2 of force that is applied to entities that touch this.
 * 
 * @author Thoggwrangler Tossarian
 */
public class Spring extends Event {

	//this is the frequency that the spring sound can play
	private static final float PROC_CD = 0.25f;

	private final Vector2 vec = new Vector2();
	private float procCdCount = PROC_CD;

	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec.set(vec);
	}
	
	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec.set(vec);
		setEventSprite(Sprite.SPRING);
		setIndependent(true);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					fixB.getEntity().pushMomentumMitigation(vec.x, vec.y);

					if (procCdCount >= PROC_CD) {
						procCdCount = 0;

						SoundManager.play(state, new SoundLoad(SoundEffect.SPRING)
								.setVolume(0.25f)
								.setPosition(getPixelPosition()));

						EffectEntityManager.getParticle(state, new ParticleCreate(Particle.MOMENTUM, getPixelPosition())
								.setLifespan(1.0f));
					}
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE), (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		if (procCdCount < PROC_CD) {
			procCdCount += delta;
		}
	}
	
	@Override
	public void clientController(float delta) {
		this.controller(delta);
	}
	
	@Override
	public void loadDefaultProperties() { setEventSprite(Sprite.SPRING); }
}
