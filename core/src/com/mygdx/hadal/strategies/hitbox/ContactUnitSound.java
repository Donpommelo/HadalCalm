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
 * This strategy creates a sound when the hbox hits a player
 * @author Plorbzog Phovington
 */
public class ContactUnitSound extends HitboxStrategy {
	
	//this is the sound effect that will be played.
	private final SoundEffect sound;
	
	//this is the volume that the sound will be played at
	private final float volume;
	
	//this is the pitch that the sound will get played at. (default is no change. change using factory method.)
	private float pitch = 1.0f;

	//this is the variance in pitch
	private float pitchSpread = 0.0f;

	//if the hbox is still, we ignore the velocity requirement before playing a sound. (mostly used for sticky bombs)
	private final boolean still;
	
	//This is the max interval the sound can be repeated.
	private static final float procCd = 0.1f;
	
	//this is the slowest the hbox can be moving while still playing the sound. This is to avoid non-moving hboxes from
	// repeatedly playing their sound
	private static final float minVelo = 3.0f;

	//Does the server notify the client of this sound?
	private boolean synced = true;

	public ContactUnitSound(PlayState state, Hitbox proj, BodyData user, SoundEffect sound, float volume, boolean still) {
		super(state, proj, user);
		this.sound = sound;
		this.volume = volume;
		this.still = still;
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
		if (procCdCount >= procCd && (hbox.getLinearVelocity().len2() > minVelo || still)) {
			if (fixB != null) {
				if (fixB.getType().equals(UserDataType.BODY)) {
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
	
	public ContactUnitSound setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public ContactUnitSound setPitchSpread(float pitchSpread) {
		this.pitchSpread = pitchSpread;
		return this;
	}

	public ContactUnitSound setSynced(boolean synced) {
		this.synced = synced;
		return this;
	}
}
