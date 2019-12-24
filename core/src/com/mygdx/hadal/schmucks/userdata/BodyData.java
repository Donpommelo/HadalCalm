package com.mygdx.hadal.schmucks.userdata;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.utils.Stats;

/**
 * Body data contains the stats and methods of any unit; player or enemy.
 * @author Zachary Tu
 *
 */
public class BodyData extends HadalData {

	//The Schmuck that owns this data
	protected Schmuck schmuck;
	
	//schmuck stats
	private float[] baseStats;
	private float[] buffedStats;	
	
	//Speed on ground
	private static final float maxGroundXSpeed = 15.0f;
	private static final float maxAirXSpeed = 10.0f;
		
	//Accelerating on the ground/air
	private static final float groundXAccel = 0.10f;
	private static final float airXAccel = 0.05f;
	private static final float groundXDeaccel = 0.05f;
	private static final float airXDeaccel = 0.01f;
	
	private static final float groundYAccel = 0.10f;
	private static final float airYAccel = 0.50f;
	private static final float groundYDeaccel = 0.05f;
	private static final float airYDeaccel = 0.01f;
	
	//Hp and regen
	private static final float hpRegen = 0.0f;
	
	private static final int maxFuel = 100;
	private static final float fuelRegen = 8.0f;
	
	private final static float flashDuration = 0.08f;
	
	protected float currentHp, currentFuel;

	//statuses inflicted o nthe unit. statuses checked is used to recursive activate each status effect
	protected ArrayList<Status> statuses;
	protected ArrayList<Status> statusesChecked;	
	
	//the currently equipped tool
	protected Equipable currentTool;
	
	//This is the last schumck who damaged this entity. Used for kill credit
	private BodyData lastDamagedBy;
	
	/**
	 * This is created upon the create() method of any schmuck.
	 * Schmucks are the Body data type.
	 * @param schmuck: the entity that has this data
	 * @param maxHp: the unit's hp
	 */
	public BodyData(Schmuck schmuck, int maxHp) {
		super(UserDataTypes.BODY, schmuck);
		this.schmuck = schmuck;	
		
		this.baseStats = new float[52];
		this.buffedStats = new float[52];
		
		baseStats[0] = maxHp;
		baseStats[1] = maxFuel;
		baseStats[2] = hpRegen;
		baseStats[3] = fuelRegen;
		
		this.statuses = new ArrayList<Status>();
		this.statusesChecked = new ArrayList<Status>();
		
		calcStats();

		currentHp = getStat(Stats.MAX_HP);
		currentFuel = getStat(Stats.MAX_FUEL);
		
		lastDamagedBy = this;
	}
	
	/**
	 * Status proc time is called at certain points of the game that could activate any effect.
	 * @param procTime: the type of proc time that this is
	 * This fields of this are the various info needed for each status. fields will be null when unused
	 * @return a float for certain statuses that pass along a modified value (like on damage effects)
	 */
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
				
		float finalAmount = amount;
		ArrayList<Status> oldChecked = new ArrayList<Status>();
		for(Status s : this.statusesChecked) {
			this.statuses.add(0, s);
			oldChecked.add(s);
		}
		this.statusesChecked.clear();
		
		while(!this.statuses.isEmpty()) {
			Status tempStatus = this.statuses.get(0);
			
			finalAmount = tempStatus.statusProcTime(procTime, schmuck, finalAmount, status, tool, hbox, tags);
			
			if(this.statuses.contains(tempStatus)){
				this.statuses.remove(tempStatus);
				this.statusesChecked.add(tempStatus);
			}
		}
		
