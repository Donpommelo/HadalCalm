package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.server.AlignmentFilter;
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
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (AlignmentFilter c: AlignmentFilter.values()) {

			if (c.isTeam()) {

				final AlignmentFilter selected = c;
				Text itemChoose = new Text(c.toString(), 0, 0, true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (state.isServer()) {
							state.getPlayer().setBodySprite(null, selected);
							state.getPlayer().getPlayerData().getLoadout().team = selected;
							state.getPlayer().getPlayerData().syncServerLoadoutChange();
						} else {
							state.getPlayer().getPlayerData().syncClientLoadoutChangeTeam(selected);
						}
						state.getGsm().getLoadout().setTeam(selected.toString());
					}
				});
				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).pad(UIHub.optionsPadding, 0, UIHub.optionsPadding, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight).row();
	}
}
