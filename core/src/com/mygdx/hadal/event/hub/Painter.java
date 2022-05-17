package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

/**
 * The Painter is a hub event that allows players to change their color.
 * When team mode is toggled on by the host, players with the same colors will be on the same team
 * @author Zagwump Zirmelo
 */
public class Painter extends HubEvent {

	public Painter(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.PAINTER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (AlignmentFilter c : AlignmentFilter.values()) {

			if (c.isTeam()) {

				final AlignmentFilter selected = c;
				Text itemChoose;
				if (c.equals(AlignmentFilter.NONE)) {
					itemChoose = new Text(c.toString()).setButton(true);
				} else {
					itemChoose = new Text(c.getColoredAdjective()).setButton(true);
				}

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (state.getPlayer().getPlayerData() == null) { return; }

						if (state.isServer()) {
							state.getPlayer().getPlayerData().setTeam(selected);
							state.getPlayer().getPlayerData().syncServerTeamChange(selected);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncTeamClient(selected));
						}
						state.getGsm().getLoadout().setTeam(selected.toString());
					}
				});
				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}
}
