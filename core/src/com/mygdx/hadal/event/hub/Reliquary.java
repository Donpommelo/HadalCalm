package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Reliquary extends HubEvent {

	private static final String name = "Reliquary";
	private static final String title = "SELECT ARTIFACT";

	public Reliquary(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y, title);
	
	}
	
	public void enter() {

		super.enter();
		
		for (UnlockArtifact c: UnlockArtifact.getUnlocks()) {
			
			final UnlockArtifact selected = c;

			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {
		        	state.getGsm().getLoadout().artifact = selected;
		        	
		        	state.getPlayer().getPlayerData().replaceSlot(selected);
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
