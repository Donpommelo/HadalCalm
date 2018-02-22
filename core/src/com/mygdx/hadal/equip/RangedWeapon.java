package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
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
	
	protected Vector2 velo;
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
			float projectileSpeed, float shootCd, float shootDelay, int reloadAmount, HitboxFactory onShoot, String spriteId) {
		super(user, name, shootCd, shootDelay, spriteId);
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
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
				
		
		//Convert body coordinates into screen coordinates to calc a starting velocity for the projectile.
		Vector3 bodyScreenPosition = new Vector3(
				shooter.getSchmuck().getBody().getPosition().x,
				shooter.getSchmuck().getBody().getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / projectileSpeed;
		
		float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;
		this.velo = new Vector2(xImpulse, yImpulse);
		
		//Also store the recoil vector and filter.
		this.faction = faction;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * This is run after the weapon's shootDelay to actually fire.
	 * Here, the stored velo, recoil, filter are used to generate a projectile
	 */
	@Override
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {
		
		//Check ckip size. empty clip = reload instead. This makes reloading automatic.
		if (clipLeft > 0 && velo != null) {
			
			//Generate the hitbox(s). This method's return is unused, so it may not return a hitbox or whatever at all.
			onShoot.makeHitbox(user, state, velo, 
					shooter.getSchmuck().getBody().getPosition().x * PPM, 
					shooter.getSchmuck().getBody().getPosition().y * PPM, 
					faction, world, camera, rays);
			
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
	public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {}
	
	/**
	 * This method is run every engine tick when reloading.
	 */
	@Override
	public void reload(float delta) {
		
		//Keep track of how long schmuck has been reloading. If done, get more ammo.
		if (reloadCd > 0) {
			reloadCd -= delta;
		} else {
			
			//A reloadAmount of 0 indicates that the whole clip should be reloaded.
			clipLeft += reloadAmount != 0 ? reloadAmount : getClipSize();
			reloadCd = reloadTime;

			//If clip is full, finish reloading.
			if (clipLeft >= getClipSize()) {
				clipLeft = getClipSize();
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
}
