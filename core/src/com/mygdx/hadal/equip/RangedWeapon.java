package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatusProcTime;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;

/**
 * Ranged Weapons are weapons used by clicking somewhere on the screen to probably fire a projcetile or whatever in that direction.
 * Ranged weapons have a clip size and can be reloaded.
 * @author Zachary Tu
 *
 */
public class RangedWeapon extends Equipable {

	protected float ammoPercent;
	protected int ammoSize;
	protected int ammoLeft;
	
	protected float clipPercent;
	protected int clipSize;
	protected int clipLeft;
	
	protected int reloadAmount;
	protected float recoil;
	protected float projectileSpeed;

	protected boolean autoreload;

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
	public RangedWeapon(Schmuck user, String name, int clipSize, int ammoSize, float reloadTime, float recoil, float projectileSpeed, float shootCd, float shootDelay, int reloadAmount,
			boolean autoreload, Sprite weaponSprite, Sprite eventSprite, float chargeTime) {
		super(user, name, shootCd, shootDelay, weaponSprite, eventSprite, chargeTime);
		this.clipSize = clipSize;
		this.clipLeft = clipSize;
		this.clipPercent = 1.0f;
		this.ammoSize = ammoSize;
		this.ammoLeft = ammoSize;
		this.ammoPercent = 1.0f;
		this.reloadTime = reloadTime;
		this.reloadAmount = reloadAmount;
		this.autoreload = autoreload;
		this.recoil = recoil;
		this.projectileSpeed = projectileSpeed;
	}
	
	public RangedWeapon(Schmuck user, String name, int clipSize, int ammoSize, float reloadTime, float recoil, float projectileSpeed, float shootCd, float shootDelay, int reloadAmount,
			boolean autoreload, Sprite weaponSprite, Sprite eventSprite) {
		this(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, autoreload, weaponSprite, eventSprite, 1);
	}

	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {
		
		float powerDiv = shooter.getSchmuck().getPosition().dst(x, y) / projectileSpeed;
		
		float xImpulse = -(shooter.getSchmuck().getPosition().x - x) / powerDiv;
		float yImpulse = -(shooter.getSchmuck().getPosition().y - y) / powerDiv;
		weaponVelo.set(xImpulse, yImpulse);
		
		//Also store the recoil vector and filter.
		this.faction = faction;
		this.x = x;
		this.y = y;
		
		shooter.statusProcTime(StatusProcTime.WHILE_SHOOTING, null, delta, null, this, null);
	}
	
	/**
	 * This is run after the weapon's shootDelay to actually fire.
	 * Here, the stored velo, recoil, filter are used to generate a projectile
	 */
	@Override
	public void execute(PlayState state, BodyData shooter) {
		
		//Check clip size. empty clip = reload instead. This makes reloading automatic.
		if (clipLeft > 0) {
			
			shooter.statusProcTime(StatusProcTime.ON_SHOOT, null, 0, null, this, null);
			
			Vector2 projOffset = new Vector2(weaponVelo).nor().scl(15);
			
			//Shoot			
			fire(state, user, weaponVelo, 
					shooter.getSchmuck().getPosition().x * PPM + projOffset.x,  
					shooter.getSchmuck().getPosition().y * PPM + projOffset.y, faction);
			
			clipLeft--;
			clipPercent = (float)clipLeft / getClipSize();
			
			//If player fires in the middle of reloading, reset reload progress
			reloading = false;
			reloadCd = 0;
			
			//process weapon recoil.
			user.recoil(x, y, recoil * (1 + shooter.getBonusRecoil()));
		}
		
		if (clipLeft <= 0 && autoreload) {
			if (!reloading) {
				reloading = true;
				reloadCd = 0;
			}
			
			if (getAmmoLeft() <= 0) {
				if (shooter instanceof PlayerBodyData) {
					PlayerBodyData p = (PlayerBodyData)shooter;
					p.emptySlot(p.getCurrentSlot());
				}
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

		//Reloading cancels charge
		charging = false;
		chargeCd = 0;
		
		//Keep track of how long schmuck has been reloading. If done, get more ammo.
		if (reloadCd < getReloadTime()) {
			reloadCd += delta;
			
			user.getBodyData().statusProcTime(StatusProcTime.WHILE_RELOADING, null, delta, null, this, null);
			
		} else {
			
			//A reloadAmount of 0 indicates that the whole clip should be reloaded.
			int missingClip = getClipSize() - clipLeft;
			int weaponReloadAmount = Math.min(missingClip, reloadAmount != 0 ? reloadAmount : getClipSize());
			int clipToReload = Math.min(weaponReloadAmount, getAmmoLeft());

			ammoLeft -= clipToReload;
			clipLeft += clipToReload;
			
			clipPercent = (float)clipLeft / getClipSize();

			reloadCd = 0;

			user.getBodyData().statusProcTime(StatusProcTime.ON_RELOAD, null, 0, null, this, null);

			//If clip is full finish reloading.
			if (clipLeft >= getClipSize()) {
				clipLeft = getClipSize();
				clipPercent = 1.0f;
				reloading = false;
			}
			
			//If out of ammo finish reloading.
			if (getAmmoLeft() <= 0) {
				reloading = false;
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
	
	@Override
	public String getAmmoText() {
		return getAmmoLeft() + "";
	}
	
	/**
	 * helper method for gaining ammo. Not currently used, but could be useful for stuff that gives you free reloads
	 * @param gained: amount of ammo to gain.
	 */
	@Override
	public void gainClip(int gained) {
		clipLeft += gained;
		if (clipLeft >= getClipSize()) {
			clipLeft = getClipSize();
		}
		clipPercent = (float)clipLeft / getClipSize();
	}
	
	@Override
	public float getUseCd() {
		return useCd * (1 - user.getBodyData().getRangedFireRate());
	}
	
	@Override
	public int getClipSize() {		
		if (clipSize * user.getBodyData().getBonusClipSize() > 0 && clipSize * user.getBodyData().getBonusClipSize() < 1) {
			return clipSize + 1;
		} else {
			return (int) (clipSize * (1 + user.getBodyData().getBonusClipSize()));
		}
	}

	@Override
	public int getClipLeft() {
		return clipLeft;
	}
	
	@Override
	public int getAmmoLeft() {
		return (int) (ammoLeft * (1 + user.getBodyData().getAmmoCapacity()));
	}
	
	@Override
	public void setClipLeft(int clipLeft) {
		this.clipLeft = clipLeft;
	}

	@Override
	public void setAmmoLeft(int ammoLeft) {
		this.ammoLeft = ammoLeft;
	}
	
	public void setClipLeft() {
		clipLeft = (int) (clipPercent * getClipSize());
	}
}
