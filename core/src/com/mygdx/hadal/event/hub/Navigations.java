package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;

/**
 * The Navigations is a HubEvent that allows the player to begin a level.
 * @author Zachary Tu
 *
 */
public class Navigations extends HubEvent {

	private static final String title = "SELECT LEVEL";
	private String tag;

	public Navigations(PlayState state, int width, int height, int x, int y, String name, String tag) {
		super(state, name, width, height, x, y, title);
		this.tag = tag;
	}
	
	public void enter() {

		super.enter();
		
		for (UnlockLevel c: UnlockLevel.getUnlocks(true, UnlockTag.valueOf(tag))) {
			
			final UnlockLevel selected = c;

			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        
				@Override
				public void clicked(InputEvent e, float x, float y) {
		        	state.getGsm().setLevel(selected);
		        	state.loadLevel(selected, true);
		        }
		        
		        @Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					mouseIn(selected.getName() + ": " + selected.getDescr() + " \n \n" + selected.getDescrLong());
				}

				@Override
				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					super.enter(event, x, y, pointer, toActor);
					mouseOut();
				}
		    });
			itemChoose.setScale(0.50f);
			tableInner.add(itemChoose).width(optionsWidth).height(optionsHeight);
			tableInner.row();
		}
		tableInner.add(new Text(HadalGame.assetManager, "", 0, 0)).width(optionsWidth).height(optionsHeight);
		tableInner.row();
	}
}
