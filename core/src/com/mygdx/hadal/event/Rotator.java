package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.states.PlayState.SYNC_TIME;

/**
 * Rotators connect to other events and either apply a continuous or instant rotation
 * <p>
 * Triggered Behavior: when triggered, if continuous, this event will toggle on/off the rotating of the connected event
 * Triggering Behavior: This event's connected event is the event that will be rotated
 * <p>
 * Fields:
 * continuous: do we apply rotation continuously or instantly? Default: true
 * angle: The amount of rotation to apply (either set its angular velocity or its angle)
 * 
 * @author Smeggdrop Shudale
 */
public class Rotator extends Event {

	private final boolean continuous;
	private final float angle;
	
	public Rotator(PlayState state, boolean continuous, float angle) {
		super(state);
		this.continuous = continuous;
		this.angle = angle;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (continuous) {
					if (null != getConnectedEvent()) {
						if (null != getConnectedEvent().getBody()) {
							if (0 == getConnectedEvent().getAngularVelocity()) {
								getConnectedEvent().setAngularVelocity(angle);
							} else {
								getConnectedEvent().setAngularVelocity(0);
							}
						}
					}
				} else {
					if (null != getConnectedEvent()) {
						getConnectedEvent().setTransform(getConnectedEvent().getPosition(), angle * MathUtils.degRad);
					}
				}
			}
		};
	}

	private boolean startSynced;
	private float syncDelayCount;
	@Override
	public void clientController(float delta) {
		if (!startSynced) {
			syncDelayCount += delta;

			if (syncDelayCount >= 2.0f) {
				startSynced = true;
				PacketManager.clientTCP(new Packets.RequestStartSyncedEvent(triggeredID));
			}
		}
	}

	private final Vector2 delayedAngle = new Vector2(0, 1);
	@Override
	public Object onServerSyncInitial() {
		if (!continuous) { return null; }

		if (null == getConnectedEvent()) {
			return null;
		} else {
			delayedAngle.setAngleRad(getConnectedEvent().getAngle());
			return new Packets.CreateStartSyncedEvent(state.getTimer(), triggeredID, getConnectedEvent().getTriggeredID(),
					delayedAngle, new Vector2());
		}
	}

	@Override
	public void onClientSyncInitial(float timer, Event target, Vector2 position, Vector2 velocity) {
		if (null != getConnectedEvent()) {
			delayedAngle.set(position).setAngleDeg(position.angleDeg() - angle * 2 * SYNC_TIME);
			getConnectedEvent().setTransform(getConnectedEvent().getPosition(), delayedAngle.angleRad());
		}
	}
}
