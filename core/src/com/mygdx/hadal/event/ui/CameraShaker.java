package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.CameraUtil;

/**
 */
public class CameraShaker extends Event {

	private final float shake, duration, interval;
	private boolean on;
	private float timeLeft;
	public CameraShaker(PlayState state, float shake, float duration, float interval) {
		super(state);
		this.shake = shake;
		this.duration = duration;
		this.interval = interval;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				on = true;
				timeLeft = duration;
			}
		};
	}

	private float controllerCount;
	@Override
	public void controller(float delta) {
		if (on) {
			controllerCount += delta;

			while (controllerCount >= interval) {
				controllerCount -= interval;
				CameraUtil.inflictTrauma(state.getGsm(), shake);
			}

			timeLeft -= delta;
			if (timeLeft <= 0.0f) {
				on = false;
			}
		}
	}

	@Override
	public void clientController(float delta) {
		controller(delta);
	}

	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}