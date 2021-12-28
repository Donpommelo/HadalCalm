package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

/**
 * The Dormitory is a HubEvent that allows the player to change their dude.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Curjuncel Ciskabbity
 */
public class Dormitory extends HubEvent {

	public Dormitory(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.DORMITORY);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (UnlockCharacter c: UnlockCharacter.getUnlocks(state, checkUnlock, tags)) {
			
			final UnlockCharacter selected = c;

			Text itemChoose = new Text(selected.getInfo().getName()).setButton(true);
			
			itemChoose.addListener(new ClickListener() {
		        
				@Override
				public void clicked(InputEvent e, float x, float y) {

					if (state.getPlayer().getPlayerData() == null) { return; }

					if (state.isServer()) {
			        	state.getPlayer().setBodySprite(selected, null);
			        	state.getPlayer().getPlayerData().getLoadout().character = selected;
			        	state.getPlayer().getPlayerData().syncServerCharacterChange(selected);
					} else {
						HadalGame.client.sendTCP(new PacketsLoadout.SyncCharacterClient(selected));
					}
					state.getGsm().getLoadout().setCharacter(selected.toString());
		        }
		        
		        @Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(selected.getInfo().getName() + ": " + selected.getInfo().getDescription() + "\n\n" + selected.getInfo().getDescriptionLong());
				}
		    });
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}
}
