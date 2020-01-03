package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * 
 * @author Zachary Tu
 *
 */
public class ShaderChanger extends Event {

	private Shader shader;
	private float duration;
	
	public ShaderChanger(PlayState state, String shader, float duration) {
		super(state);
		this.shader = Shader.valueOf(shader);
		this.duration = duration;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.setShader(shader, duration);
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
