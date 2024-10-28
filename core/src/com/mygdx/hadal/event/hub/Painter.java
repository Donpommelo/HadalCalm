package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOptionPlayer;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;

/**
 * The Painter is a hub event that allows players to change their color.
 * When team mode is toggled on by the host, players with the same colors will be on the same team
 * @author Zagwump Zirmelo
 */
public class Painter extends HubEvent {

	private static final int TEXT_WIDTH = 240;
	private static final int TEXT_OFFSET_Y = 195;
	private static final int OPTION_WIDTH = 250;
	private static final int OPTION_HEIGHT = 500;

	//rate that each sprite is lazy-loaded
	private static final float loadInterval = 0.1f;
	private float loadCount;

	//A list of characters that we are loading
	private final Array<AlignmentFilter> loadingCharacters = new Array<>();

	//player sprites for hub options in the ui
	private final Array<HubOptionPlayer> sprites = new Array<>();

	//last character that was used to load each colored sprite
	private UnlockCharacter lastCharacter;

	public Painter(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.PAINTER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUIManager().getUiHub();

		Loadout loadout = HadalGame.usm.getOwnUser().getLoadoutManager().getActiveLoadout();

		//if we need to reload sprites (due to character change), clear existing sprites and begin loading new sprites
		if (lastCharacter != loadout.character) {
			FrameBufferManager.clearUnusedFrameBuffers();
			sprites.clear();

			lastCharacter = loadout.character;

			for (AlignmentFilter c : AlignmentFilter.values()) {
				if (c.isTeam()) {
					loadingCharacters.add(c);
				}
			}
		} else {
			//if reopening with no change, add existing sprites to hub
			for (HubOptionPlayer sprite : sprites) {
				hub.addActor(sprite, sprite.getWidth(), 1);
			}
		}
	}

	@Override
	public void controller(float delta) {
		super.controller(delta);

		//at set interval, create new hub option for the next to-be-loaded player
		loadCount += delta;
		if (loadCount >= loadInterval) {
			loadCount = 0.0f;

			if (!loadingCharacters.isEmpty()) {
				final UIHub hub = state.getUIManager().getUiHub();

				Player ownPlayer = HadalGame.usm.getOwnPlayer();

				if (null == ownPlayer) { return; }
				if (null == ownPlayer.getPlayerData()) { return; }

				AlignmentFilter selected = loadingCharacters.removeIndex(0);
				HubOptionPlayer option;
				if (AlignmentFilter.NONE.equals(selected)) {
					option = new HubOptionPlayer(selected.toString(), ownPlayer, lastCharacter, selected,
							false, null);
				} else {
					option = new HubOptionPlayer(selected.getColoredAdjective(), ownPlayer,	lastCharacter, selected,
							false, null);
				}
				option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_HEIGHT);
				option.setWrap(TEXT_WIDTH);
				option.setYOffset(TEXT_OFFSET_Y);

				sprites.add(option);

				//clicking on the option sets your team alignment
				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (state.isServer()) {
							ownPlayer.getCosmeticsHelper().setTeam(selected);
							ownPlayer.getCosmeticsHelper().syncServerTeamChange(selected);
						} else {
							PacketManager.clientTCP(new PacketsLoadout.SyncTeamClient(selected));
						}
						JSONManager.loadout.setTeam(HadalGame.usm.getOwnUser(), selected.toString());
					}
				});

				//if we are loading the last character, finish loading sprites
				if (hub.getType().equals(hubTypes.PAINTER)) {
					hub.addActor(option, option.getWidth(), 1);

					if (loadingCharacters.isEmpty()) {
						hub.addActorFinish();
					}
				}
			}
		}
	}
}
