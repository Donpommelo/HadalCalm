package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * 
 * @author Zachary Tu
 *
 */
public class ActiveItem extends Equipable {

	protected int x, y;
	protected short faction;
	
	protected float currentCharge, maxCharge;
	
	private chargeStyle style;
	
	public ActiveItem(Schmuck user, String name, float usecd, float usedelay, float maxCharge, chargeStyle chargeStyle) {
		super(user, name, usecd, usedelay);
		this.maxCharge = maxCharge;
		this.currentCharge = 0;
		this.style = chargeStyle;
	}
	
	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {

		mouseLocation.set(shooter.getSchmuck().getBody().getPosition().x,
				shooter.getSchmuck().getBody().getPosition().y, 0);
		
		state.camera.project(mouseLocation);
		
		float powerDiv = mouseLocation.dst(x, y, 0);
		
		float xImpulse = -(mouseLocation.x - x) / powerDiv;
		float yImpulse = -(mouseLocation.y - y) / powerDiv;

		weaponVelo.set(xImpulse, yImpulse);
		this.faction = faction;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * This is run after the weapon's swingDelay to actually swing.
	 * Here, the stored velo, recoil, filter are used to generate a melee hitbox
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (currentCharge >= maxCharge) {
			currentCharge = 0;
			useItem(state, shooter);
		}

	}
	
	public void useItem(PlayState state, BodyData shooter) {
		
	}
	
	public void gainCharge(float charge) {
		currentCharge += charge;
		if (currentCharge > maxCharge) {
			currentCharge = maxCharge;
		}
	}
	
	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, BodyData bodyData) {}

	/**
	 * Default behaviour for reloading is nothing.
	 * Override this for special weapon arts or whatever.
	 */
	@Override
	public void reload(float delta) { reloading = false; }

	public boolean isReady() {
		return currentCharge >= maxCharge;
	}
	
	public float chargePercent() {
		return currentCharge / maxCharge;
	}	
	
	public float getRemainingCharge() {
		return (maxCharge - currentCharge);
	}

	/**
	 * returns the weapon name
	 */
	@Override
	public String getText() {
		return "";
	}

	public float getCurrentCharge() {
		return currentCharge;
	}


	public void setCurrentCharge(float currentCharge) {
		this.currentCharge = currentCharge;
	}


	public chargeStyle getStyle() {
		return style;
	}


	public void setStyle(chargeStyle style) {
		this.style = style;
	}

	public static enum chargeStyle {
		byTime,
		byDamage
	}
}
