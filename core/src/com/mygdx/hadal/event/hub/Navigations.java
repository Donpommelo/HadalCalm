package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

import java.util.ArrayList;
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
		state.getUiHub().setType(type);
		state.getUiHub().setTitle(title);

		if (tag.equals(UnlockTag.NAVIGATIONS)) {
			state.getUiHub().enter(tag, true, false, false,this);
		} else if (tag.equals(UnlockTag.SINGLEPLAYER)) {
			state.getUiHub().enter(tag, true, true, false,
				this, "ALL", "ARENA", "BOSS", "SANDBOX", "BIRD");
		} else {
			state.getUiHub().enter(tag, true, true, false,
				this, "CURATED", "ALL", "PVP", "ARENA", "BOSS", "SANDBOX", "BIRD");
		}

		open = true;
		addOptions(lastSearch, -1, lastTag);
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		ArrayList<UnlockTag> newTags = new ArrayList<>(tags);
		if (tag != null) {
			newTags.add(tag);
		}

		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();
		final Navigations me = this;

		for (UnlockLevel c: UnlockLevel.getUnlocks(state, checkUnlock, newTags)) {
			final UnlockLevel selected = c;

			boolean appear = false;
			if (search.equals("")) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getInfo().getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}

			if (appear || selected.equals(UnlockLevel.RANDOM)) {
				Text itemChoose = new Text(selected.getInfo().getName(), 0, 0, true);

				itemChoose.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.isServer()) {
							//select a random valid map if selecting the random option
							if (selected.equals(UnlockLevel.RANDOM)) {
								state.loadLevel(UnlockLevel.getRandomMap(state, newTags), TransitionState.NEWLEVEL, "");
							} else {
								state.loadLevel(selected, TransitionState.NEWLEVEL, "");
							}

							//play a particle when the player uses this event
							new ParticleEntity(state, me, Particle.TELEPORT, 0.0f, 3.0f, true, particleSyncType.CREATESYNC, new Vector2(0, - me.getSize().y / 2));
						} else {

							//clients suggest maps when clicking
							HadalGame.client.sendTCP(new Packets.ClientChat("Suggests Map: " + selected.name(), DialogType.SYSTEM));
						}
						leave();
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getInfo().getName() + ": " + selected.getInfo().getDescription() + " \n \n" + selected.getInfo().getDescriptionLong());
					}
				});
				itemChoose.setScale(UIHub.optionsScale);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight).row();

		if (!level.equals("") && state.isServer()) {
			if (!UnlockManager.checkUnlock(state, UnlockType.LEVEL, level)) {
				UnlockManager.setUnlock(state, UnlockType.LEVEL, level, true);
				state.getDialogBox().addDialogue("", "TELEPYRAMID ACTIVATED", "", true, true, true, 3.0f, null, null, DialogType.SYSTEM);
			}
		}
	}
}
