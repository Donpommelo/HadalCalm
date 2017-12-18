package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ProjectileFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class RangedWeapon extends Equipable{

	public int clipSize;
	public int clipLeft;
	public float reloadTime;
	public int reloadAmount;
	public float baseDamage;
	public float recoil;
	public float weaponSwitchTimeMod;
	public float projectileSpeed;
	public ProjectileFactory onShoot;
	
	public RangedWeapon(Schmuck user, String name, int clipSize, float reloadTime, float baseDamage, float recoil, 
			float weaponSwitchTimeMod, float projectileSpeed, int shootCd, int reloadAmount, ProjectileFactory onShoot) {
		super(user, name, shootCd);
		this.clipSize = clipSize;
		this.clipLeft = clipSize;
		this.reloadTime = reloadTime;
		this.reloadAmount = reloadAmount;
		this.baseDamage = baseDamage;
		this.recoil = recoil;
		this.weaponSwitchTimeMod = weaponSwitchTimeMod;
		this.projectileSpeed = projectileSpeed;
		this.onShoot = onShoot;
	}

	@Override
	public void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		
		if (clipLeft > 0) {
			Vector3 bodyScreenPosition = new Vector3(shooter.getBody().getPosition().x, shooter.getBody().getPosition().y, 0);
			camera.project(bodyScreenPosition);
			
			float powerDiv = bodyScreenPosition.dst(x, y, 0) / projectileSpeed;
			
			float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
			float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;
			
			onShoot.makeProjectile(state, new Vector2(xImpulse, yImpulse), shooter.getBody().getPosition().x * PPM, shooter.getBody().getPosition().y * PPM, 
					faction, world, camera, rays);
			
			clipLeft--;
			
			//If player fires in the middle of reloading, reset reload progress
			reloading = false;
			reloadCd = reloadTime;
			
			user.recoil(x, y, recoil);
			
		} else {
			if (!reloading) {
				reloading = true;
				reloadCd = reloadTime;
			}
		}
	}
	
	public void recoil() {
		
	}

	public void reload() {
		if (reloadCd > 0) {
			reloadCd--;
		} else {
			clipLeft += reloadAmount;
			reloadCd = reloadTime;

			if (clipLeft >= clipSize) {
				clipLeft = clipSize;
				reloading = false;
			}
		}
	}

	@Override
	public String getText() {
		if (reloading) {
			return name + ": " + clipLeft + "/" + clipSize + " RELOADING";
		} else {
			return name + ": " + clipLeft + "/" + clipSize;

		}
	}
	
	
}
