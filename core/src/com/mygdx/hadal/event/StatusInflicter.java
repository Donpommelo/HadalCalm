package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

/**
 * A Status inflicter inflicts a stat change status on the player that activates it.
 * <p>
 * Triggered Behavior: When triggered, inflict a status to the activating player
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * stat: int constant of the stat to modify. Look up each stat in the stats file in utils. Default: 0 (max Hp)
 * amount: float amount to modify the stat by. default: 0.0f
 * duration: duration of the status. If 0, the status is permanent (until leaving the stage) default: 0.0f
 * 
 * @author Nurfield Naddam
 */
public class StatusInflicter extends Event {

	private final int stat;
	private final float amount, duration;
	
	public StatusInflicter(PlayState state, int stat, float amount, float duration) {
		super(state);
		this.stat = stat;
		this.amount = amount;
		this.duration = duration;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (p != null) {
					if (duration != 0.0f) {
						p.getPlayerData().addStatus(new StatChangeStatus(state, duration, stat, amount, p.getPlayerData(), p.getPlayerData()));
					} else {
						p.getPlayerData().addStatus(new StatChangeStatus(state, stat, amount, p.getPlayerData()));
					}
				}
			}
		};
	}
}
