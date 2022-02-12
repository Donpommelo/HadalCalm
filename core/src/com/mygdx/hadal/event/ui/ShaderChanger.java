package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A ShaderChanger changes the shader of the state (the state background).
 * 
 * Triggered Behavior: When triggered, this event will change the shader.
 * Triggering Behavior: N/A
 * 
 * Fields:
 * shader: the shader to use, Default: NOTHING
 * 
 * @author Himichurri Hanut
 */
public class ShaderChanger extends Event {

	private final Shader shader;
	
	public ShaderChanger(PlayState state, String shader) {
		super(state);
		this.shader = Shader.valueOf(shader);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.setShaderBase(shader);
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
