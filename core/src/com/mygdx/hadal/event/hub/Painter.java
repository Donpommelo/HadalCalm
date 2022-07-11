package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
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
 * The Painter is a hub event that allows players to change their color.
 * When team mode is toggled on by the host, players with the same colors will be on the same team
 * @author Zagwump Zirmelo
 */
public class Painter extends HubEvent {

	private static final int TextWidth = 240;
	private static final int TexOffsetY = 195;
	private static final int OptionWidth = 250;
	private static final int OptionHeight = 500;

	private final Array<HubOptionPlayer> sprites = new Array<>();
	private UnlockCharacter lastCharacter;

	public Painter(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.PAINTER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();

		if (lastCharacter != state.getPlayer().getPlayerData().getLoadout().character) {
			for (HubOptionPlayer sprite : sprites) {
				sprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
			}
			sprites.clear();

			lastCharacter = state.getPlayer().getPlayerData().getLoadout().character;

			for (AlignmentFilter c : AlignmentFilter.values()) {

				if (c.isTeam()) {
					final AlignmentFilter selected = c;
					HubOptionPlayer option;
					if (AlignmentFilter.NONE.equals(c)) {
						option = new HubOptionPlayer(c.toString(), state.getPlayer(),
								state.getPlayer().getPlayerData().getLoadout().character, c);
					} else {
						option = new HubOptionPlayer(c.getColoredAdjective(), state.getPlayer(),
								state.getPlayer().getPlayerData().getLoadout().character, c);
					}
					option.setOptionWidth(OptionWidth).setOptionHeight(OptionHeight);
					option.setWrap(TextWidth);
					option.setYOffset(TexOffsetY);

					sprites.add(option);

					option.addListener(new ClickListener() {

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
					hub.addActor(option, option.getWidth(), 1);
				}
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
