package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Displacers apply a continuous displacement to entities overlapping them. This is distinct from currents in that it directly transforms the entity's position instead of using physics.
 * This can be used for conveyor belts as well as moving platforms (see triggering behavior).
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: If not null, this represents an event that the displacer is connected to and will follow the movements of another event. 
 * 
 * When following another event, the displacer displaces equal to its own movement. This makes a player follow a horizontal platform they are standing on with no friction.
 * 
 * Fields:
 * displaceX, displaceY: floats that indicate the amount of movement that should be performed.
 * 
 * @author Zachary Tu
 *
 */
public class Displacer extends Event {
	
	//displacement applied every 1/60 seconds
	private Vector2 vec;
	private float momentumScale = 50.0f;

	private Vector2 offset;
	private Vector2 newOffset = new Vector2();
	
	public Displacer(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec = vec;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onRelease(HadalData fixB) {
				super.onRelease(fixB);
				
				//when leaving a displacer, we simulate physics by applying a push in the direction of the displacement
				if (fixB != null) {
					if (fixB.getEntity().getBody() != null) {
						fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().add(vec.x * momentumScale, vec.y * momentumScale));
						if (getConnectedEvent() != null) {
							if (!getConnectedEvent().getBody().equals(null)) {
								fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().add(newOffset.x * momentumScale, newOffset.y * momentumScale));
							}
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() == null) {
			
			//no connected event: displace all affected entities.
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.setTransform(entity.getPosition().add(vec), entity.getBody().getAngle());
			}
		} else if (!getConnectedEvent().getBody().equals(null) && getConnectedEvent().isAlive()) {
			
			//calculate movement of connected event. Move self and all affected entities by that amount
			if (offset == null) {
				offset = new Vector2(getConnectedEvent().getPixelPosition()).sub(startPos).scl(1 / PPM);
			}
			
			newOffset.set(getConnectedEvent().getBody().getPosition()).sub(offset).sub(getBody().getPosition());
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.setTransform(entity.getPosition().add(newOffset.x, 0), entity.getBody().getAngle());
			}

			setTransform(getConnectedEvent().getBody().getPosition().sub(offset), getBody().getAngle());
		}
	}
}
