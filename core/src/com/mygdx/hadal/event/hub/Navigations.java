package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Navigations is a HubEvent that allows the player to begin a level.
 * @author Glichamp Gonjspice
 */
public class Navigations extends HubEvent {

	private final String level;

	public Navigations(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, String level, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.NAVIGATIONS);
		this.level = level;
	}

	@Override
	public void enter() {
		state.getUIManager().getUiHub().setType(type);
		state.getUIManager().getUiHub().setTitle(title);

		if (UnlockTag.NAVIGATIONS.equals(tag)) {
			state.getUIManager().getUiHub().enter(this);
		}

		open = true;
		addOptions(lastSearch, -1, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		Array<UnlockTag> newTags = new Array<>(tags);
		if (tag != null) {
			newTags.add(tag);
		}

		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUIManager().getUiHub();
		for (UnlockLevel c : UnlockLevel.getUnlocks(checkUnlock, newTags)) {
			final UnlockLevel selected = c;

			boolean appear = false;
			if (search.isEmpty()) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}

			if (appear) {
				Text itemChoose = new Text(selected.getName()).setButton(true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.isServer()) {
							state.getTransitionManager().loadLevel(selected, TransitionState.NEWLEVEL, "");
						} else if (HadalGame.usm.isHost()) {
							//client hosts request a transition to the selected level
							PacketManager.clientTCP(new Packets.ClientLevelRequest(selected, selected.getModes()[0], null));
						} else {
							//clients suggest maps when clicking
							PacketManager.clientTCP(new Packets.ClientChat(UIText.MAP_SUGGEST.text(selected.getName()),
									DialogType.SYSTEM));
						}
						leave();
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});
				itemChoose.setScale(UIHub.OPTIONS_SCALE);
				hub.getTableOptions().add(itemChoose).height(UIHub.OPTION_HEIGHT).pad(UIHub.OPTION_PAD, 0, UIHub.OPTION_PAD, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.OPTION_HEIGHT).row();

		if (!"".equals(level) && state.isServer()) {
			if (!UnlockManager.checkUnlock(UnlockType.LEVEL, level)) {
				UnlockManager.setUnlock(state, UnlockType.LEVEL, level, true);
				state.getUIManager().getDialogBox().addDialogue("", UIText.NAVIGATION_ACTIVATION.text(), "", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
		}
	}

	@Override
	public boolean isSearchable() { return true; }
}
