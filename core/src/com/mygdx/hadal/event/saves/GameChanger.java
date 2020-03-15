package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * GameChangers change some aspect of the game when activated.
 * atm, this consists of lives, score and timer settings
 * 
 * Fields:
 * lives: Integer specifying how much to change the "lives" field in the ui. Optional. Default: 0
 * score: Integer specifying how much to change the "score" field in the ui. Optional. Default: 0
 * timer: Float specifying how much to change the "timer" field in the ui. Optional. Default: 0.0f
 * timerIncr: Float specifying how much the timer should change (usually -1, 0, 1). Optional. Default: 0.0f
 * 
 * @author Zachary Tu
 *
 */
public class GameChanger extends Event {

	private int scoreIncr, livesIncr;
	private float timerSet, timerIncr;
	private boolean changeTimer;
	
	public GameChanger(PlayState state, int lives, int score, float timerSet, float timerIncr, boolean changeTimer) {
		super(state);
		this.livesIncr = lives;
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
				state.getUiExtra().changeFields(p, scoreIncr, livesIncr, timerSet, timerIncr, changeTimer);
			}
		};
	}
}
