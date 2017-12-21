package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class RangedWeapon extends Equipable{

	public int clipSize;
	public int clipLeft;
	public float reloadTime;
	public int reloadAmount;
	public float recoil;
	public float projectileSpeed;
	public HitboxFactory onShoot;
	
	public Vector2 velo;
	public int x, y;
	public short faction;

	public RangedWeapon(HadalEntity user, String name, int clipSize, float reloadTime, float recoil, 
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

	@Override
	public void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		
		if (clipLeft > 0) {
			Vector3 bodyScreenPosition = new Vector3(shooter.getBody().getPosition().x, shooter.getBody().getPosition().y, 0);
			camera.project(bodyScreenPosition);
			
			float powerDiv = bodyScreenPosition.dst(x, y, 0) / projectileSpeed;
			
			float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
			float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;
			
			this.velo = new Vector2(xImpulse, yImpulse);
			this.faction = faction;
			this.x = x;
			this.y = y;
			
		} else {
			if (!reloading) {
				reloading = true;
				reloadCd = reloadTime;
			}
		}
	}
	
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {
		if (clipLeft > 0) {
			onShoot.makeHitbox(state, velo, shooter.getBody().getPosition().x * PPM, shooter.getBody().getPosition().y * PPM, 
					faction, world, camera, rays);
			
			clipLeft--;
			
			//If player fires in the middle of reloading, reset reload progress
			reloading = false;
			reloadCd = reloadTime;
			
			user.recoil(x, y, recoil);
		} 
		if (clipLeft <= 0) {
			if (!reloading) {
				reloading = true;
				reloadCd = reloadTime;
			}
		}
	}

	public void reload(float delta) {
		if (reloadCd > 0) {
			reloadCd -= delta;
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
