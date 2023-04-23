package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;

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
 * I think that the repercussions of this are probably minor, because this event will probably not be changed by downstream events.
 * 
 * Fields:
 * 
 * hp: Change the player Hp by this amount. Optional. Default: 0.0f
 * fuel: Change the player Fuel by this amount. Optional. Default: 0.0f
 * ammo: Change the player amo by this amount. Optional. Default: 0.0f
 * 
 * @author Lostard Lodrach
 */
public class PlayerChanger extends Event {

	private final float hp, fuel, ammo;
	
	public PlayerChanger(PlayState state, float hp, float fuel, float ammo) {
		super(state);
		this.hp = hp;
		this.fuel = fuel;
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
					
					SoundEffect.MAGIC2_FUEL.playSourced(state, p.getPixelPosition(), 0.3f);
				}
				
				if (data.getCurrentHp() < data.getStat(Stats.MAX_HP) && hp > 0) {
					data.regainHp(hp * data.getStat(Stats.MAX_HP) / 100, p.getPlayerData(), true,
							DamageTag.MEDPAK);
					activated = true;
					
					SoundEffect.MAGIC21_HEAL.playSourced(state, p.getPixelPosition(), 0.3f);
				}
				
				if (data.getCurrentTool().getAmmoLeft() < data.getCurrentTool().getAmmoSize() && ammo > 0) {
					data.getCurrentTool().gainAmmo(ammo);
					activated = true;
					
					SoundEffect.LOCKANDLOAD.playUniversal(state, p.getPixelPosition(), 0.8f, false);
				}				
				
				if (hp < 0) {
					SoundEffect.DAMAGE5.playSourced(state, p.getPixelPosition(), 0.4f);

					data.receiveDamage(-hp, new Vector2(), state.getWorldDummy().getBodyData(), false,
							null, DamageSource.MAP_FALL);
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
