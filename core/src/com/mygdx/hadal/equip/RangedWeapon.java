package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * Ranged Weapons are weapons used by clicking somewhere on the screen to probably fire a projcetile or whatever in that direction.
 * Ranged weapons have a clip size and can be reloaded.
 * @author Zachary Tu
 *
 */
public class RangedWeapon extends Equipable{

	protected int clipSize;
	protected int clipLeft;
	
	protected int reloadAmount;
	protected float recoil;
	protected float projectileSpeed;
	protected HitboxFactory onShoot;
	
	protected int x, y;
	protected short faction;

	/**
	 * Ranged weapons, like most equipment, is constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param clipSize: Amount of times the weapon can be fired before reloading
	 * @param reloadTime: The time in seconds it takes to reload this weapon once.
	 * @param recoil: The amount of force pushing the player upon firing.
	 * @param projectileSpeed: The initial velocity of hitboxes created by this weapon.
	 * @param shootCd: The delay after using this tool before you can use a tool again.
	 * @param shootDelay: The delay between pressing the button for this tool and it activating. 
	 * @param reloadAmount: The amount of clip restored upon one reload
	 * @param onShoot: This is a factory that creates a hitbox
	 */
	public RangedWeapon(Schmuck user, String name, int clipSize, float reloadTime, float recoil, 
			float projectileSpeed, float shootCd, float shootDelay, int reloadAmount, HitboxFactory onShoot) {
		super(user, name, shootCd, shootDelay);
		this.clipSize = clipSize;
		this.clipLeft = clipSize;
		this.reloadTime = reloadTime;
		this.reloadAmount = reloadAmount;
		this.recoil = recoil;
		this.projectileSpeed = projectileSpeed;
		this.onShoot = onShoot;
	}
	
	public RangedWeapon(Schmuck user, String name, int clipSize, float reloadTime, float recoil, 
			float projectileSpeed, float shootCd, float shootDelay, int reloadAmount, HitboxFactory onShoot, String spriteId, String spriteEventId) {
		super(user, name, shootCd, shootDelay, spriteId, spriteEventId);
		this.clipSize = clipSize;
		this.clipLeft = clipSize;
		this.reloadTime = reloadTime;
		this.reloadAmount = reloadAmount;
		this.recoil = recoil;
		this.projectileSpeed = projectileSpeed;
		this.onShoot = onShoot;
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
		
		float powerDiv = mouseLocation.dst(x, y, 0) / projectileSpeed;
		
		float xImpulse = -(mouseLocation.x - x) / powerDiv;
		float yImpulse = -(mouseLocation.y - y) / powerDiv;
		weaponVelo.set(xImpulse, yImpulse);
		
		//Also store the recoil vector and filter.
		this.faction = faction;
		this.x = x;
		this.y = y;
		
		shooter.statusProcTime(7, null, delta, null, this, null);
	}
	
	/**
	 * This is run after the weapon's shootDelay to actually fire.
	 * Here, the stored velo, recoil, filter are used to generate a projectile
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		
		//Check clip size. empty clip = reload instead. This makes reloading automatic.
		if (clipLeft > 0) {
			
			shooter.statusProcTime(8, null, 0, null, this, null);
			
			//Generate the hitbox(s). This method's return is unused, so it may not return a hitbox or whatever at all.
			onShoot.makeHitbox(user, state, this, weaponVelo, 
					shooter.getSchmuck().getBody().getPosition().x * PPM, 
					shooter.getSchmuck().getBody().getPosition().y * PPM, 
					faction);
			
			clipLeft--;
			
			//If player fires in the middle of reloading, reset reload progress
			reloading = false;
			reloadCd = getReloadTime();
			
			//process weapon recoil.
			user.recoil(x, y, recoil * (1 + shooter.getBonusRecoil()));
		} 
		if (clipLeft <= 0) {
			if (!reloading) {
				reloading = true;
				reloadCd = getReloadTime();
			}
		}
	}

	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, BodyData bodyData) {}
	
	/**
	 * This method is run every engine tick when reloading.
	 */
	@Override
	public void reload(float delta) {
		
		//Keep track of how long schmuck has been reloading. If done, get more ammo.
		if (reloadCd > 0) {
			reloadCd -= delta;
			
			user.getBodyData().statusProcTime(9, null, delta, null, this, null);
			
		} else {
			
			//A reloadAmount of 0 indicates that the whole clip should be reloaded.
			clipLeft += reloadAmount != 0 ? reloadAmount : getClipSize();
			reloadCd = getReloadTime();

			//If clip is full, finish reloading.
			if (clipLeft >= getClipSize()) {
				clipLeft = getClipSize();
				reloading = false;
				
				user.getBodyData().statusProcTime(10, null, 0, null, this, null);
			}
		}
	}

	/**
	 * Return name + clip + reload status
	 */
	@Override
	public String getText() {
		return clipLeft + "/" + getClipSize();
	}
	
	/**
	 * helper method for gaining ammo. Not currently used, but could be useful for stuff that gives you free reloads
	 * @param gained: amount of ammo to gain.
	 */
	@Override
	public void gainAmmo(int gained) {
		clipLeft += gained;
		if (clipLeft >= getClipSize()) {
			clipLeft = getClipSize();
		}
	}
	
	@Override
	public float getUseCd() {
		return useCd * (1 - user.getBodyData().getRangedFireRate());
	}
	
	public int getClipSize() {
		
		if (clipSize * user.getBodyData().getBonusClipSize() > 0 && clipSize * user.getBodyData().getBonusClipSize() < 1) {
			return clipSize + 1;
		} else {
			return (int) (clipSize * (1 + user.getBodyData().getBonusClipSize()));
		}
	}

	public int getClipLeft() {
		return clipLeft;
	}

	public HitboxFactory getOnShoot() {
		return onShoot;
	}

}
