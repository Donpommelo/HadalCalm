package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.states.PlayState;

/**
 * The Armory is a HubEvent that allows the player to spend Scrap on unlocks.
 * @author Zachary Tu
 *
 */
public class Quartermaster extends HubEvent {

	private static final String name = "Quartermaster";
	private static final String title = "SPEND SCRIP";

	public Quartermaster(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, name, startPos, size, title);
	}
	
	public void enter() {

		super.enter();
		
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
				itemChoose.setScale(0.50f);
				tableInner.add(itemChoose).width(optionsWidth).height(optionsHeight);
				tableInner.row();
			}
		}
		tableInner.add(new Text("", 0, 0)).width(optionsWidth).height(optionsHeight);
		tableInner.row();
	}
}