		for(Status s : this.statusesChecked) {
			if(!oldChecked.contains(s)) {
				this.statuses.add(s);
			}
		}
		this.statusesChecked.clear();
		for(Status s : oldChecked) {
			this.statusesChecked.add(s);
		}
		return finalAmount;		
	}
	
	/**
	 * Add a status to this schmuck
	 * @param s: Status to add
	 */
	public void addStatus(Status s) {
		
		if (!schmuck.getState().isServer()) {
			return;
		}
		
		boolean added = false;
		Status old = getStatus(s.getClass());
		if (old != null) {
			switch(s.getStackType()) {
			case ADD:
				added = true;
				break;
			case IGNORE:
				break;
			case REPLACE:
				old.setDuration(s.getDuration());
				break;			
			}
		} else {
			added = true;
		}
		
		if (added) {
			statuses.add(s);
			statusProcTime(StatusProcTime.ON_INFLICT, null, 0, s, null, null);
			calcStats();
		}
	}
	
	/**
	 * Removes a status from this schmuck
	 */
	public void removeStatus(Status s) {
		statusProcTime(StatusProcTime.ON_REMOVE, null, 0, s, null, null);
		statuses.remove(s);
		statusesChecked.remove(s);
		calcStats();
	}
	
	/**
	 * This checks if this schmuck is afflicted by a status.
	 * If so, the status is returned
	 */
	public Status getStatus(Class<? extends Status> s) {
		for (Status st : statuses) {
			if (st.getClass().equals(s)) {
				return st;
			}
		}
		for (Status st : statusesChecked) {
			if (st.getClass().equals(s)) {
				return st;
			}
		} 
		return null;
	}
	
	/**
	 * Whenever anything that could modify a schmuck's stats happens, we call this to recalc all of the unit' stats.
	 * This occurs when statuses are added/removed or equipment is equipped
	 */
	public void calcStats() {
		
		//Keep Hp% and fuel% constant in case of changing max values
		float hpPercent = currentHp / getStat(Stats.MAX_HP);
		float fuelPercent = currentFuel / getStat(Stats.MAX_FUEL);
		
		for (int i = 0; i < buffedStats.length; i++) {
			buffedStats[i] = baseStats[i];
		}
		statusProcTime(StatusProcTime.STAT_CHANGE, null, 0, null, currentTool, null);
		
		currentHp = hpPercent * getStat(Stats.MAX_HP);
		currentFuel = fuelPercent * getStat(Stats.MAX_FUEL);
		
		if (currentTool instanceof RangedWeapon) {
			((RangedWeapon) currentTool).setClipLeft();
			((RangedWeapon) currentTool).setAmmoLeft();
		}
	}
	
	/**
	 * This method is called when this schmuck receives damage.
	 * @param basedamage: amount of damage received
	 * @param knockback: amount of knockback to apply.
	 * @param perp: the schmuck who inflicted damage
	 * @param tool: the tool that was used to inflict this damage (null if noot tool-inflicted)
	 * @param procEffects: should this damage proc on-damage effects?
	 * @param tags: varargs of damage tags
	 */
	@Override
	public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Equipable tool, Boolean procEffects, DamageTypes... tags) {
		
		if (!schmuck.isAlive()) {
			return;
		}
		
		float damage = basedamage;
		
		damage -= basedamage * (getStat(Stats.DAMAGE_RES));
		damage += basedamage * (perp.getStat(Stats.DAMAGE_AMP));
		
		if (procEffects) {
			damage = perp.statusProcTime(StatusProcTime.DEAL_DAMAGE, this, damage, null, tool, null, tags);
			damage = statusProcTime(StatusProcTime.RECEIVE_DAMAGE, perp, damage, null, currentTool, null, tags);
		}
				
		currentHp -= damage;
		
		//Make shmuck flash upon receiving damage
		if (damage > 0 && schmuck.getFlashingCount() < -flashDuration) {
			schmuck.setFlashingCount(flashDuration);
			schmuck.impact.onForBurst(0.25f);
		}
		
		float kbScale = 1;
		
		kbScale -= getStat(Stats.KNOCKBACK_RES);
		kbScale += perp.getStat(Stats.KNOCKBACK_AMP);
		
		schmuck.applyLinearImpulse(knockback.scl(kbScale));
		
		//Give credit for kills to last schmuck (besides self) who damaged this schmuck
		if (!perp.equals(this)) {
			lastDamagedBy = perp;
		}
		
		if (currentHp <= 0) {
			currentHp = 0;
			die(lastDamagedBy, tool);
		}
		
		//charge on-damage active item
		if (perp instanceof PlayerBodyData) {
			if (((PlayerBodyData) perp).getActiveItem().getStyle().equals(chargeStyle.byDamage)) {
				((PlayerBodyData) perp).getActiveItem().gainCharge(damage);
			}
		}
	}
	
	/**
	 * This method is called when the schmuck is healed
	 * @param heal: amount of Hp to regenerate
	 * @param perp: the schmuck who healed
	 * @param procEffects: should this damage proc on-damage effects?
	 * @param tags: varargs of damage tags
	 */
	public void regainHp(float baseheal, BodyData perp, Boolean procEffects, DamageTypes... tags) {
		
		float heal = baseheal;
		
		if (procEffects) {
			heal = statusProcTime(StatusProcTime.ON_HEAL, this, heal, null, currentTool, null, tags);
		}
		
		currentHp += heal;
		if (currentHp >= getStat(Stats.MAX_HP)) {
			currentHp = getStat(Stats.MAX_HP);
		}
	}
	
	/**
	 * This method is called when the schmuck dies. Queue up to be deleted next engine tick.
	 */
	public void die(BodyData perp, Equipable tool) {
		if (schmuck.queueDeletion()) {
			perp.statusProcTime(StatusProcTime.ON_KILL, this, 0, null, tool, null);
			statusProcTime(StatusProcTime.ON_DEATH, perp, 0, null, currentTool, null);
		}		
	}
	
	public Schmuck getSchmuck() { return schmuck; }
		
	public float getCurrentHp() { return currentHp; }

	public void setCurrentHp(float currentHp) { this.currentHp = currentHp;	}

	public float getCurrentFuel() {	return currentFuel;	}

	public void setCurrentFuel(float currentFuel) {	this.currentFuel = currentFuel; }

	public float getStat(int index) { return buffedStats[index];	}
	
	/**
	 * Set a buffed stat for calcs. If hp or fuel, make sure the current amount does not exceed the max amount
	 * @param index
	 * @param amount
	 */
	public void setStat(int index, float amount) {
		buffedStats[index] = amount;
		
		if (index == Stats.MAX_HP) {
			currentHp = currentHp / buffedStats[index] * amount;
		}
		
		if (index == Stats.MAX_FUEL) {
			currentFuel = currentFuel / buffedStats[index] * amount;
		}
	}
	
	public Equipable getCurrentTool() { return currentTool; }	
	
	public void setCurrentTool(Equipable currentTool) { this.currentTool = currentTool; }
	
	public float getXGroundSpeed() { return maxGroundXSpeed * (1 + getStat(Stats.GROUND_SPD)); }
	
	public float getXAirSpeed() { return maxAirXSpeed * (1 + getStat(Stats.AIR_SPD)); }
	
	public float getXGroundAccel() { return groundXAccel * (1 + getStat(Stats.GROUND_ACCEL)); }
	
	public float getXAirAccel() { return airXAccel * (1 + getStat(Stats.AIR_ACCEL)); }
	
	public float getXGroundDeaccel() { return groundXDeaccel * (1 + getStat(Stats.GROUND_DRAG)); }
	
	public float getXAirDeaccel() {	return airXDeaccel * (1 + getStat(Stats.AIR_DRAG)); }
	
	public float getYGroundAccel() { return groundYAccel * (1 + getStat(Stats.GROUND_DRAG)); }
	
	public float getYAirAccel() { return airYAccel * (1 + getStat(Stats.AIR_DRAG)); }
	
	public float getYGroundDeaccel() { return groundYDeaccel * (1 + getStat(Stats.AIR_DRAG)); }
	
	public float getYAirDeaccel() {	return airYDeaccel * (1 + getStat(Stats.AIR_DRAG)); }
}
