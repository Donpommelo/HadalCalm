package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
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

	private static final String title = "RELIQUARY";

	public Reliquary(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size, title, hubTypes.RELIQUARY);
	}
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockArtifact c: UnlockArtifact.getUnlocks(true, state.getGsm().getRecord(), UnlockTag.RELIQUARY)) {
			
			final UnlockArtifact selected = c;
			Text itemChoose = new Text(selected.getInfo().getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
					if (state.isServer()) {
						state.getPlayer().getPlayerData().addArtifact(selected, false);
					} else {
						state.getPlayer().getPlayerData().syncClientLoadoutAddArtifact(selected);
					}
					hub.refreshHub();
		        }
				
				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(selected.getInfo().getName() + "\nCOST: " + selected.getArtifact().getSlotCost() + "\n" + selected.getInfo().getDescription() + " \n \n" + selected.getInfo().getDescriptionLong());
				}
		    });
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).height(UIHub.optionsHeight);
			hub.getTableOptions().row();
		}
		hub.getTableOptions().add(new Text("", 0, 0)).height(UIHub.optionsHeight);
		hub.getTableOptions().row();
		hub.refreshHub();
	}
}
