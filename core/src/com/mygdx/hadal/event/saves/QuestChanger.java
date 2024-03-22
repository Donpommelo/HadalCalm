package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A QuestChanger event. This modifies a quest flag in the game's save file.
 * <p>
 * Triggered Behavior: When triggered, this event updates a quest flag.
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * <p>
 * quest: String id of quest changed.
 * newVal: new value for the quest. Optional. Default: 0
 * 
 * @author Vluitcake Veckheart
 */
public class QuestChanger extends Event {

	private final String quest;
	private final int newVal;
	
	public QuestChanger(PlayState state, String quest, int newVal) {
		super(state);
		this.quest = quest;
		this.newVal = newVal;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				JSONManager.record.getFlags().put(quest, newVal);
				JSONManager.record.saveRecord();
			}
		};
	}
}
