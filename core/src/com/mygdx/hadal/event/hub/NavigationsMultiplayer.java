package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.ModeSetting;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The NavigationsMultiplayer is a HubEvent that allows the player to select a mode and then a corresponding map
 * @author Plonnigan Pludardus
 */
public class NavigationsMultiplayer extends HubEvent {

	private static final int TEXT_WIDTH = 395;
	private static final int TEXT_OFFSET_Y = 250;
	private static final float TEXT_SCALE = 0.5f;
	private static final int OPTION_WIDTH = 405;
	private static final int OPTION_HEIGHT = 540;
	private static final int ICON_WIDTH = 405;
	private static final int ICON_HEIGHT = 540;

	private static final int MAP_TEXT_WIDTH = 340;
	private static final int MAP_TEXT_OFFSET_Y = 100;
	private static final int MAP_OPTION_WIDTH = 350;
	private static final int MAP_OPTION_HEIGHT = 260;
	private static final int MAP_ICON_WIDTH = 300;
	private static final int MAP_ICON_HEIGHT = 200;
	private static final int MAP_ICON_OFFSET_Y = 5;
	public static final float TAB_SCALE = 0.45f;
	public static final float TAB_PAD = 80.0f;
	public static final float TAB_HEIGHT = 50.0f;

	//this is the selected game mode
	private GameMode modeChosen = GameMode.DEATHMATCH;

	private final Array<GameMode> gameModes = new Array<>();

	private String lastSearch = "";
	private UnlockTag lastTag;

	private int menuDepth;

	public NavigationsMultiplayer(PlayState state, Vector2 startPos, Vector2 size, String title, String tag,
								  boolean closeOnLeave, String modes) {
		super(state, startPos, size, title, tag, false, closeOnLeave, hubTypes.NAVIGATIONS);
		if ("".equals(modes)) {
			gameModes.addAll(GameMode.values());
		} else {
			for (String s : modes.split(",")) {
				gameModes.add(GameMode.getByName(s));
			}
		}
	}

