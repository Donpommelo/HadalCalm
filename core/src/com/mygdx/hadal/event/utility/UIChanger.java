package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.actors.UILevel.uiType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A UIChanger changes the UI. specifically, the UILevel (name tentative) actor to display different information or change 
 * some extra, non-score field like lives.
 * @author Zachary Tu
 *
 */
public class UIChanger extends Event {

	private static final String name = "UI Changer";

	private uiType type;
	private int scoreIncr, extraVarIncr;
	
	public UIChanger(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, uiType type, int extraVarIncr, int scoreIncr) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.type = type;
		this.extraVarIncr = extraVarIncr;
		this.scoreIncr = scoreIncr;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (type != null) {
					state.getUiLevel().setType(type);

				}
				state.getUiLevel().incrementExtraVar(extraVarIncr);
				state.getUiLevel().incrementScore(scoreIncr);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) 0, (short) 0, true, eventData);
	}
}
