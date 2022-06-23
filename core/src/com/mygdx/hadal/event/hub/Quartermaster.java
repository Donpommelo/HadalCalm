package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.ShopInfo;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * The Armory is a HubEvent that allows the player to spend Scrap on unlocks.
 * @author Bracciatello Bestfruit
 */
public class Quartermaster extends HubEvent {

	//This contains information about this shop's selection
	private final ShopInfo shopInfo;
	
	public Quartermaster(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave, String shopId) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.QUARTERMASTER);
		this.shopInfo = GameStateManager.json.fromJson(ShopInfo.class, GameStateManager.shops.get(shopId).toJson(OutputType.json));
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		final Quartermaster me = this;
		
		for (final String item : shopInfo.getPrices().keySet()) {

			String name = UnlockManager.getName(UnlockType.valueOf(shopInfo.getType()), item);
			String desc = UnlockManager.getDesc(UnlockType.valueOf(shopInfo.getType()), item);
			String descLong = UnlockManager.getDescLong(UnlockType.valueOf(shopInfo.getType()), item);

			if (checkUnlock && !UnlockManager.checkUnlock(state, UnlockType.valueOf(shopInfo.getType()), item)) {

				Text itemChoose = new Text(UIText.QUARTERMASTER_COST.text(name,
						Integer.toString(shopInfo.getPrices().get(item)))).setButton(true);
				
				itemChoose.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	if (state.getGsm().getRecord().getScrap() >= shopInfo.getPrices().get(item)) {
				        	state.getGsm().getRecord().incrementScrap(-shopInfo.getPrices().get(item));
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
				itemChoose.setScale(UIHub.optionsScaleSmall);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}
}
