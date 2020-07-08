package com.mygdx.hadal.event.hub;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.states.PlayState;

/**
 * The choice branch gives a list of choices, each of which connects to another event
 * @author Zachary Tu
 */
public class ChoiceBranch extends HubEvent {

	private String[] optionNames;
	private Map<String, Event> options;
	
	public ChoiceBranch(PlayState state, Vector2 startPos, Vector2 size, String title, String optionNames) {
		super(state, startPos, size, title, "MISC", true, hubTypes.MISC);
		this.optionNames = optionNames.split(",");
		options = new LinkedHashMap<String, Event>();
	}
	
	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		final Event me = this;
		
		for(Entry<String, Event> entry: options.entrySet()) {

			Text itemChoose = new Text(entry.getKey(), 0, 0, true);
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
					if (state.isServer() && entry.getValue() != null) {
						entry.getValue().getEventData().preActivate(me.getEventData(), state.getPlayer());
					}
		        }
		    });
			
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).pad(UIHub.optionsPadding, 0, UIHub.optionsPadding, 0).row();
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight).row();
	}
	
	public void addOption(String name, Event connectedEvent) {
		options.put(name, connectedEvent);
	}
	
	public String[] getOptionNames() { return optionNames; }
}
