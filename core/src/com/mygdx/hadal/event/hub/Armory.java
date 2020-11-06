package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Armory is a HubEvent that allows the player to change their equipped weapons.
 * Selecting a weapon replaces currently held weapon.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Norpo Nospazog
 */
public class Armory extends HubEvent {

	public Armory(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.ARMORY);
	}


	@Override
	public void enter() {
		state.getUiHub().setType(type);
		state.getUiHub().setTitle(title);
		state.getUiHub().enter(tag, true, false, false, this);
		open = true;
		addOptions("" , 0, tag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();

		for (UnlockEquip c: UnlockEquip.getUnlocks(state, checkUnlock, tags)) {
			final UnlockEquip selected = c;

			boolean appear = false;
			if (search.equals("")) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getInfo().getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}

			if (appear) {
				Text itemChoose = new Text(selected.getInfo().getName(), 0, 0, true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						int slotToReplace = state.getPlayer().getPlayerData().getCurrentSlot();

						if (state.isServer()) {
							for (int i = 0; i < state.getPlayer().getPlayerData().getNumWeaponSlots(); i++) {
								if (state.getPlayer().getPlayerData().getMultitools()[i] instanceof NothingWeapon) {
									slotToReplace = i;
									break;
								}
							}
							state.getPlayer().getPlayerData().pickup(
								Objects.requireNonNull(UnlocktoItem.getUnlock(selected, state.getPlayer())));
						} else {
							state.getPlayer().getPlayerData().syncClientLoadoutChangeWeapon(selected);

							for (int i = 0; i < ((ClientState)state).getUiPlay().getOverrideWeaponSlots(); i++) {
								if (state.getPlayer().getPlayerData().getMultitools()[i] instanceof NothingWeapon) {
									slotToReplace = i;
									break;
								}
							}
						}
						state.getGsm().getLoadout().setEquips(slotToReplace, selected.toString());
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getInfo().getName() + "\n\n" + selected.getInfo().getDescription() + " \n \n" + selected.getInfo().getDescriptionLong());
					}
				});
				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight).row();
	}
}
