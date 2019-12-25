package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.states.PlayState;

/**
 * The Dormitory is a HubEvent that allows the player to change their dude.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Zachary Tu
 *
 */
public class Dormitory extends HubEvent {

	private static final String name = "Dormitory";
	private static final String title = "DORMITORY";

	public Dormitory(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, name, startPos, size, title, hubTypes.DORMITORY);
	}
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockCharacter c: UnlockCharacter.getUnlocks()) {
			
			final UnlockCharacter selected = c;

			Text itemChoose = new Text(selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        
				@Override
				public void clicked(InputEvent e, float x, float y) {
					if (state.isServer()) {
			        	state.getPlayer().setBodySprite(selected);
			        	state.getPlayer().getPlayerData().getLoadout().character = selected;
			        	state.getPlayer().getPlayerData().syncServerLoadoutChange();
					} else {
						state.getPlayer().getPlayerData().syncClientLoadoutChangeCharacter(selected);
					}
					state.getGsm().getRecord().setCharacter(selected.name());
		        }
		        
		        @Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(selected.getName() + ": " + selected.getDescr() + " \n \n" + selected.getDescrLong());
				}
		    });
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).height(UIHub.optionsHeight);
			hub.getTableOptions().row();
		}
		hub.getTableOptions().add(new Text("", 0, 0)).height(UIHub.optionsHeight);
		hub.getTableOptions().row();
	}
}
