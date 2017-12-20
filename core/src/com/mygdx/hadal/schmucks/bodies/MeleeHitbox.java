package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;


public class MeleeHitbox extends Hitbox {

	//This is used exclusively for swinging melee hitboxes.
	public RevoluteJointDef jdef;
	public float angle;

	public MeleeHitbox(PlayState state, float x, float y, int width, int height, int lifespan,
			Vector2 startAngle, short filter, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, x, y, width, height, 0, lifespan, 0, startAngle, filter, true, world, camera, rays);
	}
	
	public void create() {
		super.create();

		jdef.bodyB = body;
		jdef.localAnchorA.set(0, 0);
		jdef.localAnchorB.set(-width / 4 / PPM, 0);
		
		jdef.enableLimit = true;
		jdef.upperAngle = angle;
		jdef.lowerAngle = angle;
		
		world.createJoint(jdef);
				
		this.body.setTransform(startX / PPM, startY / PPM, angle);

	}

	public void setJoint(RevoluteJointDef jointDef, float angle) {
		this.jdef = jointDef;
		this.angle = angle;
	}
}
