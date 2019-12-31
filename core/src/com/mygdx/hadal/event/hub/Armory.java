package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * The Armory is a HubEvent that allows the player to change their equiped weapons.
 * Selecting a weapon replaces currently held weapon.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Zachary Tu
 *
 */
public class Armory extends HubEvent {

	private static final String title = "ARMORY";

	public Armory(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size, title, hubTypes.ARMORY);
	}
	
	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockEquip c: UnlockEquip.getUnlocks(state, true, UnlockTag.ARMORY)) {
			
			final UnlockEquip selected = c;
			Text itemChoose = new Text(selected.getInfo().getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
				
				@Override
		        public void clicked(InputEvent e, float x, float y) {
					if (state.isServer()) {
						state.getPlayer().getPlayerData().pickup(UnlocktoItem.getUnlock(selected, state.getPlayer()));
					} else {
			        	state.getPlayer().getPlayerData().syncClientLoadoutChangeWeapon(selected);
					}
					state.getGsm().getRecord().setEquips(state.getPlayer().getPlayerData().getCurrentSlot(), selected.name());
		        }
				
				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(selected.getInfo().getName() + ": " + selected.getInfo().getDescription() + " \n \n" + selected.getInfo().getDescriptionLong());
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
