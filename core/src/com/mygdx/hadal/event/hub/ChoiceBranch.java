package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * The choice branch gives a list of choices, each of which connects to another event
 * @author Miwolario Mingerbread
 */
public class ChoiceBranch extends HubEvent {

	//these are the options that appear in the menu
	private final String[] optionNames;
	
	//this maps each string option to the event that will be activated when the player chooses it.
	private final OrderedMap<String, Event> options;
	
	//should the menu close after the player chooses an option
	private final boolean closeAfterSelect;
	
	public ChoiceBranch(PlayState state, Vector2 startPos, Vector2 size, String title, String optionNames, boolean closeAfterSelect, boolean closeOnLeave) {
		super(state, startPos, size, title, "MISC", true, closeOnLeave, hubTypes.MISC);
		this.optionNames = UIText.getByName(optionNames).text().split(",");
		options = new OrderedMap<>();
		this.closeAfterSelect = closeAfterSelect;
	}
	
	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUIManager().getUiHub();
		
		final ChoiceBranch me = this;
		
		for (ObjectMap.Entry<String, Event> entry : options.entries()) {
			final Event option = entry.value;
			Text itemChoose = new Text(entry.key).setButton(true);
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
					
					if (option != null) {
						option.getEventData().preActivate(me.getEventData(), HadalGame.usm.getOwnPlayer());
						if (closeAfterSelect) {
							me.leave();
						}
					}
		        }
		    });
			
			itemChoose.setScale(UIHub.OPTIONS_SCALE);
			hub.getTableOptions().add(itemChoose).height(UIHub.OPTION_HEIGHT).pad(UIHub.OPTION_PAD, 0, UIHub.OPTION_PAD, 0).row();
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.OPTION_HEIGHT).row();
	}
	
	public void addOption(String name, Event connectedEvent) {
		options.put(name, connectedEvent);
	}
	
	public String[] getOptionNames() { return optionNames; }
}
