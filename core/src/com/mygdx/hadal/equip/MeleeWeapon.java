package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;
import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.effects.Sprite;

/**
 * Melee weapons are weapons that create MeleeHitboxes that are attached to the user.
 * @author Zachary Tu
 *
 */
public class MeleeWeapon extends Equipable {

	protected float momentum;
	
	protected HitboxFactory onSwing;

	protected int x, y;
	protected short faction;
	
	/**
	 * 
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param swingcd: The delay after using this tool before you can use a tool again.
	 * @param windup: The delay between pressing the button for this tool and it activating. 
	 * @param momentum: reverse recoil. Using a melee wepon will inch the user forwards by a force of this magnitude.
	 * @param onSwing: This is a factory that creates a melee hitbox.
	 */
	public MeleeWeapon(Schmuck user, String name, float swingcd, float windup, float momentum,
			HitboxFactory onSwing) {
		super(user, name, swingcd, windup);
		this.momentum = momentum;
		this.onSwing = onSwing;
	}
	
	public MeleeWeapon(Schmuck user, String name, float swingcd, float windup, float momentum,
			HitboxFactory onSwing, Sprite weaponSprite, Sprite eventSprite) {
		super(user, name, swingcd, windup, weaponSprite, eventSprite);
		this.momentum = momentum;
		this.onSwing = onSwing;
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
		onSwing.makeHitbox(user, state, this, weaponVelo, 
				shooter.getSchmuck().getBody().getPosition().x * PPM, 
				shooter.getSchmuck().getBody().getPosition().y * PPM, 
				faction);
		
		user.recoil(x, y, -momentum * (1 + shooter.getMeleeMomentum()));

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

	/**
	 * returns the weapon name
	 */
	@Override
	public String getText() {
		return "";
	}
	
	@Override
	public float getUseCd() {
		return useCd * (1 - user.getBodyData().getMeleeSwingRate());
	}
	
	@Override
	public float getUseDelay() {
		return useDelay * (1 - user.getBodyData().getMeleeSwingDelay());
	}
}
