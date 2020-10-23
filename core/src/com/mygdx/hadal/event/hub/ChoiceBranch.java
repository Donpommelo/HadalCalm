package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.states.PlayState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The choice branch gives a list of choices, each of which connects to another event
 * @author Miwolario Mingerbread
 */
public class ChoiceBranch extends HubEvent {

	//these are the options that appear in the menu
	private final String[] optionNames;
	
	//this maps each string option to the event that will be activated when the player chooses it.
	private final Map<String, Event> options;
	
	//should the menu close after the player chooses an option
	private final boolean closeAfterSelect;
	
	public ChoiceBranch(PlayState state, Vector2 startPos, Vector2 size, String title, String optionNames, boolean closeAfterSelect, boolean closeOnLeave) {
		super(state, startPos, size, title, "MISC", true, closeOnLeave, hubTypes.MISC);
		this.optionNames = optionNames.split(",");
		options = new LinkedHashMap<>();
		this.closeAfterSelect = closeAfterSelect;
	}
	
	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		final ChoiceBranch me = this;
		
		for (Entry<String, Event> entry: options.entrySet()) {

			Text itemChoose = new Text(entry.getKey(), 0, 0, true);
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
					
					if (entry.getValue() != null) {
						entry.getValue().getEventData().preActivate(me.getEventData(), state.getPlayer());
						
						if (closeAfterSelect) {
							me.leave();
						}
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
