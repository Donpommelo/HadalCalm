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
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Reliquary is a HubEvent that allows the player to change their starting Artifact.
 * Selecting an artifact replaces currently held artifact.
 * It also changes the current loadout, so the player will have the same loadout upon returning the hub or restarting.
 * @author Trelzubramaniam Twediculous
 */
public class Reliquary extends HubEvent {

	public Reliquary(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.RELIQUARY);
		this.lastSlot = -1;
	}

	@Override
	public void enter() {
		state.getUiHub().setType(type);
		state.getUiHub().setTitle(title);
		state.getUiHub().enter(this);
		open = true;
		addOptions(lastSearch, lastSlot, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		Array<UnlockTag> newTags = new Array<>(tags);
		if (tag != null) {
			newTags.add(tag);
		}

		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();

		for (UnlockArtifact c : UnlockArtifact.getUnlocks(state, checkUnlock, newTags)) {
			final UnlockArtifact selected = c;

			boolean appear = false;
			if ("".equals(search)) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}
			if (slots != -1) {
				if (slots != c.getArtifact().getSlotCost()) {
					appear = false;
				}
			}
			if (appear) {
				HubOption option = new HubOption(c.getName(), new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, c.getFrame()));

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (state.getPlayer().getPlayerData() == null) { return; }

						if (state.isServer()) {
							state.getPlayer().getPlayerData().addArtifact(selected, false, true);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncArtifactAddClient(selected, true));
						}
						hub.refreshHub(null);
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(UIText.ARTIFACT_INFO.text(selected.getName(),
								Integer.toString(selected.getArtifact().getSlotCost()),
								selected.getDesc(), selected.getDescLong()));
					}
				});
				hub.addActor(option, option.getWidth(), 4);
			}
		}
		hub.addActorFinish();
		hub.refreshHub(null);
	}

	@Override
	public boolean isSearchable() { return true; }

	@Override
	public boolean isTaggable() { return true; }

	@Override
	public boolean isCostable() { return true; }

	@Override
	public String[] getSearchTags() { return UIText.RELIQUARY_TAGS.text().split(","); }
}
