package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.SavedLoadout;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class Outfitter extends HubEvent {

	public Outfitter(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.OUTFITTER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();

		for (ObjectMap.Entry<String, SavedLoadout> c : state.getGsm().getSavedOutfits().getOutfits()) {
			final SavedLoadout selected = c.value;
			Text itemChoose = new Text(c.key).setButton(true);

			itemChoose.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					if (state.getPlayer().getPlayerData() == null) { return; }

					//selecting outfit equips its weapons/artifacts/active item
					if (state.isServer()) {
						state.getPlayer().getPlayerData().syncLoadout(new Loadout(selected), false, false);
						state.getGsm().getLoadout().setLoadout(selected);
						state.getPlayer().getPlayerData().syncServerWholeLoadoutChange();
					} else {
						HadalGame.client.sendTCP(new PacketsLoadout.SyncWholeLoadout(state.getPlayer().getConnId(), new Loadout(selected), false));
					}
				}

				private final StringBuilder text = new StringBuilder();
				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);

					//description contains outfit weapons, active item and artifact
					text.setLength(0);
					text.append(c.key).append("\n\n");
					for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
						if (!selected.getEquips()[i].equals(UnlockEquip.NOTHING.toString())) {
							text.append(selected.getEquips()[i]).append("\n");
						}
					}
					text.append("\n");
					text.append(selected.getActive()).append("\n\n");
					for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
						if (!selected.getArtifacts()[i].equals(UnlockArtifact.NOTHING.toString())) {
							text.append(selected.getArtifacts()[i]).append("\n");
						}
					}
					hub.setInfo(text.toString());
				}
			});
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).colspan(2).row();
		hub.refreshHub(this);
	}
}