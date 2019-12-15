package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;

/**
 * The Reliquary is a HubEvent that allows the player to change their starting Artifact.
 * Selecting an artifact replaces currently held artifact.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Zachary Tu
 *
 */
public class Reliquary extends HubEvent {

	private static final String name = "Reliquary";
	private static final String title = "SELECT ARTIFACT";

	public Reliquary(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y, title);
	
	}
	
	public void enter() {

		super.enter();
		
		for (UnlockArtifact c: UnlockArtifact.getUnlocks(true, UnlockTag.RELIQUARY)) {
			
			final UnlockArtifact selected = c;
			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
		        	state.getPlayer().getPlayerData().replaceStartingArtifact(selected);
		        	state.getGsm().getRecord().setArtifact(selected.name());
		        	state.getPlayer().getPlayerData().syncClientLoadoutChange();
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
