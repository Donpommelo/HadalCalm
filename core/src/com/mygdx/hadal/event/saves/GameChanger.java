package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * GameChangers change some aspect of the game when activated.
 * atm, this consists of lives, score and timer settings
 * 
 * Fields:
 * lives: Integer specifying how much to change the "lives" field in the ui. Optional. Default: 0
 * score: Integer specifying how much to change the "score" field in the ui. Optional. Default: 0
 * timerSet: Float specifying what to set the "timer" field to. Optional. Default: 0.0f
 * timerIncr: Float specifying how much the timer should change (usually -1, 0, 1). Optional. Default: 0.0f
 * changeTimer: boolean of whether to change the timer or not
 * 
 * @author Nonek Nehemoth
 */
public class GameChanger extends Event {

	private final int scoreIncr;
	private final float timerSet, timerIncr;
	private final boolean changeTimer;
	
	public GameChanger(PlayState state, int score, float timerSet, float timerIncr, boolean changeTimer) {
		super(state);
		this.scoreIncr = score;
		this.timerSet = timerSet;
		this.timerIncr = timerIncr;
		this.changeTimer = changeTimer;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (changeTimer) {
					state.getUiExtra().changeTimer(timerSet, timerIncr);
				}
				state.getMode().processPlayerScoreChange(state, p, scoreIncr);
			}
		};
	}
}
