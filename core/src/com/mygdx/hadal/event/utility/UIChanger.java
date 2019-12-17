package com.mygdx.hadal.event.utility;

import java.util.ArrayList;

import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A UIChanger changes the UI. specifically, the UILevel (name tentative) actor to display different information or change 
 * some extra, non-score field like lives.
 * 
 * Triggered Behavior: When triggered, this event will change the text that appears in the upper right-hand side of the screen.
 * Triggering Behavior: N/A
 * 
 * Fields:
 * tags: This string specifies the uiType enums that will be used for the ui change. This is a comma-separated list of enum names.
 * change: this is an integer that specifies what change should be made to the ui. Optional. Default: 0
 * 	-1: remove these tags
 * 	0: set these tags
 * 	1: add these tags 
 * lives: Integer specifying how much to change the "lives" field in the ui. Optional. Default: 0
 * score: Integer specifying how much to change the "score" field in the ui. Optional. Default: 0
 * timer: Float specifying how much to change the "timer" field in the ui. Optional. Default: 0.0f
 * misc: String describing the misc tag to be changed in the ui. You can only change 1 MISC tag at once because of this.
 * 	To remove a MISC tag, you must provide its exact misc text as an input of this field. 
 * 	Also, if you want a newline after the text, you'll have to insert it yourself.
 * 
 * @author Zachary Tu
 *
 */
public class UIChanger extends Event {

	private static final String name = "UI Changer";

	private ArrayList<UITag> tags;
	private int changeType, scoreIncr, livesIncr, var1Incr, var2Incr;
	private float timerIncr;
	private String miscTag;
	
	public UIChanger(PlayState state, String types, int changeType, int livesIncr, int scoreIncr, int var1Incr, int var2Incr, float timerIncr, String misc) {
		super(state, name);
		this.changeType = changeType;
		this.livesIncr = livesIncr;
		this.scoreIncr = scoreIncr;
		this.var1Incr = var1Incr;
		this.var2Incr = var2Incr;
		this.timerIncr = timerIncr;
		this.miscTag = misc;

		this.tags = new ArrayList<UITag>();
		if (types != null) {
			for (String type : types.split(",")) {
				uiType newType = uiType.valueOf(type);
				
				UITag newTag = new UITag(newType);
				
				if (newType.equals(uiType.MISC)) {
					newTag.setMisc(miscTag);
				}
				
				this.tags.add(newTag);
			}
		}
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.getUiExtra().changeTypes(changeType, tags);
				state.getUiExtra().incrementLives(livesIncr);
				state.getUiExtra().incrementScore(scoreIncr);
				state.getUiExtra().incrementTimer(timerIncr);
				state.getUiExtra().incrementVar1(var1Incr);
				state.getUiExtra().incrementVar2(var2Incr);
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(2);
	}
}
