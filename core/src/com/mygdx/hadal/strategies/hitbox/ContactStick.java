package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes an hbox stick to units or walls upon making contact with them
 * @author Zachary Tu
 */
public class ContactStick extends HitboxStrategy {
	
	//does this hbox stick to walls and units? If so, is it already stuck to a wall or unit?
	private boolean stickToWalls, stickToDudes, stuckToWall, stuckToDude;
	
	//The angle that the projectile should be stuck at
	private float angle, targetAngle;
	
	//the target that the hbox is stuck to
	private HadalEntity target;
	
	//the offset location of this hbox and the stuck entity's position
	private Vector2 location = new Vector2();
	
	//this stores the relative location of the stuck projectile after accounting for rotation
	private Vector2 rotatedLocation = new Vector2();
	
	public ContactStick(PlayState state, Hitbox proj, BodyData user, boolean walls, boolean dudes) {
		super(state, proj, user);
		this.stickToWalls = walls;
		this.stickToDudes = dudes;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		
		//if we have not stuck yet, check to see if we are making contact with an entity we want to stick to.
		//if so, set target (unless touching a wall with no entity), angle and location.
		if ((!stuckToWall || stickToWalls) && (!stuckToDude || stickToDudes)) {
			if (fixB != null) {
				if (fixB.getType().equals(UserDataTypes.BODY) && stickToDudes) {
					stuckToDude = true;
					
					target = fixB.getEntity();
					angle = hbox.getAngle();
					targetAngle = target.getAngle();
					location.set(hbox.getPosition().x - target.getPosition().x, hbox.getPosition().y - target.getPosition().y);	
				}
				if (fixB.getType().equals(UserDataTypes.WALL) && stickToWalls) {
					stuckToWall = true;
					
					target = fixB.getEntity();
					angle = hbox.getAngle();
					targetAngle = target.getAngle();
					location.set(hbox.getPosition().x - target.getPosition().x, hbox.getPosition().y - target.getPosition().y);	
				}
			}
		}
	}
	
	@Override
	public void controller(float delta) {
		
		//keep a constant distance/angle from the attached entity (or stay still if attached to a wall)
		if ((stuckToDude || stuckToWall) && target != null && location != null) {
			if (target.isAlive()) {
				rotatedLocation.set(location).rotateRad(target.getAngle() - targetAngle);
				hbox.setTransform(target.getPosition().add(rotatedLocation), angle + target.getAngle() - targetAngle);
				hbox.setLinearVelocity(0, 0);
			} else {
				stuckToDude = false;
			}
		}
	}
}
