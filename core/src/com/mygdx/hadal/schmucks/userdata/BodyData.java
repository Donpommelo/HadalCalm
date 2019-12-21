package com.mygdx.hadal.schmucks.userdata;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
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
	
	/**
	 * Stats:
	 * 0: Max Hp
	 * 1: Max Fuel
	 * 2: Hp Regeneration per Second
	 * 3: Fuel Regeneration per Second
	 * 4: Ground Speed Modification
	 * 5: Air Speed Modification
	 * 6: Ground Acceleration Modification
	 * 7: Air Acceleration Modification
	 * 8: Ground Drag Reduction
	 * 9: Air Drag Reduction
	 * 10: Bonus Jump Power
	 * 11: Bonus Jump Number
	 * 12: Bonus Hover Power
	 * 13: Hover Cost
	 * 14: Bonus Airblast Power
	 * 15: Airblast Cost
	 * 16: Bonus Airblast Recoil
	 * 17: Bonus Airblast Size
	 * 18: Active Item Charge Rate
	 * 19: Active Item Max Charge
	 * 20: Active Item Power !
	 * 21: Universal Damage Amplification
	 * 22: Universal Damage Reduction
	 * 23: Universal Knockback on Hit (to others)
	 * 24: Universal Knockback Resistance (to self)
	 * 25: Universal Tool-Use Speed
	 * 26: Ranged Damage on Hit
	 * 27: Ranged Fire Rate
	 * 28: Ranged Reload Rate
	 * 29: Ranged Clip Size
	 * 30: Ranged Projectile Speed
	 * 31: Ranged Projectile Size
	 * 32: Ranged Projectile Gravity
	 * 33: Ranged Projectile Lifespan
	 * 34: Ranged Projectile Durability
	 * 35: Ranged Projectile Bounciness
	 * 36: Ranged Recoil
	 * 37: Melee Damage on Hit
	 * 38: Melee Swing Speed
	 * 39: Melee Swing Delay
	 * 40: Melee Swing Interval
	 * 41: Melee Range
	 * 42: Melee Arc Size
	 * 43: Melee Momentum on Swing
	 */
	
	private float[] baseStats;
	private float[] buffedStats;	
	
	//Speed on ground
	private float maxGroundXSpeed = 15.0f;
	private float maxAirXSpeed = 10.0f;
		
	//Accelerating on the ground/air
	private float groundXAccel = 0.10f;
	private float airXAccel = 0.05f;
	private float groundXDeaccel = 0.05f;
	private float airXDeaccel = 0.01f;
	
	private float groundYAccel = 0.10f;
	private float airYAccel = 0.50f;
	private float groundYDeaccel = 0.05f;
	private float airYDeaccel = 0.01f;
	
	//Hp and regen
	private float hpRegen = 0.0f;
	
	private int maxFuel = 100;
	private float fuelRegen = 8.0f;
	
	protected float currentHp, currentFuel;

	private final static float flashDuration = 0.08f;
	
	protected ArrayList<Status> statuses;
	protected ArrayList<Status> statusesChecked;	
	
	protected Equipable currentTool;
	
	//This is the last schumck who damaged this entity. Used for kill credit
	private BodyData lastDamagedBy;
	
	/**
	 * This is created upon the create() method of any schmuck.
	 * Schmucks are the Body data type.
	 * @param world
	 * @param schmuck
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
	
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
				
		float finalAmount = amount;
		ArrayList<Status> oldChecked = new ArrayList<Status>();
		for(Status s : this.statusesChecked){
			this.statuses.add(0,s);
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
		
		for(Status s : this.statusesChecked){
			if(!oldChecked.contains(s)){
				this.statuses.add(s);
			}
		}
		this.statusesChecked.clear();
		for(Status s : oldChecked){
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
	
	public void removeStatus(Status s) {
		statusProcTime(StatusProcTime.ON_REMOVE, null, 0, s, null, null);
		statuses.remove(s);
		statusesChecked.remove(s);
		calcStats();
	}
	
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
	
	public void calcStats() {
		
		//Keep Hp% constant in case of changing max hp
		float hpPercent = currentHp / getStat(Stats.MAX_HP);

		for (int i = 0; i < buffedStats.length; i++) {
			buffedStats[i] = baseStats[i];
		}
		statusProcTime(StatusProcTime.STAT_CHANGE, null, 0, null, currentTool, null);
		
		currentHp = hpPercent * getStat(Stats.MAX_HP);
		
		if (currentTool instanceof RangedWeapon) {
			((RangedWeapon) currentTool).setClipLeft();
			((RangedWeapon) currentTool).setAmmoLeft();
		}
	}
	
	/**
	 * This method is called when this schmuck receives damage.
	 * @param basedamage: amount of damage received
	 * @param knockback: amount of knockback to apply.
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
	}
	
	/**
	 * This method is called when the schmuck is healed
	 * @param heal: amount of Hp to regenerate
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
	
	public Schmuck getSchmuck() {
		return schmuck;
	}
		
	public float getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(float currentHp) {
		this.currentHp = currentHp;
	}

	public float getCurrentFuel() {
		return currentFuel;
	}

	public void setCurrentFuel(float currentFuel) {
		this.currentFuel = currentFuel;
	}

	public float getStat(int index) {
		return buffedStats[index];
	}
	
	public void setStat(int index, float amount) {
		buffedStats[index] = amount;
		
		if (index == Stats.MAX_HP) {
			currentHp = currentHp / buffedStats[index] * amount;
		}
		
		if (index == Stats.MAX_FUEL) {
			currentFuel = currentFuel / buffedStats[index] * amount;
		}
	}
	
	public Equipable getCurrentTool() {
		return currentTool;
	}	
	
	public void setCurrentTool(Equipable currentTool) {
		this.currentTool = currentTool;
	}
	
	public float getXGroundSpeed() {
		return maxGroundXSpeed * (1 + getStat(Stats.GROUND_SPD));
	}
	
	public float getXAirSpeed() {
		return maxAirXSpeed * (1 + getStat(Stats.AIR_SPD));
	}
	
	public float getXGroundAccel() {
		return groundXAccel * (1 + getStat(Stats.GROUND_ACCEL));
	}
	
	public float getXAirAccel() {
		return airXAccel * (1 + getStat(Stats.AIR_ACCEL));
	}
	
	public float getXGroundDeaccel() {
		return groundXDeaccel * (1 + getStat(Stats.GROUND_DRAG));
	}
	
	public float getXAirDeaccel() {
		return airXDeaccel * (1 + getStat(Stats.AIR_DRAG));
	}
	
	public float getYGroundAccel() {
		return groundYAccel * (1 + getStat(Stats.GROUND_DRAG));
	}
	
	public float getYAirAccel() {
		return airYAccel * (1 + getStat(Stats.AIR_DRAG));
	}
	
	public float getYGroundDeaccel() {
		return groundYDeaccel * (1 + getStat(Stats.AIR_DRAG));
	}
	
	public float getYAirDeaccel() {
		return airYDeaccel * (1 + getStat(Stats.AIR_DRAG));
	}
}
