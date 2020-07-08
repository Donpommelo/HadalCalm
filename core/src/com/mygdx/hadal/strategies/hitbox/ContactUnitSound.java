package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a sound when the hbox hits a player
 * @author Zachary Tu
 */
public class ContactUnitSound extends HitboxStrategy {
	
	//this is the sound effect that will be played.
	private SoundEffect sound;
	
	//this is the volume that the sound wull be played at
	private float volume;
	
	//if the hbox is still, we ignore the velocity requirement before playing a sound
	private boolean still;
	
	//This is the max interval the sound can be repeated.
	private static final float procCd = 0.1f;
	
	//this is the slowest the hbox can be moving while still playing the sound. This is to avoid non-moving hboxes from repeatedly playing their sound
	private static final float minVelo = 3.0f;
		
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
				if (fixB.getType().equals(UserDataTypes.BODY)) {
					procCdCount = 0;
					sound.playUniversal(state, hbox.getPixelPosition(), volume, false);
				}
			}
		}
	}
}
