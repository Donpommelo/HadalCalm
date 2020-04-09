package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * @author Zachary Tu
 *
 */
public class ContactWallSound extends HitboxStrategy {
	
	private SoundEffect sound;
	private float volume;
	
	private static final float procCd = 0.25f;
	private static final float minVelo = 3.0f;
	
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
			procCdCount = 0;
			
			if (fixB != null) {
				if (fixB.getType().equals(UserDataTypes.WALL)) {
					sound.playUniversal(state, hbox.getPixelPosition(), volume, false);
				}
			}
		}
	}
}
