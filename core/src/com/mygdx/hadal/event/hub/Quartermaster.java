package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

/**
 * The Armory is a HubEvent that allows the player to spend Scrap on unlocks.
 * @author Zachary Tu
 *
 */
public class Quartermaster extends HubEvent {

	private static final String title = "QUARTERMASTER";

	public Quartermaster(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size, title, hubTypes.QUARTERMASTER);
	}
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockEquip c: UnlockEquip.values()) {
			
			if (!c.isUnlocked()) {
				final UnlockEquip selected = c;
				Text itemChoose = new Text(selected.getName() + " Cost: " + selected.getCost(), 0, 0);
				
				itemChoose.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	if (state.getGsm().getRecord().getScrap() >= selected.getCost()) {
				        	state.getGsm().getRecord().incrementScrap(-selected.getCost());
				        	selected.setUnlocked(true);
				        	leave();
			        	}
			        }
			    });
				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionsHeight);
				hub.getTableOptions().row();
			}
		}
		hub.getTableOptions().add(new Text("", 0, 0)).height(UIHub.optionsHeight);
		hub.getTableOptions().row();
	}
}
