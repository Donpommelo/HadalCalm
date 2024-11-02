package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 *  This strategy creates a sound when the hbox hits a wall
 *  It contains additional logic to prevent repeatedly making the sound over and over when the hbox is effectively motionless.
 * @author Donston Dandrea
 */
public class ContactWallSound extends HitboxStrategy {

	//This is the max interval the sound can be repeated.
	private static final float PROC_CD = 0.2f;

	//this is the slowest the hbox can be moving while still playing the sound
	private static final float MIN_VELO = 3.0f;

	//This is the sound effect that gets played
	private final SoundEffect sound;
	
	//this is the volume the sound gets played at.
	private final float volume;
	
	//this is the pitch that the sound will get played at. (default is no change. change using factory method.)
	private float pitch = 1.0f;

	private float pitchSpread = 0.0f;

	public ContactWallSound(PlayState state, Hitbox proj, BodyData user, SoundEffect sound, float volume) {
		super(state, proj, user);
		this.sound = sound;
		this.volume = volume;
	}
	
	private float procCdCount = PROC_CD;
	@Override
	public void controller(float delta) {
		if (procCdCount < PROC_CD) {
			procCdCount += delta;
		}
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (procCdCount >= PROC_CD && hbox.getLinearVelocity().len2() > MIN_VELO) {
			if (fixB != null) {
				if (UserDataType.WALL.equals(fixB.getType())) {
					procCdCount = 0;

					float newPitch = pitch + (MathUtils.random() - 0.5f) * pitchSpread;
					SoundManager.play(state, new SoundLoad(sound)
							.setVolume(volume)
							.setPitch(newPitch)
							.setPosition(hbox.getPixelPosition()));
				}
			}
		}
	}
	
	public ContactWallSound setDuration(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public ContactWallSound setPitchSpread(float pitchSpread) {
		this.pitchSpread = pitchSpread;
		return this;
	}
}
