package com.mygdx.hadal.equip;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;

/**
 * An active item is an item displayed in the lower right corner. They can be used with the spacebar and have a cooldown or some other charging mechanic.
 * @author Neston Noggby
 */
public class ActiveItem extends Equippable {

	//active items charge slower with damage compared to time (this is so that the numbers are on the same scale)
	public static final float DAMAGE_CHARGE_MULTIPLIER = 0.2f;

	//active items charge slower when damaging non-player enemies. (according to this float)
	public static final float ENEMY_DAMAGE_CHARGE_MULTIPLIER = 0.1f;

	protected final float maxCharge;
	protected float currentCharge;

	public ActiveItem(Player user, float maxCharge) {
		super(user, 0.0f, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
		this.maxCharge = maxCharge;
	}
	
	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	private final Vector2 playerLocation = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mousePosition) {

		playerLocation.set(shooter.getSchmuck().getPixelPosition());
		
		float powerDiv = playerLocation.dst(mousePosition);
		weaponVelo.set(playerLocation).sub(mousePosition).scl(-1 / powerDiv);
		
		this.faction = faction;
		this.mouseLocation.set(mousePosition);
	}
	
	/**
	 * This is run after the weapon's useDelay to actually use it.
	 * Here, the stored properties are used to use the item
	 */
	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {
		
		user.getBodyData().statusProcTime(new ProcTime.BeforeActiveUse(this));
		
		if (currentCharge >= maxCharge) {
			currentCharge = 0;
			useItem(state, shooter);
			
			user.getBodyData().statusProcTime(new ProcTime.AfterActiveUse(this));
		}
	}
	
	/**
	 * This uses the item. Overridden in each active item.
	 * @param state: Playstate
	 * @param shooter: the user of the item
	 */
	public void useItem(PlayState state, PlayerBodyData shooter) {}

	private static final float MELEE_CHARGE_BONUS = 0.5f;
	/**
	 * This is used when the active item charges
	 * @param charge: The amount of charge that the item gains.
	 */
	public void gainCharge(float charge) {
		
		//this is used to keep track of when an item fully charges to play a sound
		boolean uncharged = currentCharge < maxCharge;
		
		if (user.getEquipHelper().getCurrentTool() instanceof MeleeWeapon) {
			currentCharge += (charge * (1 + MELEE_CHARGE_BONUS + user.getBodyData().getStat(Stats.ACTIVE_CHARGE_RATE)));
		} else {
			currentCharge += (charge * (1 + user.getBodyData().getStat(Stats.ACTIVE_CHARGE_RATE)));
		}
		
		if (currentCharge >= maxCharge) {
			currentCharge = maxCharge;
			
			if (uncharged) {
				if (user.equals(HadalGame.usm.getOwnPlayer())) {
					SoundManager.play(new SoundLoad(SoundEffect.MAGIC1_ACTIVE)
							.setVolume(0.4f));
				}
			}
		}
	}
	
	/**
	 * This is gainCharge except by a percentage of the max charge
	 */
	public void gainChargeByPercent(float charge) {

		//this is used to keep track of when an item fully charges to play a sound
		boolean uncharged = currentCharge < maxCharge;

		currentCharge += (charge * maxCharge * (1 + user.getBodyData().getStat(Stats.ACTIVE_CHARGE_RATE)));
		
		if (currentCharge > maxCharge) {
			currentCharge = maxCharge;
			
			if (uncharged) {
				if (user.equals(HadalGame.usm.getOwnPlayer())) {
					SoundManager.play(new SoundLoad(SoundEffect.MAGIC1_ACTIVE)
							.setVolume(0.4f));
				}
			}
		}
	}
	
	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, PlayerBodyData bodyData) {}

	@Override
	public boolean reload(float delta) { 
		reloading = false; 
		return false;
	}

	public boolean isUsable() { return currentCharge >= maxCharge; }

	public float chargePercent() { return currentCharge / maxCharge; }	
	
	public float getMaxCharge() { return maxCharge; }

	public float getCurrentCharge() { return currentCharge; }

	public void setCurrentCharge(float currentCharge) {	
		this.currentCharge = currentCharge; 
		if (this.currentCharge > maxCharge) {
			this.currentCharge = maxCharge;
		}
	}

	public void setCurrentChargePercent(float currentCharge) {	
		this.currentCharge = currentCharge * maxCharge; 
		if (this.currentCharge > maxCharge) {
			this.currentCharge = maxCharge;
		}
	}

	/**
	 * This is a vague estimate of how long this item takes to activate.
	 * This is checked by a few effects that want to do something after an active item completes.
	 */
	public float getUseDuration() { return 0.0f; }
}
