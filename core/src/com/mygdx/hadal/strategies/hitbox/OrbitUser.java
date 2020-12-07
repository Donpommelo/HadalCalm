package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox orbit its user
 * @author Droddjob Dabird
 */
public class OrbitUser extends HitboxStrategy {
	
	//this is the angle of the hbox compared to the player, the distance and the speed that it rotates
	private final float  distance, speed;
	private float angle;
	
	public OrbitUser(PlayState state, Hitbox proj, BodyData user, float startAngle, float distance, float speed) {
		super(state, proj, user);
		this.angle = startAngle;
		this.distance = distance;
		this.speed = speed;
	}
	
	private final Vector2 playerPos = new Vector2();
	private final Vector2 offset = new Vector2();
	@Override
	public void controller(float delta) {
		
		angle += speed * delta;
		
		if (creator.getSchmuck().getBody() != null && creator.getSchmuck().isAlive()) {
			playerPos.set(creator.getSchmuck().getPosition());
			offset.set(0, distance).setAngleDeg(angle);
			hbox.setTransform(playerPos.add(offset), (float) (angle * Math.PI / 180));
		} else {
			hbox.die();
		}
	}
}
