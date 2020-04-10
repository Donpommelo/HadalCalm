package com.mygdx.hadal.equip;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.Stats;

/**
 * An active item is an item displayed in the lower right corner. They can be used with the spacebar and have a cooldown or some other charging mechanic.
 * @author Zachary Tu
 *
 */
public class ActiveItem extends Equipable {

	protected short faction;
	
	protected float currentCharge, maxCharge;
	
	//active items charge slower when damaging non-player enemies. (according to this float)
	public static final float enemyDamageChargeMultiplier = 0.1f;
	
	//This indicates whether the active charges over time or by inflicting damage (or potentially, other)
	private chargeStyle style;	
	
	public ActiveItem(Schmuck user, float usecd, float usedelay, float maxCharge, chargeStyle chargeStyle) {
		super(user, usecd, usedelay, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
		this.maxCharge = maxCharge;
		this.currentCharge = maxCharge;
		this.style = chargeStyle;
	}
	
	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {

		float powerDiv = shooter.getSchmuck().getPixelPosition().dst(mousePosition);
		weaponVelo.set(shooter.getSchmuck().getPixelPosition()).sub(mousePosition).scl(-1 / powerDiv);
		
		this.faction = faction;
		this.mouseLocation.set(mousePosition);
	}
	
	/**
	 * This is run after the weapon's useDelay to actually use it.
	 * Here, the stored properties are used to use the item
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		
		user.getBodyData().statusProcTime(new ProcTime.BeforeActiveUse(this));
		
		if (currentCharge >= maxCharge) {
			currentCharge = 0;
			useItem(state, (PlayerBodyData)shooter);
			
			user.getBodyData().statusProcTime(new ProcTime.AfterActiveUse(this));
		}
	}
	
	/**
	 * This uses the item. Overridden in each active item.
	 * @param state: Playstate
	 * @param shooter: the user of the item
	 */
	public void useItem(PlayState state, PlayerBodyData shooter) {}
	
	/**
	 * This is used when the active item charges
	 * @param charge: The amount of charge that the item gains.
	 */
	public void gainCharge(float charge) {
		
		//this is used to keep track of when an item fully charges to play a sound
		boolean uncharged = currentCharge < maxCharge;
		
		currentCharge += (charge * (1 + user.getBodyData().getStat(Stats.ACTIVE_CHARGE_RATE)));
		if (currentCharge >= maxCharge) {
			currentCharge = maxCharge;
			
			if (uncharged) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(user.getState(), user.getPixelPosition(), 0.4f, false);
			}
		}
	}
	
	/**
	 * This is gainCharge except by a percentage of the max charge
	 */
	public void gainChargeByPercent(float charge) {

		//this is used to keep track of when an item fully charges to play a sound
		boolean uncharged = currentCharge < maxCharge;

		currentCharge += (charge * maxCharge * (1 + user.getBodyData().getStat(Stats.ACTIVE_CHARGE_RATE)));
		
		if (currentCharge > maxCharge) {
			currentCharge = maxCharge;
			
			if (uncharged) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(user.getState(), user.getPixelPosition(), 0.4f, false);
			}
		}
	}
	
	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, BodyData bodyData) {}

	@Override
	public boolean reload(float delta) { 
		reloading = false; 
		return false;
	}

	public boolean isReady() { return currentCharge >= maxCharge; }
	
	public float chargePercent() { return currentCharge / maxCharge; }	
	
	public float getRemainingCharge() { return maxCharge - currentCharge; }

	public float getCurrentCharge() { return currentCharge; }

	public void setCurrentCharge(float currentCharge) {	
		this.currentCharge = currentCharge; 
		if (this.currentCharge > maxCharge) {
			this.currentCharge = maxCharge;
		}
	}

	public void setCurrentChargePercent(float currentCharge) {	
		this.currentCharge = currentCharge * maxCharge; 
		if (this.currentCharge > maxCharge) {
			this.currentCharge = maxCharge;
		}
	}

	public chargeStyle getStyle() { return style; }

	/**
	 * This is a vague estimate of how long this item takes to activate.
	 * This is checked by a few effects that want to do something after an active item completes.
	 */
	public float getUseDuration() { return 0.0f; }
	
	public static enum chargeStyle {
		byTime,
		byDamageInflict,
	}
}
