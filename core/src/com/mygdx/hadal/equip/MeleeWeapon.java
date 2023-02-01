package com.mygdx.hadal.equip;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Melee weapons are weapons that are not affected by ranged weapon modifiers and have no clip/ammo mechanic
 * @author Pronkman Pushbird
 */
public class MeleeWeapon extends Equippable {

	/**
	 * @param user: Schmuck that is using this tool.
	 * @param useCd: The delay after using this tool before you can use a tool again.
	 * @param weaponSprite: The equip's sprite when equipped
	 * @param eventSprite: The equip's sprite as a pickup
	 * @param chargeTime: If a charge weapon, how long does it take to fully charge?
	 */	
	public MeleeWeapon(Player user, float useCd, Sprite weaponSprite, Sprite eventSprite, float chargeTime) {
		super(user, useCd, weaponSprite, eventSprite, chargeTime);
	}
	
	public MeleeWeapon(Player user, float swingcd, Sprite weaponSprite, Sprite eventSprite) {
		this(user, swingcd, weaponSprite, eventSprite, 1);
	}

	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	private final Vector2 playerLocation = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		
		playerLocation.set(shooter.getSchmuck().getPixelPosition());
		
		float powerDiv = playerLocation.dst(mouseLocation);
		weaponVelo.set(playerLocation).sub(mouseLocation).scl(-powerDiv);
		
		this.faction = faction;
		this.mouseLocation.set(mouseLocation);
	}
	
	/**
	 * This is run after the weapon's swingDelay to actually swing.
	 * Here, the stored velo, recoil, filter are used to generate a melee hitbox
	 */
	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {
		fire(state, user, user.getPixelPosition(), weaponVelo, faction);
	}
	
	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, PlayerBodyData bodyData) {}

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
