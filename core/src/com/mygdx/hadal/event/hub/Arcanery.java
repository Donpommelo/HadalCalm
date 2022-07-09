package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Dispensary is a HubEvent that allows the player to change their active item.
 * Selecting an item replaces currently held active item.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Gratticini Grinkmeyer
 */
public class Arcanery extends HubEvent {

	public Arcanery(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.DISPENSARY);
	}

	@Override
	public void enter() {
		state.getUiHub().setType(type);
		state.getUiHub().setTitle(title);
		state.getUiHub().enter(true, false, false, this);
		open = true;
		addOptions(lastSearch, lastSlot, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockManager.UnlockTag tag) {
		super.addOptions(search, slots, tag);
		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();

		for (UnlockActives c : UnlockActives.getUnlocks(state, checkUnlock, tags)) {
			final UnlockActives selected = c;

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
				Text itemChoose = new Text(selected.getName()).setButton(true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.getPlayer().getPlayerData() == null) { return; }

						if (state.isServer()) {
							state.getPlayer().getPlayerData().pickup(
								Objects.requireNonNull(UnlocktoItem.getUnlock(selected, state.getPlayer())));
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncActiveClient(selected));
						}
						state.getGsm().getLoadout().setActive(selected.toString());
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc() + "\n\n" + selected.getDescLong());
					}
				});

				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}
}
