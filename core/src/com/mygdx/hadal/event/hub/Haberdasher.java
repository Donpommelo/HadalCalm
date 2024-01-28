package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.HubOptionPlayer;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Painter is a hub event that allows players to change their color.
 * When team mode is toggled on by the host, players with the same colors will be on the same team
 * @author Shonnigan Swilbatross
 */
public class Haberdasher extends HubEvent {

	private static final float iconScale = 0.4f;
	private static final int TEXT_WIDTH = 240;
	private static final int TEXT_OFFSET_Y = 45;
	private static final int OPTION_WIDTH = 250;
	private static final int OPTION_HEIGHT = 270;

	private static final int TEXT_OFFSET_CHARACTER_Y = 195;
	private static final int OPTION_CHARACTER_HEIGHT = 500;

	//rate that each sprite is lazy-loaded
	private static final float loadInterval = 0.1f;
	private float loadCount;

	//A list of cosmetics that we are loading
	private final Array<UnlockCosmetic> loadingCosmetics = new Array<>();

	//player sprites for hub options in the ui
	private final Array<HubOptionPlayer> sprites = new Array<>();

	//last team color/character/cosmetic slot that was used to load each colored sprite
	private AlignmentFilter lastFilter;
	private UnlockCharacter lastCharacter;
	private CosmeticSlot lastCosmetic;

	//this is the selected game mode
	private CosmeticSlot slotChosen = CosmeticSlot.HAT1;

	//keeps track of whether we are looking at cosmetic slots or cosmetics
	private int menuDepth;

