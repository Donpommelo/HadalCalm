package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;

/**
 * Melee weapons are weapons thatare not affected by ranged weapon modifiers and have no clip/ammo mechanic
 * @author Zachary Tu
 *
 */
public class MeleeWeapon extends Equipable {

	/**
	 * 
	 * @param user: Schmuck that is using this tool.
	 * @param swingcd: The delay after using this tool before you can use a tool again.
	 * @param windup: The delay between pressing the button for this tool and it activating.
	 * @param equipSprite: The equip's sprite when equipped
	 * @param eventSprite: The equip's sprite as a pickup
	 * @param chargeTime: If a charge weapon, how long does it take to fully charge?
	 */	
	public MeleeWeapon(Schmuck user, float swingcd, float windup, Sprite weaponSprite, Sprite eventSprite, float chargeTime) {
		super(user, swingcd, windup, weaponSprite, eventSprite, chargeTime);
	}
	
	public MeleeWeapon(Schmuck user, float swingcd, float windup, Sprite weaponSprite, Sprite eventSprite) {
		this(user, swingcd, windup, weaponSprite, eventSprite, 1);
	}

	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		
		float powerDiv = shooter.getSchmuck().getPixelPosition().dst(mouseLocation);
		
		weaponVelo.set(shooter.getSchmuck().getPixelPosition()).sub(mouseLocation).scl(-powerDiv);
		this.faction = faction;
		this.mouseLocation.set(mouseLocation);
	}
	
	/**
	 * This is run after the weapon's swingDelay to actually swing.
	 * Here, the stored velo, recoil, filter are used to generate a melee hitbox
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		fire(state, user, user.getPixelPosition(), weaponVelo, faction);
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
	public boolean reload(float delta) { 
		reloading = false; 
		return false;
	}
}
