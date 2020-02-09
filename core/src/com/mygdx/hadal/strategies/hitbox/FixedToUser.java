package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox fied to the user. It replaces melee hboxes
 * @author Zachary Tu
 *
 */
public class FixedToUser extends HitboxStrategy {
	
	//the point on the player that this hbox is attached to
	private Vector2 center = new Vector2();
	
	//the angle that this hbox is fixed at
	private Vector2 angle = new Vector2();
	
	//does this hbox rotate when the user does?
	private boolean rotate;
	
	public FixedToUser(PlayState state, Hitbox proj, BodyData user, Vector2 angle, Vector2 center, boolean rotate) {
		super(state, proj, user);
		this.center = center;
		this.angle = angle;
		this.rotate = rotate;
	}
	
	@Override
	public void controller(float delta) {
	
		if (!creator.getSchmuck().isAlive()) {
			hbox.queueDeletion();
		} else {
			Vector2 hbLocation = creator.getSchmuck().getPosition().add(center);
			if (rotate) {
				hbox.setTransform(hbLocation, creator.getSchmuck().getBody().getAngle() + angle.angleRad());
			} else {
				hbox.setTransform(hbLocation, angle.angleRad());
			}
		}
	}
}
