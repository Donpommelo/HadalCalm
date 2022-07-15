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
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Haberdasher extends HubEvent {

	private static final float iconScale = 0.4f;
	private static final int TEXT_WIDTH = 240;
	private static final int TEXT_OFFSET_Y = 45;
	private static final int OPTION_WIDTH = 250;
	private static final int OPTION_HEIGHT = 270;

	private static final int TEXT_OFFSET_CHARACTER_Y = 195;
	private static final int OPTION_CHARACTER_HEIGHT = 500;

	private static final float loadInterval = 0.1f;
	private float loadCount;

	private final Array<UnlockCosmetic> loadingCosmetics = new Array<>();
	private final Array<HubOptionPlayer> sprites = new Array<>();
	private AlignmentFilter lastFilter;
	private UnlockCharacter lastCharacter;
	private CosmeticSlot lastCosmetic;

	//this is the selected game mode
	private CosmeticSlot slotChosen = CosmeticSlot.HAT1;

	public Haberdasher(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.HABERDASHER);
	}

	@Override
	public void addOptions(String search, int slots, UnlockManager.UnlockTag tag) {
		super.addOptions(search, slots, tag);

		Pattern pattern = Pattern.compile(search);

		if (state.getPlayer().getPlayerData() == null) {
			return;
		}

		if (lastCharacter != state.getPlayer().getPlayerData().getLoadout().character
				|| lastFilter != state.getPlayer().getPlayerData().getLoadout().team || lastCosmetic != slotChosen) {
			for (HubOptionPlayer sprite : sprites) {
				sprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
			}
			sprites.clear();

			lastCharacter = state.getPlayer().getPlayerData().getLoadout().character;
			lastFilter = state.getPlayer().getPlayerData().getLoadout().team;
			lastCosmetic = slotChosen;

			for (UnlockCosmetic c : UnlockCosmetic.getUnlocks(state, checkUnlock, tags)) {
				if (!c.getCosmeticSlot().equals(slotChosen)) {
					continue;
				}
				if (c.checkCompatibleCharacters(lastCharacter)) {
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

		for (CosmeticSlot slot : CosmeticSlot.values()) {
			final CosmeticSlot selected = slot;

			PlayerBodyData playerData = state.getPlayer().getPlayerData();
			Animation<TextureRegion> frame = null;

			if (playerData != null) {
				UnlockCosmetic cosmeticType = playerData.getLoadout().cosmetics[slot.getSlotNumber()];
				if (!cosmeticType.isBlank()) {
					UnlockCharacter character = playerData.getLoadout().character;
					CharacterCosmetic cosmetic = cosmeticType.getCosmetics().get(character);
					if (cosmetic != null) {
						frame = cosmetic.getShadedFrames(state.getBatch(), playerData.getLoadout().team, character);
					}
				}
			}

			if (frame == null) {
				frame = new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED, Sprite.EMOTE_NO.getFrames());
			}

			float iconWidth = frame.getKeyFrame(0).getRegionWidth();
			float iconHeight = frame.getKeyFrame(0).getRegionHeight();

			int cosmeticCount = 0;
			for (UnlockCosmetic c : UnlockCosmetic.getUnlocks(state, checkUnlock, tags)) {
				if (!c.getCosmeticSlot().equals(slot)) {
					continue;
				}
				if (c.checkCompatibleCharacters(state.getPlayer().getPlayerData().getLoadout().character)) {
					continue;
				}
				cosmeticCount++;
			}

			if (cosmeticCount == 0) { continue; }

			HubOption option = new HubOption(slot.getSlotName() + " (" + cosmeticCount + ")", frame);
			option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_HEIGHT);
			option.setIconWidth((int) (iconWidth * iconScale)).setIconHeight((int) (iconHeight * iconScale));
			option.setWrap(TEXT_WIDTH);
			option.setYOffset(TEXT_OFFSET_Y);

			option.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {

					slotChosen = selected;

					state.getUiHub().setType(type);
					state.getUiHub().enter(true, false, false, me);
					addOptions(lastSearch, -1, lastTag);
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
		if (loadCount >= loadInterval) {
			loadCount = 0.0f;

			if (!loadingCosmetics.isEmpty()) {
				final UIHub hub = state.getUiHub();
				final HubEvent me = this;

				UnlockCosmetic selected = loadingCosmetics.removeIndex(0);

				CharacterCosmetic cosmetic = selected.getCosmetics().get(lastCharacter);
				if (cosmetic != null) {
					cosmetic.getShadedFrames(state.getBatch(), lastFilter, lastCharacter);
				}

				HubOptionPlayer option = new HubOptionPlayer(getCosmeticText(state.getPlayer().getPlayerData(), selected),
						state.getPlayer(), lastCharacter, lastFilter, selected);
				option.setOptionWidth(OPTION_WIDTH).setOptionHeight(OPTION_CHARACTER_HEIGHT);
				option.setWrap(TEXT_WIDTH);
				option.setYOffset(TEXT_OFFSET_CHARACTER_Y);

				sprites.add(option);

				option.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						if (state.getPlayer().getPlayerData() == null) {
							return;
						}

						UnlockCosmetic choice = selected;
						if (!isEquipping(state.getPlayer().getPlayerData(), selected)) {
							choice = getBlank(selected.getCosmeticSlot());
						}

						if (state.isServer()) {
							state.getPlayer().getPlayerData().setCosmetic(choice);
							state.getPlayer().getPlayerData().syncServerCosmeticChange(choice);
						} else {
							HadalGame.client.sendTCP(new PacketsLoadout.SyncCosmeticClient(choice));
						}
						state.getGsm().getLoadout().setCosmetic(choice.getCosmeticSlot().getSlotNumber(), choice.toString());

						me.enter();
						hub.refreshHub(me);
					}

					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(selected.getName() + "\n\n" + selected.getDesc());
					}
				});

				if (hub.getType().equals(hubTypes.HABERDASHER)) {
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

	@Override
	public void dispose() {
		for (HubOptionPlayer sprite : sprites) {
			sprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
		}
	}

	private static boolean isEquipping(PlayerBodyData playerData, UnlockCosmetic cosmetic) {
		return playerData.getLoadout().cosmetics[cosmetic.getCosmeticSlot().getSlotNumber()] != cosmetic;
	}

	private static String getCosmeticText(PlayerBodyData playerData, UnlockCosmetic cosmetic) {
		boolean equipping = isEquipping(playerData, cosmetic);
		if (equipping) {
			return cosmetic.getCosmeticSlot().getSlotName() + " " + cosmetic.getName() + " (EQUIP)";
		} else {
			return cosmetic.getCosmeticSlot().getSlotName() + " " + cosmetic.getName() + " (UNEQUIP)";
		}
	}

	private static UnlockCosmetic getBlank(CosmeticSlot slot) {
		return switch (slot) {
			case HAT2 -> UnlockCosmetic.NOTHING_HAT2;
			case EYE -> UnlockCosmetic.NOTHING_EYE;
			case NOSE -> UnlockCosmetic.NOTHING_NOSE;
			case MOUTH1 -> UnlockCosmetic.NOTHING_MOUTH1;
			case MOUTH2 -> UnlockCosmetic.NOTHING_MOUTH2;
			case HEAD -> UnlockCosmetic.NOTHING_HEAD;
			case DECAL_HEAD -> UnlockCosmetic.NOTHING_DECAL_HEAD;
			case DECAL_BODY -> UnlockCosmetic.NOTHING_DECAL_BODY;
			default -> UnlockCosmetic.NOTHING_HAT1;
		};
	}
}
