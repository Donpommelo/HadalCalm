package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOptionPlayer;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

/**
 * The Dormitory is a HubEvent that allows the player to change their dude.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Curjuncel Ciskabbity
 */
public class Dormitory extends HubEvent {

	private static final int TEXT_WIDTH = 240;
	private static final int TEXT_OFFSET_Y = 195;
	private static final int OPTION_WIDTH = 250;
	private static final int OPTION_HEIGHT = 500;

	private final Array<HubOptionPlayer> sprites = new Array<>();
	private AlignmentFilter lastFilter;

	public Dormitory(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.DORMITORY);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();

		if (lastFilter != state.getPlayer().getPlayerData().getLoadout().team) {
			for (HubOptionPlayer sprite : sprites) {
				sprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
			}
			sprites.clear();

			lastFilter = state.getPlayer().getPlayerData().getLoadout().team;

			for (UnlockCharacter c : UnlockCharacter.getUnlocks(state, checkUnlock, tags)) {

				final UnlockCharacter selected = c;

				HubOptionPlayer option = new HubOptionPlayer(selected.getName(), state.getPlayer(), c,
						state.getPlayer().getPlayerData().getLoadout().team);
				option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_HEIGHT);
				option.setWrap(TEXT_WIDTH);
				option.setYOffset(TEXT_OFFSET_Y);

				sprites.add(option);

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.getPlayer().getPlayerData() == null) { return; }

						if (state.isServer()) {
							state.getPlayer().getPlayerData().setCharacter(selected);
							state.getPlayer().getPlayerData().syncServerCharacterChange(selected);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncCharacterClient(selected));
						}
						state.getGsm().getLoadout().setCharacter(selected.toString());
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});
				hub.addActor(option, option.getWidth(), 1);
			}
		} else {
			for (HubOptionPlayer sprite : sprites) {
				hub.addActor(sprite, sprite.getWidth(), 1);
			}
		}
		hub.addActorFinish();
	}

	@Override
	public void dispose() {
		for (HubOptionPlayer sprite : sprites) {
			sprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
		}
	}
}
