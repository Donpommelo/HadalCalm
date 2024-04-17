package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.effects.CharacterCosmetic;
import com.mygdx.hadal.managers.JSONManager;
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
public class Vending extends HubEvent {

	public static final int REFRESH_COST = 4;

	private final Array<String> weapons = new Array<>();
	private final Array<String> artifacts = new Array<>();
	private final Array<String> magics = new Array<>();

	private final int numWeapon, numArtifact, numMagic;

	public Vending(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, int numWeapon, int numArtifact, int numMagic) {
		super(state, startPos, size, title, tag, false, true, hubTypes.VENDING);
		this.lastSlot = -1;
		this.numWeapon = numWeapon;
		this.numArtifact = numArtifact;
		this.numMagic = numMagic;
		setChoices();
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
		final Vending me = this;

		Array<UnlockTag> newTags = new Array<>(tags);
		if (tag != null) {
			newTags.add(tag);
		}

		final UIHub hub = state.getUiHub();

		for (String c : artifacts) {
			final UnlockArtifact selected = UnlockArtifact.getByName(c);
			if (checkArtifactOwnership(selected)) { continue; }

			HubOption option = new HubOption(UIText.ARTIFACT_OPTION_VENDING.text(selected.getName(),
					Integer.toString(JSONManager.artifactInfo.getPrices().get(c))),
					new Animation<>(CharacterCosmetic.COSMETIC_ANIMATION_SPEED,
					selected.getFrame()));

			option.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					if (state.isServer()) {
						Vending.checkUnlock(state, selected, HadalGame.usm.getOwnUser());

						me.leave();
						me.enter();
						state.getUiExtra().syncUIText(UITag.uiType.CURRENCY);
					} else {
						HadalGame.client.sendTCP(new PacketsLoadout.SyncVendingArtifact(selected));
					}
				}

				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					hub.setInfo(UIText.ARTIFACT_INFO_VENDING.text(selected.getName(),
							Integer.toString(JSONManager.artifactInfo.getPrices().get(c)),
							selected.getDesc(), selected.getDescLong()));
				}
			});
			hub.addActor(option, option.getWidth(), 4);
		}
		hub.addActorFinish();
		hub.refreshHub(this);
	}

	public void setChoices() {
		weapons.clear();
		artifacts.clear();
		magics.clear();

		while (weapons.size < numWeapon) {
			String weapon = JSONManager.weaponInfo.getPrices().keys().toArray()
					.get(MathUtils.random(JSONManager.weaponInfo.getPrices().size - 1));
			if (!weapons.contains(weapon, false)) {
				weapons.add(weapon);
			}
		}
		while (artifacts.size < numArtifact) {
			String artifact = JSONManager.artifactInfo.getPrices().keys().toArray()
					.get(MathUtils.random(JSONManager.artifactInfo.getPrices().size - 1));
			if (!artifacts.contains(artifact, false)) {
				if (!checkArtifactOwnership(UnlockArtifact.getByName(artifact))) {
					artifacts.add(artifact);
				}
			}
		}
		while (magics.size < numMagic) {
			String magic = JSONManager.magicInfo.getPrices().keys().toArray()
					.get(MathUtils.random(JSONManager.magicInfo.getPrices().size - 1));
			if (!magics.contains(magic, false)) {
				magics.add(magic);
			}
		}
	}

	private boolean checkArtifactOwnership(UnlockArtifact artifact) {
		UnlockArtifact[] artifacts = HadalGame.usm.getOwnUser().getLoadoutManager().getActiveLoadout().artifacts;
		boolean alreadyUsed = false;
        for (UnlockArtifact unlockArtifact : artifacts) {
            if (artifact.equals(unlockArtifact)) {
                alreadyUsed = true;
                break;
            }
        }
		return alreadyUsed;
	}

	public void refreshOptions() {
		if (HadalGame.usm.getOwnUser().getScoreManager().getCurrency() >= REFRESH_COST) {
			if (state.isServer()) {
				HadalGame.usm.getOwnUser().getScoreManager().setCurrency(HadalGame.usm.getOwnUser().getScoreManager().getCurrency() - REFRESH_COST);
			} else {
				HadalGame.client.sendTCP(new PacketsLoadout.SyncVendingScrapSpend(REFRESH_COST));
			}

			setChoices();
			enter();
			state.getUiHub().refreshHub(this);
		}
	}

	public static void checkUnlock(PlayState state, UnlockArtifact selected, User user) {
		int cost = JSONManager.artifactInfo.getPrices().get(selected.name());

		if (user.getScoreManager().getCurrency() >= cost) {
			user.getScoreManager().setCurrency(user.getScoreManager().getCurrency() - cost);
			if (null != user.getPlayer()) {
				user.getPlayer().getArtifactHelper().addArtifact(selected, true, false);

				String playerName = TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH);
				state.getKillFeed().addNotification(UIText.VENDING_PURCHASE.text(playerName, selected.getName()), true);
			}
		}
	}
}
