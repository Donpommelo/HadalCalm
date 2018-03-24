package com.mygdx.hadal.event.utility;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A UIChanger changes the UI. specifically, the UILevel (name tentative) actor to display different information or change 
 * some extra, non-score field like lives.
 * @author Zachary Tu
 *
 */
public class UIChanger extends Event {

	private static final String name = "UI Changer";

	private ArrayList<UITag> tags;
	private int changeType, scoreIncr, extraVarIncr;
	private float timerIncr;
	private String miscTag;
	
	public UIChanger(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String types, int changeType, int extraVarIncr, int scoreIncr, float timerIncr, String misc) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.changeType = changeType;
		this.extraVarIncr = extraVarIncr;
		this.scoreIncr = scoreIncr;
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
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				state.getUiLevel().changeTypes(changeType, tags);
				state.getUiLevel().incrementLives(extraVarIncr);
				state.getUiLevel().incrementScore(scoreIncr);
				state.getUiLevel().incrementTimer(timerIncr);
			}
		};
	}
}
