package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * The Navigations is a HubEvent that allows the player to begin a level.
 * @author Zachary Tu
 *
 */
public class Navigations extends HubEvent {

	public Navigations(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock) {
		super(state, startPos, size, title, tag, checkUnlock, hubTypes.NAVIGATIONS);
	}
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockLevel c: UnlockLevel.getUnlocks(state, checkUnlock, tags)) {
			
			final UnlockLevel selected = c;

			Text itemChoose = new Text(selected.getInfo().getName(), 0, 0, true);
			
			itemChoose.addListener(new ClickListener() {
		        
				@Override
				public void clicked(InputEvent e, float x, float y) {
		        	state.loadLevel(selected, TransitionState.NEWLEVEL, false, "");
		        	leave();
		        }
		        
		        @Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(selected.getInfo().getName() + ": " + selected.getInfo().getDescription() + " \n \n" + selected.getInfo().getDescriptionLong());
				}
		    });
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).pad(UIHub.optionsPadding, 0, UIHub.optionsPadding, 0);
			hub.getTableOptions().row();
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight);
		hub.getTableOptions().row();
	}
}
