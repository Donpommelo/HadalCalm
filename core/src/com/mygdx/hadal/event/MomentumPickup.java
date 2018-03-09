package com.mygdx.hadal.event;

import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class MomentumPickup extends Event {

	private Vector2 momentum;
	
	private static final int width = 16;
	private static final int height = 16;
	
	private static final float lifespan = 8.0f;
	private float lifeLeft;
	
	private static final String name = "Momentum Bubble";

	public MomentumPickup(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y, 
			Vector2 momentum) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.momentum = momentum;
		this.lifeLeft = lifespan; 
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive()) {
					p.getMomentums().addLast(momentum);
					queueDeletion();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER| Constants.BIT_SENSOR | Constants.BIT_WALL),
				(short) 0, true, eventData);
		
		Vector2 v = new Vector2(2,0);

		Random rnd = new Random();
		double rotationAngle = 2.0 * Math.PI * rnd.nextDouble();

		Vector2 vRotated = new Vector2( 
				(float)((v.x)*Math.cos(rotationAngle) + (v.y)*Math.sin(rotationAngle)),
		   (float)((v.y)*Math.cos(rotationAngle) - (v.x)*Math.sin(rotationAngle))
		);
		
		body.setLinearVelocity(vRotated);
	}

	@Override
	public void controller(float delta) {
		lifeLeft -= delta;
		if (lifeLeft <= 0) {
			queueDeletion();
		}
	}
	
	@Override
	public String getText() {
		return momentum + " (PRESS E TO PICKUP)";
	}

}
