package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Stats;

/**
 * A PlayerChanger changes some property of the player (Hp, fuel, scrap, maybe other stuff we add later?)
 * 
 * Triggered Behavior: When triggered, this event makes changes the properties of the player.
 * Triggering Behavior: Ok, this is a weird one. When activated, this event will chain to its connected event if
 * the player change is valid. 
 * 
 * Example: We want to use this for a medpack that heals the player. The player touches it. We want the medpack to
 * despawn if the player's use of it is valid(ie not already at full health)
 * 
 * In this case, the Triggering Behavior would be to make the medpack despawn. 
 * 
 * Also, as an added note, when this event chains, it inputs its activator event, not itself. This is kinda weird and arbitrary
 * but makes it so that event deleters can more easily delete the medpack without the use of an extra alt-trigger.
 * 
 * I think that the repurcussions of this are probably minor, because this event will probably not be changed by downstream events.
 * 
 * Fields:
 * 
 * hp: Change the player Hp by this amount. Optional. Default: 0.0f
 * fuel: Change the player Fuel by this amount. Optional. Default: 0.0f
 * scrap: Change the player Scrap by this amount. Optional. Default: 0.0f
 * 
 * @author Zachary Tu
 *
 */
public class PlayerChanger extends Event {

	private static final String name = "Player Changer";
	
	private float hp, fuel, ammo;
	private int scrap;
	
	public PlayerChanger(PlayState state, float hp, float fuel, float ammo, int scrap) {
		super(state, name);
		this.hp = hp;
		this.fuel = fuel;
		this.scrap = scrap;
		this.ammo = ammo;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				PlayerBodyData data = p.getPlayerData();
				boolean activated = false;
				
				if (data.getCurrentFuel() < data.getStat(Stats.MAX_FUEL) && fuel > 0) {
					data.fuelGain(fuel);
					activated = true;
				}
				
				if (data.getCurrentHp() < data.getStat(Stats.MAX_HP) && hp > 0) {
					data.regainHp(hp, p.getPlayerData(), true, DamageTypes.MEDPAK);
					activated = true;
				}
				
				if (data.getCurrentTool().getAmmoLeft() < data.getCurrentTool().getAmmoSize() && ammo > 0) {
					data.getCurrentTool().gainAmmo(ammo);
					activated = true;
				}				
				
				if (hp < 0) {
					data.receiveDamage(-hp, new Vector2(), state.getWorldDummy().getBodyData(), null, false);
					activated = true;
				}
				
				if (scrap > 0) {
					state.getGsm().getRecord().incrementScrap(scrap);
					state.getUiExtra().syncData();
					activated = true;
				}
				
				if (ammo < 0) {
					data.getCurrentTool().gainAmmo(ammo);
					activated = true;
				}
				
				if (activated && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(activator, p);
				}
			}
		};
	}
}
