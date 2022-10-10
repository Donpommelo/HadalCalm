package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes an hbox stick to units or walls upon making contact with them
 * @author Yategnatio Yelderdash
 */
public class ContactStick extends HitboxStrategy {
	
	//does this hbox stick to walls and units? If so, is it already stuck to anything?
	private final boolean stickToWalls, stickToDudes;
	private boolean stuckToTarget;
	
	//The angle that the projectile should be stuck at
	private float angle, targetAngle;
	
	//the target body that the hbox is stuck to
	private HadalEntity target;
	
	//the offset location of this hbox and the stuck entity's position
	private final Vector2 location = new Vector2();
	
	//this stores the relative location of the stuck projectile after accounting for rotation
	private final Vector2 rotatedLocation = new Vector2();
	
	public ContactStick(PlayState state, Hitbox proj, BodyData user, boolean walls, boolean dudes) {
		super(state, proj, user);
		this.stickToWalls = walls;
		this.stickToDudes = dudes;
	}

	@Override
	public void onHit(HadalData fixB) {
		
		//if we have not stuck yet, check to see if we are making contact with an entity we want to stick to.
		//if so, set target (unless touching a wall with no entity), angle and location.
		if (!stuckToTarget) {
			if (fixB != null) {
				if (UserDataType.BODY.equals(fixB.getType()) && stickToDudes) {
					SoundEffect.SQUISH.playSourced(state, hbox.getPixelPosition(), 0.8f, 1.0f);
					stuckToTarget = true;
					
					target = fixB.getEntity();
					angle = hbox.getAngle();
					targetAngle = target.getAngle();
					location.set(hbox.getPosition().x - target.getPosition().x, hbox.getPosition().y - target.getPosition().y);	
				}
				if (UserDataType.WALL.equals(fixB.getType()) && stickToWalls) {
					SoundEffect.SQUISH.playSourced(state, hbox.getPixelPosition(), 0.8f, 1.0f);
					stuckToTarget = true;
					
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
		if (stuckToTarget && target != null) {
			if (target.isAlive()) {
				rotatedLocation.set(location).rotateRad(target.getAngle() - targetAngle);
				hbox.setTransform(target.getPosition().add(rotatedLocation), angle + target.getAngle() - targetAngle);
				hbox.setLinearVelocity(0, 0);
			} else {
				stuckToTarget = false;
				if (hbox.getBody() != null) {
					hbox.getBody().setGravityScale(1.0f);
				}
			}
		}
	}
}
