package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * A PlayerChanger TBA
 * 
 * @author Zachary Tu
 *
 */
public class PlayerChanger extends Event {

	private static final String name = "Player Changer";
	
	private float hp, fuel;
	private int scrap;
	
	public PlayerChanger(PlayState state, float hp, float fuel, int scrap) {
		super(state, name);
		this.hp = hp;
		this.fuel = fuel;
		this.scrap = scrap;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				PlayerBodyData data = state.getPlayer().getPlayerData();
				boolean activated = false;
				
				if (data.getCurrentFuel() < data.getMaxFuel() && fuel > 0) {
					data.fuelGain(fuel);
					activated = true;
				}
				
				if (data.getCurrentHp() < data.getMaxHp() && hp > 0) {
					state.getPlayer().getPlayerData().regainHp(hp, state.getPlayer().getPlayerData(), true, DamageTypes.MEDPAK);
					activated = true;
				}
				
				if (hp < 0) {
					state.getPlayer().getPlayerData().receiveDamage(-hp, new Vector2(0, 0), state.getWorldDummy().getBodyData(), null, false);
					activated = true;
				}
				
				if (scrap > 0) {
					state.getGsm().getRecord().incrementScrip(scrap);
					activated = true;
				}
				
				if (activated && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().onActivate(this);
				}
			}
		};
	}
}
