package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Navigations extends HubEvent {

	private static final String name = "Navigations";
	private static final String title = "SELECT LEVEL";

	public Navigations(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y, title);
	
	}
	
	public void enter() {

		super.enter();
		
		for (UnlockLevel c: UnlockLevel.getUnlocks(true, UnlockTag.NAVIGATIONS)) {
			
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
					mouseIn(selected.getName() + ": " + selected.getDescr());
				}

				@Override
				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					super.enter(event, x, y, pointer, toActor);
					mouseOut();
				}
		    });
			itemChoose.setScale(0.75f);
			tableInner.add(itemChoose).width(optionsWidth).height(optionsHeight);
			tableInner.row();
		}
		tableInner.add(new Text(HadalGame.assetManager, "", 0, 0)).width(optionsWidth).height(optionsHeight);
		tableInner.row();
	}
}
