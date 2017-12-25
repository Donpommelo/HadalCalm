package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;
import static com.mygdx.hadal.utils.Constants.PPM;



import box2dLight.RayHandler;

public class MeleeWeapon extends Equipable {

	public float momentum;
	public HitboxFactory onSwing;

	public Vector2 velo;
	public int x, y;
	public short faction;
	
	public MeleeWeapon(Schmuck user, String name, float swingcd, float windup, float momentum,
			HitboxFactory onSwing) {
		super(user, name, swingcd, windup);
		this.momentum = momentum;
		this.onSwing = onSwing;
	}

	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y, World world,
			OrthographicCamera camera, RayHandler rays) {

		Vector3 bodyScreenPosition = new Vector3(
				shooter.getSchmuck().getBody().getPosition().x, 
				shooter.getSchmuck().getBody().getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0);
		
		float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;

		this.velo = new Vector2(xImpulse, yImpulse);
		this.faction = faction;
		this.x = x;
		this.y = y;
	}
	
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {
		onSwing.makeHitbox(user, state, velo, 
				shooter.getSchmuck().getBody().getPosition().x * PPM, 
				shooter.getSchmuck().getBody().getPosition().y * PPM, 
				faction, world, camera, rays);
		
		user.recoil(x, y, -momentum);

	}
	
	@Override
	public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {}

	@Override
	public void reload(float delta) {}

	@Override
	public String getText() {
		return name;
	}
}
