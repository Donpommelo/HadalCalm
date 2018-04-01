package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import box2dLight.RayHandler;

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
	
	public PlayerChanger(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float hp, float fuel, int scrap) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.hp = hp;
		this.fuel = fuel;
		this.scrap = scrap;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
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
