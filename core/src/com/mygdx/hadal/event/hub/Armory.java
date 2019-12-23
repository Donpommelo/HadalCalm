package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
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

	private static final String name = "Armory";
	private static final String title = "SELECT WEAPON";

	public Armory(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, name, startPos, size, title);
	}
	
	@Override
	public void enter() {

		super.enter();
		
		for (UnlockEquip c: UnlockEquip.getUnlocks(true, UnlockTag.ARMORY)) {
			
			final UnlockEquip selected = c;
			Text itemChoose = new Text(selected.getName(), 0, 0);
			
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
		tableInner.add(new Text("", 0, 0)).width(optionsWidth).height(optionsHeight);
		tableInner.row();
	}
}
