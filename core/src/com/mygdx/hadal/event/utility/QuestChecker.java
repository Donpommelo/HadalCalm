package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * A QuestChecker TBA
 * 
 * @author Zachary Tu
 *
 */
public class QuestChecker extends Event {

	private static final String name = "Quest Checker";
	
	private String quest;
	private int val;
	
	public QuestChecker(PlayState state, String quest, int val) {
		super(state, name);
		this.quest = quest;
		this.val = val;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {				
				if (state.getGsm().getRecord().getFlags().get(quest) == val && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().onActivate(this);
				}
			}
		};
	}
}
