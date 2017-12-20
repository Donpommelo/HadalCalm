package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.schmucks.bodies.MeleeHitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;
import static com.mygdx.hadal.utils.Constants.PPM;



import box2dLight.RayHandler;

public class MeleeWeapon extends Equipable {

	public float momentum;
	public HitboxFactory onSwing;

	public Vector2 velo;
	public short faction;
	
	public MeleeWeapon(HadalEntity user, String name, int useCd, int windup, float momentum,
			HitboxFactory onSwing) {
		super(user, name, useCd, windup);
		this.momentum = momentum;
		this.onSwing = onSwing;
	}

	@Override
	public void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world,
			OrthographicCamera camera, RayHandler rays) {

		Vector3 bodyScreenPosition = new Vector3(shooter.getBody().getPosition().x, shooter.getBody().getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / momentum;
		
		float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;

		this.velo = new Vector2(xImpulse, yImpulse);
		this.faction = faction;
	}
	
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {
		MeleeHitbox hbox = (MeleeHitbox) onSwing.makeHitbox(state, velo, shooter.getBody().getPosition().x * PPM, 
				shooter.getBody().getPosition().y * PPM, faction, world, camera, rays);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = shooter.getBody().body;
		
		hbox.setJoint(jointDef, (float) velo.angleRad());
	}

	@Override
	public void reload() {
		
	}

	@Override
	public String getText() {
		return name;

	}

}
