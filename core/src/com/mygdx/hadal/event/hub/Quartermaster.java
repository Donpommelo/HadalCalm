package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.actors.HubOption;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.save.ShopInfo;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mygdx.hadal.managers.JSONManager.JSON;

/**
 * The Armory is a HubEvent that allows the player to spend Scrap on unlocks.
 * @author Bracciatello Bestfruit
 */
public class Quartermaster extends HubEvent {

	//This contains information about this shop's selection
	private final ShopInfo shopInfo;
	
	public Quartermaster(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave, String shopId) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.QUARTERMASTER);
		this.shopInfo = JSON.fromJson(ShopInfo.class, JSONManager.shops.get(shopId).toJson(OutputType.json));
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
	public void addOptions(String search, int slots, UnlockManager.UnlockTag tag) {
		super.addOptions(search, slots, tag);
		Pattern pattern = Pattern.compile(search);
		final UIHub hub = state.getUiHub();
		final Quartermaster me = this;

		for (final String item : shopInfo.getPrices().keySet()) {
			String name = UnlockManager.getName(UnlockType.valueOf(shopInfo.getType()), item);
			String desc = UnlockManager.getDesc(UnlockType.valueOf(shopInfo.getType()), item);
			String descLong = UnlockManager.getDescLong(UnlockType.valueOf(shopInfo.getType()), item);

			if (checkUnlock && !UnlockManager.checkUnlock(state, UnlockType.valueOf(shopInfo.getType()), item)) {
				boolean appear = false;
				if (search.isEmpty()) {
					appear = true;
				} else {
					Matcher matcher = pattern.matcher(name.toLowerCase());
					if (matcher.find()) {
						appear = true;
					}
				}

				if (appear) {
					HubOption option = new HubOption(name, null);

					option.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {
							if (JSONManager.record.getScrap() >= shopInfo.getPrices().get(item)) {
								JSONManager.record.incrementScrap(-shopInfo.getPrices().get(item));
								UnlockManager.setUnlock(state, UnlockType.valueOf(shopInfo.getType()), item, true);

								//leave and enter to reset available inventory
								me.leave();
								me.enter();
								state.getUiExtra().syncUIText(UITag.uiType.SCRAP);
							}
						}

						@Override
						public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
							super.enter(event, x, y, pointer, fromActor);
							hub.setInfo(name + "\n\n" + desc + "\n\n" + descLong);
						}
					});
					hub.addActor(option, option.getWidth(), 4);
				}
			}
		}
		hub.addActorFinish();
	}

	@Override
	public boolean isSearchable() { return true; }
}
