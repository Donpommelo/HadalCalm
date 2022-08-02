package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.server.packets.PacketsLoadout;
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
		state.getUiHub().enter(this);
		open = true;
		addOptions(lastSearch, lastSlot, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();

		for (UnlockEquip c : UnlockEquip.getUnlocks(state, checkUnlock, tags)) {
			final UnlockEquip selected = c;

			boolean appear = false;
			if ("".equals(search)) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}

			if (appear) {
				HubOption option = new HubOption(c.getName(), null);

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.getPlayer().getPlayerData() == null) { return; }

						int slotToReplace = state.getPlayer().getPlayerData().getCurrentSlot();

						//if we are picking up "nothing" in the armory, we just blank our current weapon
						if (!UnlockEquip.NOTHING.equals(selected)) {
							for (int i = 0; i < state.getPlayer().getPlayerData().getNumWeaponSlots(); i++) {
								if (state.getPlayer().getPlayerData().getMultitools()[i] instanceof NothingWeapon) {
									slotToReplace = i;
									break;
								}
							}
						}

						if (state.isServer()) {
							state.getPlayer().getPlayerData().pickup(
								Objects.requireNonNull(UnlocktoItem.getUnlock(selected, state.getPlayer())));
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncEquipClient(selected));
						}
						state.getGsm().getLoadout().setEquips(slotToReplace, selected.toString());
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc() + "\n\n" + selected.getDescLong());
					}
				});
				hub.addActor(option, option.getWidth(), 4);
			}
		}
		hub.addActorFinish();
	}

	@Override
	public boolean isSearchable() { return true; }
}