	public Haberdasher(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.HABERDASHER);
	}

	@Override
	public void addOptions(String search, int slots, UnlockManager.UnlockTag tag) {
		super.addOptions(search, slots, tag);
		state.getUiHub().setTitle(slotChosen.getSlotName());

		Pattern pattern = Pattern.compile(search);

		Loadout loadout = HadalGame.usm.getOwnUser().getLoadoutManager().getActiveLoadout();

		//if we need to reload sprites (due to character/team/slot change), clear existing sprites and begin loading new sprites
		if (lastCharacter != loadout.character || lastFilter != loadout.team || lastCosmetic != slotChosen) {
			FrameBufferManager.clearUnusedFrameBuffers();

			sprites.clear();

			lastCharacter = loadout.character;
			lastFilter = loadout.team;
			lastCosmetic = slotChosen;

			//iterate through all cosmetics that are valid with given character and slot
			for (UnlockCosmetic c : UnlockCosmetic.getUnlocks(state, checkUnlock, tags)) {
				if (!c.getCosmeticSlot().equals(slotChosen)) {
					continue;
				}
				if (c.checkCompatibleCharacters(lastCharacter) && !c.isBlank()) {
					continue;
				}
				boolean appear = false;
				if ("".equals(search)) {
					appear = true;
				} else {
					Matcher matcher = pattern.matcher(c.getName().toLowerCase());
					if (matcher.find()) {
						appear = true;
					}
				}
				if (appear) {
					loadingCosmetics.add(c);
				}
			}
		} else {
			//if reopening with no change, add existing sprites to hub
			for (HubOptionPlayer sprite : sprites) {
				state.getUiHub().addActor(sprite, sprite.getWidth(), 1);
			}
		}
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		hub.setTitle(UIText.COSMETIC_SLOTS.text());
		final Haberdasher me = this;
		menuDepth = 0;

		Loadout loadout = HadalGame.usm.getOwnUser().getLoadoutManager().getActiveLoadout();

		//create options for each cosmetic slot that has at least one cosmetic for the character
		for (CosmeticSlot slot : CosmeticSlot.values()) {
			final CosmeticSlot selected = slot;

			Animation<TextureRegion> frame = null;

			//hub option icon displays currently equipped cosmetic or a red X if there is none.
			UnlockCosmetic cosmeticType = loadout.cosmetics[slot.getSlotNumber()];
			if (!cosmeticType.isBlank()) {
				UnlockCharacter character = loadout.character;
				CharacterCosmetic cosmetic = cosmeticType.getCosmetics().get(character);
				if (cosmetic != null) {
					frame = cosmetic.getShadedFrames(state.getBatch(), loadout.team, character);
				}
			}

			if (frame == null) {
				frame = new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, Sprite.EMOTE_NO.getFrames());
			}

			float iconWidth = frame.getKeyFrame(0).getRegionWidth();
			float iconHeight = frame.getKeyFrame(0).getRegionHeight();

			//keep track of the number of cosmetics for each slot to be displayed in the respective hub option
			int cosmeticCount = 0;
			for (UnlockCosmetic c : UnlockCosmetic.getUnlocks(state, checkUnlock, tags)) {
				if (!c.getCosmeticSlot().equals(slot)) {
					continue;
				}
				if (c.checkCompatibleCharacters(loadout.character) && !c.isBlank()) {
					continue;
				}
				cosmeticCount++;
			}

			//don't display slots with no items. Subtract 1 to account for blank option
			cosmeticCount--;
			if (cosmeticCount == 0) { continue; }

			HubOption option = new HubOption(slot.getSlotName() + " (" + cosmeticCount + ")", frame);
			option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_HEIGHT);
			option.setIconWidth((int) (iconWidth * iconScale)).setIconHeight((int) (iconHeight * iconScale));
			option.setWrap(TEXT_WIDTH);
			option.setYOffset(TEXT_OFFSET_Y);

			//when selecting a slot option, we reenter the hub with menu depth 1 to see cosmetics
			option.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {

					slotChosen = selected;

					state.getUiHub().setType(type);
					state.getUiHub().enter(me);
					addOptions(lastSearch, -1, lastTag);
					menuDepth = 1;
				}
			});
			hub.addActor(option, option.getWidth(), 2);
		}
		hub.addActorFinish();
	}

	@Override
	public void controller(float delta) {
		super.controller(delta);
		loadCount += delta;

		//at set interval, create new hub option for the next to-be-loaded player
		if (loadCount >= loadInterval) {
			loadCount = 0.0f;

			if (!loadingCosmetics.isEmpty()) {
				final UIHub hub = state.getUiHub();
				final HubEvent me = this;

				Player ownPlayer = HadalGame.usm.getOwnPlayer();

				if (null == ownPlayer) { return; }
				if (null == ownPlayer.getPlayerData()) { return; }

				UnlockCosmetic selected = loadingCosmetics.removeIndex(0);

				//Have to load the shaded cosmetic prior to creating player option to avoid shaded hat not appearing
				CharacterCosmetic cosmetic = selected.getCosmetics().get(lastCharacter);
				if (cosmetic != null) {
					cosmetic.getShadedFrames(state.getBatch(), lastFilter, lastCharacter);
				}

				HubOptionPlayer option = new HubOptionPlayer(selected.getName(), ownPlayer, lastCharacter,
						lastFilter, true, selected);
				option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_CHARACTER_HEIGHT);
				option.setWrap(TEXT_WIDTH);
				option.setYOffset(TEXT_OFFSET_CHARACTER_Y);

				sprites.add(option);

				//clicking on the option sets your cosmetic
				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						Player ownPlayer = HadalGame.usm.getOwnPlayer();

						if (null == ownPlayer) { return; }

						UnlockCosmetic choice = selected;
						if (!isEquipping(ownPlayer.getUser(), selected)) {
							choice = getBlank(selected.getCosmeticSlot());
						}

						//we set loadout for both server and client so cosmetic slot page is accurate upon returning
						ownPlayer.getCosmeticsHelper().setCosmetic(choice);

						if (state.isServer()) {
							ownPlayer.getCosmeticsHelper().syncServerCosmeticChange(choice);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncCosmeticClient(choice));
						}
						state.getGsm().getLoadout().setCosmetic(HadalGame.usm.getOwnUser(), choice.getCosmeticSlot().getSlotNumber(), choice.toString());

						me.enter();
						hub.refreshHub(me);
					}

					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});

				//if we are loading the last character, finish loading sprites
				if (hub.getType().equals(hubTypes.HABERDASHER) && menuDepth == 1) {
					hub.addActor(option, option.getWidth(), 1);

					if (loadingCosmetics.isEmpty()) {
						hub.addActorFinish();
					}
				}
			}
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}

	/**
	 * This returns whether the player already has the given cosmetic equipped
	 */
	private static boolean isEquipping(User user, UnlockCosmetic cosmetic) {
		return user.getLoadoutManager().getActiveLoadout().cosmetics[cosmetic.getCosmeticSlot().getSlotNumber()] != cosmetic;
	}

	private static UnlockCosmetic getBlank(CosmeticSlot slot) {
		return switch (slot) {
			case HAT2 -> UnlockCosmetic.NOTHING_HAT2;
			case EYE -> UnlockCosmetic.NOTHING_EYE;
			case NOSE -> UnlockCosmetic.NOTHING_NOSE;
			case MOUTH1 -> UnlockCosmetic.NOTHING_MOUTH1;
			case MOUTH2 -> UnlockCosmetic.NOTHING_MOUTH2;
			case HEAD -> UnlockCosmetic.NOTHING_HEAD;
			case SKIN -> UnlockCosmetic.NOTHING_SKIN;
			case DECAL_HEAD -> UnlockCosmetic.NOTHING_DECAL_HEAD;
			case DECAL_BODY -> UnlockCosmetic.NOTHING_DECAL_BODY;
			default -> UnlockCosmetic.NOTHING_HAT1;
		};
	}

	@Override
	public void back() {
		super.back();
		if (menuDepth > 0) {
			enter();
		}
	}

	@Override
	public boolean isSearchable() { return true; }
}
