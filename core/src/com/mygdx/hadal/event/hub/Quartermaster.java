package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.InfoItem;
import com.mygdx.hadal.save.ShopInfo;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.states.PlayState;

import java.util.Objects;

/**
 * The Armory is a HubEvent that allows the player to spend Scrap on unlocks.
 * @author Bracciatello Bestfruit
 */
public class Quartermaster extends HubEvent {

	//This contains information about this shop's selection
	private final ShopInfo shopInfo;
	
	public Quartermaster(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave, String shopId) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.QUARTERMASTER);
		this.shopInfo = GameStateManager.json.fromJson(ShopInfo.class, GameStateManager.shops.get(shopId).toJson(OutputType.minimal));
	}
	
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		final Quartermaster me = this;
		
		for (final String item: shopInfo.getPrices().keySet()) {
			
			InfoItem info = UnlockManager.getInfo(UnlockType.valueOf(shopInfo.getType()), item);
			
			if (checkUnlock && !UnlockManager.checkUnlock(state, UnlockType.valueOf(shopInfo.getType()), item)) {

				Text itemChoose = new Text(Objects.requireNonNull(info).getName() + ": Cost: " + shopInfo.getPrices().get(item), 0, 0, true);
				
				itemChoose.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	if (state.getGsm().getRecord().getScrap() >= shopInfo.getPrices().get(item)) {
				        	state.getGsm().getRecord().incrementScrap(-shopInfo.getPrices().get(item));
				        	UnlockManager.setUnlock(state, UnlockType.valueOf(shopInfo.getType()), item, true);
				        	
				        	//leave and enter to reset available inventory
				        	me.leave();
				        	me.enter();
				        	state.getUiExtra().syncData();
			        	}
			        }
					
					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						hub.setInfo(info.getName() +  "\n\n" + info.getDescription() + " \n\n" + info.getDescriptionLong());
					}
			    });
				itemChoose.setScale(UIHub.optionsScaleSmall);
				hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
			}
		}
		hub.getTableOptions().add(new Text("", 0, 0, false)).height(UIHub.optionsHeight).row();
	}
}
