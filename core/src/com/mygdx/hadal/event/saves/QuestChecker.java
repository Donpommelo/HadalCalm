package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A QuestChecker event. This checks a quest flag in the game's save file, to potentially chain to another event.
 * 
 * Triggered Behavior: When triggered, this event checks a quest flag. If the quest progress matches, chain to...
 * Triggering Behavior: If the quest is at the specified point, the next event is activated,
 *
 * Fields:
 * 
 * quest: String id of quest checked
 * newVal: Value of the quest to check for. If equal, chain to connected event. Optional. Default: 0
 * 
 * @author Zachary Tu
 *
 */
public class QuestChecker extends Event {

	private String quest;
	private int val;
	
	public QuestChecker(PlayState state, String quest, int val) {
		super(state);
		this.quest = quest;
		this.val = val;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (state.getGsm().getRecord().getFlags().get(quest) == val && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(this, p);
				}
			}
		};
	}
}