	@Override
	public void addOptions(String search, int slots, UnlockTag tag) {
		super.addOptions(search, slots, tag);
		state.getUiHub().setTitle(modeChosen.getName());
		addTabs();

		lastSearch = search;
		lastTag = tag;

		addSettings();
		addModifiers();
		addMaps();
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		hub.getTableTabs().clear();
		hub.setTitle(UIText.GAME_MODES.text());
		final NavigationsMultiplayer me = this;
		menuDepth = 0;

		//bring up all game modes that can be selected from the hub
		for (GameMode c : gameModes) {
			if (!c.isInvisibleInHub()) {
				final GameMode selected = c;

				HubOption option = new HubOption(c.getName(), new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, c.getFrame()));
				option.setScale(TEXT_SCALE);
				option.setColor(Color.BLACK);
				option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_HEIGHT);
				option.setIconWidth(ICON_WIDTH).setIconHeight(ICON_HEIGHT);
				option.setWrap(TEXT_WIDTH);
				option.setYOffset(TEXT_OFFSET_Y);

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						modeChosen = selected;

						state.getUiHub().setType(type);
						state.getUiHub().enter(me);
						addOptions(lastSearch, -1, lastTag);
						menuDepth = 1;
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});
				hub.addActor(option, option.getWidth(), 1);
			}
		}
		hub.addActorFinish();
	}

	private void addTabs() {
		final UIHub hub = state.getUiHub();
		hub.getTableTabs().clear();

		Text map = new Text(UIText.TAB_MAPS.text()).setButton(true);
		map.setScale(TAB_SCALE);
		map.addListener(new ClickListener() {

		   @Override
		   public void clicked(InputEvent e, float x, float y) {
		   		addMaps();
		   		saveSettings();
		   }
	   });

		Text settings = new Text(UIText.TAB_SETTINGS.text()).setButton(true);
		settings.setScale(TAB_SCALE);
		settings.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				addSettings();
				saveSettings();
			}
		});

		Text modifiers = new Text(UIText.TAB_MODIFIERS.text()).setButton(true);
		modifiers.setScale(TAB_SCALE);
		modifiers.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				addModifiers();
				saveSettings();
			}
		});

		hub.getTableTabs().add(map).height(TAB_HEIGHT).pad(TAB_PAD);
		hub.getTableTabs().add(settings).height(TAB_HEIGHT).pad(TAB_PAD);
		hub.getTableTabs().add(modifiers).height(TAB_HEIGHT).pad(TAB_PAD);
	}

	private void addMaps() {
		final UIHub hub = state.getUiHub();
		final NavigationsMultiplayer me = this;
		hub.getTableOptions().clear();

		Array<UnlockTag> newTags = new Array<>(tags);
		if (lastTag != null) {
			newTags.add(lastTag);
		}

		Pattern pattern = Pattern.compile(lastSearch);

		for (UnlockLevel c : UnlockLevel.getUnlocks(state, checkUnlock, newTags)) {
			final UnlockLevel selected = c;

			boolean appear = false;
			if ("".equals(lastSearch)) {
				appear = true;
			} else {
				Matcher matcher = pattern.matcher(selected.getName().toLowerCase());
				if (matcher.find()) {
					appear = true;
				}
			}

			//iterate through the modes that this map can be played with to see if we add it to the options
			boolean modeCompliant = false;
			for (int i = 0; i < selected.getModes().length; i++) {
				if (selected.getModes()[i] == modeChosen.getCheckCompliance()) {
					modeCompliant = true;
					break;
				}
			}

			if (appear && modeCompliant) {
				Animation<TextureRegion> sprite = null;
				if (selected.getIcon() != null) {
					sprite = new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, selected.getIcon());
				}
				HubOption option = new HubOption(selected.getName(), sprite);
				option.setOptionWidth(MAP_OPTION_WIDTH).setOptionHeight(MAP_OPTION_HEIGHT);
				option.setIconWidth(MAP_ICON_WIDTH).setIconHeight(MAP_ICON_HEIGHT).setIconOffsetY(MAP_ICON_OFFSET_Y);
				option.setWrap(MAP_TEXT_WIDTH);
				option.setYOffset(MAP_TEXT_OFFSET_Y);

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.isServer()) {
							state.loadLevel(selected, modeChosen, PlayState.TransitionState.NEWLEVEL, "");
							//play a particle when the player uses this event
							new ParticleEntity(state, me, Particle.TELEPORT, 0.0f, 3.0f, true, SyncType.CREATESYNC)
									.setOffset(0, - me.getSize().y / 2);
						} else {

							//clients suggest maps when clicking
							HadalGame.client.sendTCP(new Packets.ClientChat(UIText.MAP_SUGGEST.text(selected.getName()),
									DialogBox.DialogType.SYSTEM));
						}
						leave();
					}

					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});
				hub.addActor(option, option.getWidth(), 2);
			}
		}
		hub.addActorFinish();
	}

	@Override
	public void back() {
		super.back();
		if (menuDepth > 0) {
			enter();
		}
	}

	private void addSettings() {
		final UIHub hub = state.getUiHub();
		hub.getTableOptions().clear();

		for (ModeSetting setting : modeChosen.getSettings()) {
			setting.setSetting(state, modeChosen, hub.getTableOptions());
		}
	}

	private void addModifiers() {
		final UIHub hub = state.getUiHub();
		hub.getTableOptions().clear();

		for (ModeSetting setting : modeChosen.getSettings()) {
			setting.setModifiers(state, modeChosen, hub.getTableOptions());
		}
	}

	private void saveSettings() {
		for (ModeSetting setting : modeChosen.getSettings()) {
			setting.saveSetting(state, modeChosen);
		}
	}

	@Override
	public boolean isSearchable() { return true; }

	@Override
	public boolean isTabbable() { return true; }
}
