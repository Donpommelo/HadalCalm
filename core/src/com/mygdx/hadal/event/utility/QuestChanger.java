package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * A QuestChanger TBA
 * 
 * @author Zachary Tu
 *
 */
public class QuestChanger extends Event {

	private static final String name = "Quest Changer";
	
	private String quest;
	private int newVal;
	
	public QuestChanger(PlayState state, String quest, int newVal) {
		super(state, name);
		this.quest = quest;
		this.newVal = newVal;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				state.getGsm().getRecord().getFlags().put(quest, newVal);
				state.getGsm().getRecord().saveRecord();
			}
		};
	}
}
