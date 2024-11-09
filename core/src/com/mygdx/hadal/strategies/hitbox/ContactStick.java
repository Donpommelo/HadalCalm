package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.event.Wall;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
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
	protected boolean stuckToTarget;

	//The angle that the projectile should be stuck at
	private float angle, targetAngle;

	//the target body that the hbox is stuck to. TargetBody is separate in case of entities with multiple bodies
	protected HadalEntity target;
	protected Body targetBody;

	//the offset location of this hbox and the stuck entity's position
	private final Vector2 location = new Vector2();

	//this stores the relative location of the stuck projectile after accounting for rotation
	private final Vector2 rotatedLocation = new Vector2();

	private float stuckLifespan;

	public ContactStick(PlayState state, Hitbox proj, BodyData user, boolean walls, boolean dudes) {
		super(state, proj, user);
		this.stickToWalls = walls;
		this.stickToDudes = dudes;
		this.stuckLifespan = proj.getLifeSpan();

		//set this here since sticky hboxes can't use the adjust angle strategy
		hbox.setAdjustAngle(true);
	}

	@Override
	public void onHit(HadalData fixB, Body body) {
		
		//if we have not stuck yet, check to see if we are making contact with an entity we want to stick to.
		//if so, set target (unless touching a wall with no entity), angle and location.
		if (!stuckToTarget) {
			if (fixB != null) {
				if (UserDataType.BODY.equals(fixB.getType()) && stickToDudes) {
					onStick(fixB.getEntity(), body);
				}
				if (UserDataType.WALL.equals(fixB.getType()) && stickToWalls) {
					if (fixB.getEntity() instanceof Wall) {
						onStick(state.getAnchor(), body);
					} else {
						onStick(fixB.getEntity(), body);
					}
				}
			}
		}
	}

	@Override
	public void controller(float delta) {

		//keep a constant distance/angle from the attached entity (or stay still if attached to a wall)
		if (stuckToTarget && target != null) {
			if (target.isAlive()) {
				if (target != state.getAnchor()) {
					float currentAngle = targetBody.getAngle();
					rotatedLocation.set(location).rotateRad(currentAngle - targetAngle);
					hbox.setTransform(targetBody.getPosition().add(rotatedLocation), angle + currentAngle - targetAngle);
				} else {
					hbox.setTransform(location, angle + target.getAngle() - targetAngle);
				}
				hbox.setLinearVelocity(0, 0);
			} else {
				onUnstick();
			}
		}
	}

	protected void onStick(HadalEntity target, Body body) {
		SoundManager.play(state, new SoundLoad(SoundEffect.SQUISH)
				.setVolume(0.8f)
				.setPosition(hbox.getPixelPosition()));

		stuckToTarget = true;

		this.target = target;
		this.targetBody = body;

		angle = hbox.getAngle();
		targetAngle = targetBody.getAngle();
		location.set(hbox.getPosition()).sub(body.getPosition());

		hbox.makeUnreflectable();
		hbox.setLifeSpan(stuckLifespan);
	}

	protected void onUnstick() {
		stuckToTarget = false;
		if (hbox.getBody() != null) {
			hbox.getBody().setGravityScale(1.0f);
		}
	}

	public ContactStick setStuckLifespan(float stuckLifespan) {
		this.stuckLifespan = stuckLifespan;
		return this;
	}
}
