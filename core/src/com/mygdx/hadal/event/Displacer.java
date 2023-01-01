package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import static com.mygdx.hadal.constants.Constants.PPM;

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
 * @author Thescargot Twarugula
 */
public class Displacer extends Event {

	//amount to scale the displacement momentum by.
	private static final float momentumScale = 50.0f;

	private final Vector2 vec = new Vector2();

	private Vector2 offset;
	private final Vector2 newOffset = new Vector2();
	
	public Displacer(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec.set(vec);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {

			/**
			 * Because this moves things using Transform instead of physics, we don't want to operate on other client players
			 */
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					if (!state.isServer() && fixB.getEntity() instanceof Player player) {
						if (state.getPlayer().equals(player)) {
							schmucks.add(fixB.getEntity());
						}
					} else {
						schmucks.add(fixB.getEntity());
					}
				}
			}

			@Override
			public void onRelease(HadalData fixB) {
				super.onRelease(fixB);
				
				//when leaving a displacer, we simulate physics by applying a push in the direction of the displacement
				if (fixB != null) {
					if (fixB.getEntity().getBody() != null) {
						if (getConnectedEvent() != null) {
							fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().add(newOffset.x * momentumScale, newOffset.y * momentumScale));
						} else {
							fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().add(vec.x * momentumScale, vec.y * momentumScale));
						}
					}
				}
			}
		};
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
	}
	
	private final Vector2 connectedLocation = new Vector2();
	private float controllerCount;
	@Override
	public void controller(float delta) {

		controllerCount += delta;
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;

			if (getConnectedEvent() == null) {

				//no connected event: displace all affected entities.
				for (HadalEntity entity : eventData.getSchmucks()) {
					entity.setTransform(entity.getPosition().add(vec), entity.getAngle());
				}
			} else if (getConnectedEvent().isAlive()) {

				//calculate movement of connected event. Move self and all affected entities by that amount
				if (offset == null) {
					offset = new Vector2(getConnectedEvent().getPixelPosition()).sub(startPos).scl(1 / PPM);
				}
				connectedLocation.set(getConnectedEvent().getPosition()).sub(offset);

				newOffset.set(connectedLocation).sub(getPosition());

				for (HadalEntity entity : eventData.getSchmucks()) {
					entity.setTransform(entity.getPosition().add(newOffset.x, 0), entity.getAngle());
				}
				setTransform(connectedLocation, getAngle());
			}
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}
}
