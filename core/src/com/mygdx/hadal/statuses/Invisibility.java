package com.mygdx.hadal.statuses;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Invisible units cannot be seen by other players, ai enemies and themselves.
 * Invisibility is removed upon attacking
 * @author Derkhammer Dankabourne
 */
public class Invisibility extends Status {
	
	//fade time determines the window of time where the player can attack before the invisibility status is removed
	private static final float FADE_TIME = 0.5f;
	private float fadeCount;
	
	public Invisibility(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);

		//set unit's invisibility to true. this is used to turn off movement particles
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setInvisible(true);
		}
		
		fadeCount = FADE_TIME;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (fadeCount >= 0) {
			fadeCount -= delta;
		}
	}
	
	@Override
	public void onRemove() {
		SyncedAttack.INVISIBILITY_OFF.initiateSyncedAttackNoHbox(state, inflicted.getSchmuck(),
				inflicted.getSchmuck().getPixelPosition(),true);
	}
	
	@Override
	public void onShoot(Equippable tool) {
		if (fadeCount <= 0) {
			inflicted.removeStatus(this);
		}
	}
	
	@Override
	public void whileAttacking(float delta, Equippable tool) {
		if (fadeCount <= 0) {
			if (tool instanceof MeleeWeapon) {
				inflicted.removeStatus(this);
			}
		}
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setInvisible(false);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
