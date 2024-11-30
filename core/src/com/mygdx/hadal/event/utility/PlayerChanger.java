package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A PlayerChanger changes some property of the player (Hp, fuel, scrap, maybe other stuff we add later?)
 * <p>
 * Triggered Behavior: When triggered, this event makes changes the properties of the player.
 * Triggering Behavior: Ok, this is a weird one. When activated, this event will chain to its connected event if
 * the player change is valid. 
 * <p>
 * Example: We want to use this for a medpack that heals the player. The player touches it. We want the medpack to
 * despawn if the player's use of it is valid(ie not already at full health)
 * <p>
 * In this case, the Triggering Behavior would be to make the medpack despawn. 
 * <p>
 * Also, as an added note, when this event chains, it inputs its activator event, not itself. This is kinda weird and arbitrary
 * but makes it so that event deleters can more easily delete the medpack without the use of an extra alt-trigger.
 * <p>
 * I think that the repercussions of this are probably minor, because this event will probably not be changed by downstream events.
 * <p>
 * Fields:
 * <p>
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
				if (data == null) { return; }

				boolean activated = false;
				
				if (data.getCurrentFuel() < data.getStat(Stats.MAX_FUEL) && fuel > 0) {
					data.fuelGain(fuel);
					activated = true;

					SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC2_FUEL)
							.setVolume(0.3f)
							.setPosition(p.getPixelPosition()));
				}
				
				if (data.getCurrentHp() < data.getStat(Stats.MAX_HP) && hp > 0) {
					data.regainHp(hp * data.getStat(Stats.MAX_HP) / 100, p.getPlayerData(), true,
							DamageTag.MEDPAK);
					activated = true;

					SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC21_HEAL)
							.setVolume(0.3f)
							.setPosition(p.getPixelPosition()));
				}
				
				if (p.getEquipHelper().getCurrentTool().getAmmoLeft() < p.getEquipHelper().getCurrentTool().getAmmoSize() && ammo > 0) {
					p.getEquipHelper().getCurrentTool().gainAmmo(ammo);
					activated = true;

					SoundManager.play(state, new SoundLoad(SoundEffect.LOCKANDLOAD)
							.setVolume(0.8f)
							.setPosition(p.getPixelPosition()));
				}
				
				if (hp < 0) {
					SoundManager.play(state, new SoundLoad(SoundEffect.DAMAGE5)
							.setVolume(0.4f)
							.setPosition(p.getPixelPosition()));

					data.receiveDamage(-hp, new Vector2(), state.getWorldDummy().getBodyData(), false,
							null, DamageSource.MAP_FALL);
					activated = true;
				}
				
				if (ammo < 0) {
					p.getEquipHelper().getCurrentTool().gainAmmo(ammo);
					activated = true;
				}
				
				if (activated && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(activator, p);
				}
			}
		};
	}
}
