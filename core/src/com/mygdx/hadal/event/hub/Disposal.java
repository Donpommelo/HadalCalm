package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.TextUtil;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 */
public class Disposal extends HubEvent {

	private static final float SELL_PRICE_MODIFIER = 0.5f;

	public Disposal(PlayState state, Vector2 startPos, Vector2 size, String title, String tag) {
		super(state, startPos, size, title, tag, false, true, hubTypes.DISPOSAL);
		this.lastSlot = -1;
	}

	@Override
	public void enter() {
		state.getUIManager().getUiHub().setType(type);
		state.getUIManager().getUiHub().setTitle(title);
		state.getUIManager().getUiHub().enter(this);
		open = true;
		addOptions(lastSearch, lastSlot, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		final Disposal me = this;

		Array<UnlockTag> newTags = new Array<>(tags);
		if (tag != null) {
			newTags.add(tag);
		}

		final UIHub hub = state.getUIManager().getUiHub();

		if (null != HadalGame.usm.getOwnUser()) {
			for (UnlockArtifact artifact : HadalGame.usm.getOwnUser().getLoadoutManager().getArcadeLoadout().artifacts) {

				if (artifact.equals(UnlockArtifact.NOTHING)) { continue; }

				HubOption option = new HubOption(UIText.ARTIFACT_OPTION_VENDING.text(artifact.getName(),
						Integer.toString((int) (JSONManager.artifactInfo.getPrices().get(artifact.name()) * SELL_PRICE_MODIFIER))),
						new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, artifact.getFrameBig()));

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (state.isServer()) {
							sellArtifact(state, artifact, HadalGame.usm.getOwnUser());
							me.leave();
							me.enter();
							state.getUIManager().getUiExtra().syncUIText(UITagType.CURRENCY);
						} else {
							PacketManager.clientTCP(new PacketsLoadout.SyncDisposalArtifact(artifact));
						}
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(UIText.ARTIFACT_INFO_VENDING.text(artifact.getName(),
								Integer.toString(JSONManager.artifactInfo.getPrices().get(artifact.name())),
								artifact.getDesc(), artifact.getDescLong()));
					}
				});
				hub.addActor(option, option.getWidth(), 4);

			}
		}

		hub.addActorFinish();
		hub.refreshHub(this);
	}

	public static void sellArtifact(PlayState state, UnlockArtifact selected, User user) {
		int cost = (int) (JSONManager.artifactInfo.getPrices().get(selected.name()) * SELL_PRICE_MODIFIER);

		user.getScoreManager().setCurrency(user.getScoreManager().getCurrency() + cost);

		if (null != user.getPlayer()) {
			user.getPlayer().getArtifactHelper().removeArtifact(selected, true);

			String playerName = TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH);
			state.getUIManager().getKillFeed().addNotification(UIText.DISPOSAL_SELL.text(playerName, selected.getName()), true);
		}
	}
}
