package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * An active item is an item displayed in the lower right corner. They can be used with thte spacebar and have a cooldown.
 * @author Zachary Tu
 *
 */
public class ActiveItem extends Equipable {

	protected int x, y;
	protected short faction;
	
	protected float currentCharge, maxCharge;
	
	//This indicates whether the active charges over time or by inflicting damage (or potentially, other)
	private chargeStyle style;	
	
	public ActiveItem(Schmuck user, String name, float usecd, float usedelay, float maxCharge, chargeStyle chargeStyle) {
		super(user, name, usecd, usedelay);
		this.maxCharge = maxCharge;
		this.currentCharge = maxCharge;
		this.style = chargeStyle;
	}
	
	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {

		float powerDiv = shooter.getSchmuck().getBody().getPosition().dst(x, y);
		
		float xImpulse = -(shooter.getSchmuck().getBody().getPosition().x - x) / powerDiv;
		float yImpulse = -(shooter.getSchmuck().getBody().getPosition().y - y) / powerDiv;

		weaponVelo.set(xImpulse, yImpulse);
		this.faction = faction;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * This is run after the weapon's useDelay to actually use it.
	 * Here, the stored properties are used to use the item
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (currentCharge >= getMaxCharge()) {
			currentCharge = 0;
			useItem(state, (PlayerBodyData)shooter);
		}

	}
	
	/**
	 * This uses the item. Overridden in each active item.
	 * @param state: Playstate
	 * @param shooter: the user of the item
	 */
	public void useItem(PlayState state, PlayerBodyData shooter) {
		
	}
	
	/**
	 * This is used when the active item charges
	 * @param charge: The amount of charge that the item gains.
	 */
	public void gainCharge(float charge) {
		currentCharge += (charge * (1 + user.getBodyData().getActiveItemChargeRate()));
		if (currentCharge > getMaxCharge()) {
			currentCharge = getMaxCharge();
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
		return currentCharge >= getMaxCharge();
	}
	
	public float chargePercent() {
		return currentCharge / getMaxCharge();
	}	
	
	public float getRemainingCharge() {
		return (getMaxCharge() - currentCharge);
	}

	public float getCurrentCharge() {
		return currentCharge;
	}

	public void setCurrentCharge(float currentCharge) {
		this.currentCharge = currentCharge;
	}

	public float getMaxCharge() {
		return maxCharge * (1 - user.getBodyData().getActiveItemMaxCharge());
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
