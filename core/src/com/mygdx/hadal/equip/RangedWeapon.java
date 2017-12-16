package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.ProjectileFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class RangedWeapon extends Equipable{

	public String name;
	public int clipSize;
	public float reloadTime;
	public float baseDamage;
	public float weaponSwitchTimeMod;
	public float projectileSpeed;
	public ProjectileFactory onShoot;
//	public Status[] statuses?
	
	public RangedWeapon(String name, int clipSize, float reloadTime, float baseDamage, float weaponSwitchTimeMod, float projectileSpeed,
			int shootCd, ProjectileFactory onShoot) {
		super(shootCd);
		this.name = name;
		this.clipSize = clipSize;
		this.reloadTime = reloadTime;
		this.baseDamage = baseDamage;
		this.weaponSwitchTimeMod = weaponSwitchTimeMod;
		this.projectileSpeed = projectileSpeed;
		this.onShoot = onShoot;
	}

	@Override
	public void mouseClicked(PlayState state, BodyData shooter, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		
		Vector3 bodyScreenPosition = new Vector3(shooter.getBody().getPosition().x, shooter.getBody().getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / projectileSpeed;
		
		float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;
		
		onShoot.makeProjectile(state, new Vector2(xImpulse, yImpulse), shooter.getBody().getPosition().x * PPM, shooter.getBody().getPosition().y * PPM, 
				Constants.PLAYER_HITBOX, world, camera, rays);		
	}
}
