package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Haberdasher extends HubEvent {

	public Haberdasher(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.HABERDASHER);
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
		final HubEvent me = this;

		if (state.getPlayer().getPlayerData() == null) {
			return;
		}

		for (UnlockCosmetic c : UnlockCosmetic.getUnlocks(state, checkUnlock, tags)) {
			final UnlockCosmetic selected = c;

			if (c.isBlank()) {
				continue;
			}
			if (c.checkCompatibleCharacters(state.getPlayer().getPlayerData().getLoadout().character)) {
				continue;
			}

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
				Text itemChoose = new Text(getCosmeticText(state.getPlayer().getPlayerData(), selected)).setButton(true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.getPlayer().getPlayerData() == null) {
							return;
						}

						UnlockCosmetic choice = selected;
						if (!isEquipping(state.getPlayer().getPlayerData(), selected)) {
							choice = getBlank(selected.getCosmeticSlot());
						}

						if (state.isServer()) {
							state.getPlayer().getPlayerData().setCosmetic(choice);
							state.getPlayer().getPlayerData().syncServerCosmeticChange(choice);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncCosmeticClient(choice));
						}
						state.getGsm().getLoadout().setCosmetic(choice.getCosmeticSlot().getSlotNumber(), choice.toString());

						me.enter();
						hub.refreshHub(me);
					}

					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});

				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}

	private static boolean isEquipping(PlayerBodyData playerData, UnlockCosmetic cosmetic) {
		return playerData.getLoadout().cosmetics[cosmetic.getCosmeticSlot().getSlotNumber()] != cosmetic;
	}

	private static String getCosmeticText(PlayerBodyData playerData, UnlockCosmetic cosmetic) {
		boolean equipping = isEquipping(playerData, cosmetic);
		if (equipping) {
			return cosmetic.getCosmeticSlot().getSlotName() + " " + cosmetic.getName() + " (EQUIP)";
		} else {
			return cosmetic.getCosmeticSlot().getSlotName() + " " + cosmetic.getName() + " (UNEQUIP)";
		}
	}

	private static UnlockCosmetic getBlank(CosmeticSlot slot) {
		return switch (slot) {
			case HAT2 -> UnlockCosmetic.NOTHING_HAT2;
			case EYE -> UnlockCosmetic.NOTHING_EYE;
			case NOSE -> UnlockCosmetic.NOTHING_NOSE;
			case MOUTH1 -> UnlockCosmetic.NOTHING_MOUTH1;
			case MOUTH2 -> UnlockCosmetic.NOTHING_MOUTH2;
			case HEAD -> UnlockCosmetic.NOTHING_HEAD;
			case DECAL_HEAD -> UnlockCosmetic.NOTHING_DECAL_HEAD;
			case DECAL_BODY -> UnlockCosmetic.NOTHING_DECAL_BODY;
			default -> UnlockCosmetic.NOTHING_HAT1;
		};
	}
}
