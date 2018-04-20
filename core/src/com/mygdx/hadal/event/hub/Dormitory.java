package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.states.PlayState;

public class Dormitory extends HubEvent {

	private static final String name = "Dormitory";
	private static final String title = "SELECT CHARACTER";

	public Dormitory(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y, title);
	
	}
	
	public void enter() {

		super.enter();
		
		for (UnlockCharacter c: UnlockCharacter.getUnlocks()) {
			
			final UnlockCharacter selected = c;

			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        
				@Override
				public void clicked(InputEvent e, float x, float y) {
		        	state.getGsm().getLoadout().character = selected;
		        	state.getPlayer().setBodySprite(selected.getSprite());
		        }
		        
		        @Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					mouseIn(selected.getName() + ": " + selected.getDescr());
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
