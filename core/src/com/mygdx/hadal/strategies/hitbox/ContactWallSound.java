package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
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
	
	//This is the sound effect that gets played
	private final SoundEffect sound;
	
	//this is the volume the sound gets played at.
	private final float volume;
	
	//this is the pitch that the sound will get played at. (default is no change. change using factory method.)
	private float pitch = 1.0f;

	private float pitchSpread = 0.0f;

	//This is the max interval the sound can be repeated.
	private static final float procCd = 0.2f;
	
	//this is the slowest the hbox can be moving while still playing the sound
	private static final float minVelo = 3.0f;

	//Does the server notify the client of this sound?
	private boolean synced = true;

	public ContactWallSound(PlayState state, Hitbox proj, BodyData user, SoundEffect sound, float volume) {
		super(state, proj, user);
		this.sound = sound;
		this.volume = volume;
	}
	
	private float procCdCount = procCd;
	@Override
	public void controller(float delta) {
		if (procCdCount < procCd) {
			procCdCount += delta;
		}
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (procCdCount >= procCd && hbox.getLinearVelocity().len2() > minVelo) {
			if (fixB != null) {
				if (fixB.getType().equals(UserDataType.WALL)) {
					procCdCount = 0;

					float newPitch = pitch + (MathUtils.random() - 0.5f) * pitchSpread;
					if (synced) {
						sound.playUniversal(state, hbox.getPixelPosition(), volume, newPitch, false);
					} else {
						sound.playSourced(state, hbox.getPixelPosition(), volume, newPitch);
					}
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

	public ContactWallSound setSynced(boolean synced) {
		this.synced = synced;
		return this;
	}
}
