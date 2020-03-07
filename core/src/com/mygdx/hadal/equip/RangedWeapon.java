package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.Stats;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;

/**
 * Ranged Weapons are weapons used by clicking somewhere on the screen to probably fire a projcetile or whatever in that direction.
 * Ranged weapons have a clip size, ammo count and can be reloaded.
 * @author Zachary Tu
 *
 */
public class RangedWeapon extends Equipable {

	//The percent of ammo remaining, total ammo capacity and amount of ammo remaining.
	protected float ammoPercent;
	protected int ammoSize;
	protected int ammoLeft;
	
	//The percent of clip remaining, total clip size and amount of shots remaining.
	protected float clipPercent;
	protected int clipSize;
	protected int clipLeft;
	
	//The amount of clip reloaded upon a single reload. (0 means the whole clip is reloaded)
	protected int reloadAmount;
	
	//projectile properties. (size is needed to determine projectile spawn origin)
	protected float projectileSize;
	protected float projectileSpeed;
	
	//The amount of push the weapon applies to the player when fired.
	protected float recoil;

	//Does this weapon automatically start reloading when at 0 clip? (exceptions for weapons with special reload functions)
	protected boolean autoreload;

	/**
	 * Ranged weapons, like most equipment, is constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param clipSize: Amount of times the weapon can be fired before reloading
	 * @param ammoSize: The weapon's ammo capacity
	 * @param reloadTime: The time in seconds it takes to reload this weapon once.
	 * @param recoil: The amount of force pushing the player upon firing.
	 * @param projectileSpeed: The initial velocity of hitboxes created by this weapon.
	 * @param shootCd: The delay after using this tool before you can use a tool again.
	 * @param shootDelay: The delay between pressing the button for this tool and it activating. 
	 * @param reloadAmount: The amount of clip restored upon one reload
	 * @param autoreload: Does this weapon automaticall begin reloading when at 0 clip? (exceptions for weapons that perform special actions on reload.)
	 * @param weaponSprite: The weapon's multitool weapon
	 * @param eventSprite: The weapon's pickup event sprite
	 * @param chargeTime: The wepaon's max charge amount (only used for charge weapons)
	 * @param projectileSize: The wepaon's projectile size. Used to determine projectile starting location offset to avoid wall clipping
	 */	
	public RangedWeapon(Schmuck user, int clipSize, int ammoSize, float reloadTime, float recoil, float projectileSpeed, float shootCd, float shootDelay, int reloadAmount,
			boolean autoreload, Sprite weaponSprite, Sprite eventSprite, float projectileSize, float chargeTime) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite, chargeTime);
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
		this.projectileSize = projectileSize;
	}
	
	public RangedWeapon(Schmuck user, int clipSize, int ammoSize, float reloadTime, float recoil, float projectileSpeed, float shootCd, float shootDelay, int reloadAmount,
			boolean autoreload, Sprite weaponSprite, Sprite eventSprite, float projectileSize) {
		this(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, autoreload, weaponSprite, eventSprite, projectileSize, 1);
	}

	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		
		float powerDiv = shooter.getSchmuck().getPixelPosition().dst(mousePosition) / projectileSpeed;
		weaponVelo.set(shooter.getSchmuck().getPixelPosition()).sub(mousePosition).scl(-1 / powerDiv);
		
		//Also store the recoil vector and filter.
		this.faction = faction;
		this.mouseLocation.set(mousePosition);
	}
	
	/**
	 * This is run after the weapon's shootDelay to actually fire.
	 * Here, the stored velo, recoil, filter are used to generate a projectile
	 */
	private Vector2 projOrigin = new Vector2();
	@Override
	public void execute(PlayState state, BodyData shooter) {
		
		//if we are able to fire, shoot gun
		if (processClip(state, shooter)) {
			shooter.statusProcTime(new ProcTime.Shoot(this));
			
			projOrigin.set(shooter.getSchmuck().getProjectileOrigin(weaponVelo, projectileSize));
			
			//Shoot			
			fire(state, user, projOrigin, weaponVelo, faction);
		}
	}

	/**
	 * This process the clip/ammo info of the weapon firing
	 * @return: boolean of whether the shot was successful or not
	 */
	public boolean processClip(PlayState state, BodyData shooter) {
		
		boolean shotSuccessful = clipLeft > 0;
		
		//Check clip size. empty clip = reload instead. This makes reloading automatic.
		if (shotSuccessful) {
			
			clipLeft--;
			clipPercent = (float)clipLeft / getClipSize();
			
			//If player fires in the middle of reloading, reset reload progress
			reloading = false;
			reloadCd = 0;
			
			//process weapon recoil.
			user.recoil(mouseLocation, recoil * (1 + shooter.getStat(Stats.RANGED_RECOIL)));
		}
		
		//if out of clip, start reloading
		if (clipLeft <= 0 && autoreload) {
			if (!reloading) {
				reloading = true;
				reloadCd = 0;
			}
		}
		
		return shotSuccessful;
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

		if (getAmmoLeft() <= 0) {
			reloading = false;
			return;
		}
		
		//Reloading cancels charge
		charging = false;
		chargeCd = 0;
		
		//Keep track of how long schmuck has been reloading. If done, get more ammo.
		if (reloadCd < getReloadTime()) {
			reloadCd += delta;
		} else {
			
			//A reloadAmount of 0 indicates that the whole clip should be reloaded.
			int missingClip = getClipSize() - clipLeft;
			int weaponReloadAmount = Math.min(missingClip, reloadAmount != 0 ? reloadAmount : getClipSize());
			int clipToReload = Math.min(weaponReloadAmount, getAmmoLeft());

			ammoLeft -= clipToReload;
			clipLeft += clipToReload;
			
			clipPercent = (float)clipLeft / getClipSize();
			ammoPercent = (float)ammoLeft / getAmmoSize();
			
			reloadCd = 0;

			user.getBodyData().statusProcTime(new ProcTime.Reload(this));

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
	public String getText() { return clipLeft + "/" + getClipSize(); }
	
	@Override
	public String getAmmoText() { return getAmmoLeft() + ""; }
	
	/**
	 * helper method for gaining ammo.
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
	public float getUseCd() {return useCd * (1 - user.getBodyData().getStat(Stats.RANGED_ATK_SPD)); }
	
	@Override
	public int getClipSize() {		
		if (clipSize * user.getBodyData().getStat(Stats.RANGED_CLIP) > 0 && clipSize * user.getBodyData().getStat(Stats.RANGED_CLIP) < 1) {
			return clipSize + 1;
		} else {
			return (int) (clipSize * (1 + user.getBodyData().getStat(Stats.RANGED_CLIP)));
		}
	}

	@Override
	public int getClipLeft() { return clipLeft; }
	
	@Override
	public void gainAmmo(float gainedPercent) {
		ammoLeft += gainedPercent * ammoSize;
		if (ammoLeft >= getAmmoSize()) {
			ammoLeft = getAmmoSize();
		}
		ammoPercent = (float)ammoLeft / getAmmoSize();
	}
	
	@Override
	public boolean isOutofAmmo() { 
		return getAmmoLeft() == 0 && getClipLeft() == 0; 
	}
	
	@Override
	public int getAmmoSize() { return (int) (ammoSize  * (1 + user.getBodyData().getStat(Stats.AMMO_CAPACITY))); }
	
	@Override
	public int getAmmoLeft() { return ammoLeft; }
	
	@Override
	public void setClipLeft(int clipLeft) {	this.clipLeft = clipLeft; }

	@Override
	public void setAmmoLeft(int ammoLeft) {	
		this.ammoLeft = ammoLeft; 
		if (this.ammoLeft >= getAmmoSize()) {
			this.ammoLeft = getAmmoSize();
		}
	}
	
	public float getAmmoPercent() { return ammoPercent; }
	
	public void setClipLeft() {	clipLeft = (int) (clipPercent * getClipSize());	}
	
	public void setAmmoLeft() {	ammoLeft = (int) (ammoPercent * getAmmoSize());	}

	public float getProjectileSpeed() {	return projectileSpeed; }
}
