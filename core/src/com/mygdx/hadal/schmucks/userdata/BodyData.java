package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.ProcTime.InflictDamage;
import com.mygdx.hadal.statuses.ProcTime.ReceiveDamage;
import com.mygdx.hadal.statuses.ProcTime.ReceiveHeal;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;

/**
 * Body data contains the stats and methods of any unit; player or enemy.
 * @author Pangosteen Placerola
 */
public class BodyData extends HadalData {

	//Speed on ground
	private static final float MAX_GROUND_X_SPEED = 15.0f;
	private static final float MAX_AIR_X_SPEED = 10.0f;
		
	//Accelerating on the ground/air
	private static final float GROUND_X_ACCEL = 0.11f;
	private static final float AIR_X_ACCEL = 0.07f;
	private static final float GROUND_X_DEACCEL = 0.1f;
	private static final float AIR_X_DEACCEL = 0.01f;
	
	private static final float GROUND_Y_ACCEL = 0.1f;
	private static final float AIR_Y_ACCEL = 0.15f;
	private static final float GROUND_Y_DEACCEL = 0.05f;
	private static final float AIR_Y_DEACCEL = 0.01f;

	//Hp and regen
	private static final float HP_REGEN = 0.0f;
	private static final int MAX_FUEL = 100;

	//variance multiplier applied to every instance of damage
	private static final float DAMAGE_VARIANCE = 0.1f;

	//The Schmuck that owns this data
	protected Schmuck schmuck;

	//schmuck stats
	private final float[] baseStats;
	private final float[] buffedStats;

	protected float currentHp, currentFuel;

	//statuses inflicted on the unit. statuses checked is used to recursively activate each status effect
	protected final Array<Status> statuses;
	protected final Array<Status> statusesChecked;
	
	//the currently equipped tool
	protected Equippable currentTool;
	
	//This is the last schmuck who damaged this entity. Used for kill credit
	private BodyData lastDamagedBy;
	
	/**
	 * This is created upon the create() method of any schmuck.
	 * Schmucks are the Body data type.
	 * @param schmuck: the entity that has this data
	 * @param maxHp: the unit's hp
	 */
	public BodyData(Schmuck schmuck, float maxHp) {
		super(UserDataType.BODY, schmuck);
		this.schmuck = schmuck;	
		
		this.baseStats = new float[52];
		this.buffedStats = new float[52];
		
		baseStats[0] = maxHp;
		baseStats[1] = MAX_FUEL;
		baseStats[2] = HP_REGEN;

		this.statuses = new Array<>();
		this.statusesChecked = new Array<>();
		
		calcStats();

		currentHp = getStat(Stats.MAX_HP);
		currentFuel = getStat(Stats.MAX_FUEL);
		
		lastDamagedBy = schmuck.getState().getWorldDummy().getBodyData();
	}
	
	/**
	 * Status proc time is called at certain points of the game that could activate any effect.
	 * @param o: the type of proc time that this is
	 * This fields of this are the various info needed for each status. fields will be null when unused
	 * @return a ProcTime for certain statuses that pass along a modified value (like on damage effects)
	 */
	public ProcTime statusProcTime(ProcTime o) {
		ProcTime finalProcTime = o;

		Array<Status> oldChecked = new Array<>();
		for (Status s : this.statusesChecked) {
			this.statuses.insert(0, s);
			oldChecked.add(s);
		}
		this.statusesChecked.clear();

		//sorting statuses makes status priority work properly
		statuses.sort();

		while (!this.statuses.isEmpty()) {
			Status tempStatus = this.statuses.get(0);

			//atm, clients only process stat-changing statuses or specifically designated statuses
			if (schmuck.getState().isServer() || o instanceof ProcTime.StatCalc || o instanceof ProcTime.Render || tempStatus.isClientIndependent()) {
				finalProcTime = tempStatus.statusProcTime(o);
			}

			if (this.statuses.contains(tempStatus, false)) {
				this.statuses.removeValue(tempStatus, false);
				this.statusesChecked.add(tempStatus);
			}
		}
		
		for (Status s : this.statusesChecked) {
			if (!oldChecked.contains(s, false)) {
				this.statuses.add(s);
			}
		}
		this.statusesChecked.clear();
		this.statusesChecked.addAll(oldChecked);

		return finalProcTime;		
	}
	
	/**
	 * Add a status to this schmuck
	 * @param s: Status to add
	 */
	public void addStatus(Status s) {

		boolean added = false;
		
		//in the case of re-adding a status, the behavior depends on the status' stack type
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
			case INCREMENT_DURATION:
				old.setDuration(old.getDuration() + s.getDuration());
				break;
			}
		} else {
			added = true;
		}
		
		if (added) {
			statuses.add(s);
			s.onInflict();
			calcStats();
		}
	}
	
	/**
	 * Removes a status from this schmuck
	 */
	public void removeStatus(Status s) {
		s.onRemove();
		statuses.removeValue(s, false);
		statusesChecked.removeValue(s, false);
		calcStats();
	}
	
	/**
	 * Removes an artifact status from this schmuck
	 */
	public void removeArtifactStatus(UnlockArtifact artifact) {

		Array<Status> toRemove = new Array<>();
		
		for (Status s : statuses) {
			if (s.getArtifact() != null) {
				if (s.getArtifact().equals(artifact)) {
					toRemove.add(s);
				}
			}
		}
		for (Status s : statusesChecked) {
			if (s.getArtifact() != null) {
				if (s.getArtifact().equals(artifact)) {
					toRemove.add(s);
				}
			}
		}
		for (Status s : toRemove) {
			removeStatus(s);
		}
		calcStats();
	}
	
	/**
	 * This checks if this schmuck is afflicted by a status.
	 * If so, the status is returned. Otherwise return null
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
		float hpPercent = getStat(Stats.MAX_HP) == 0 ? 0 : currentHp / getStat(Stats.MAX_HP);
		float fuelPercent = getStat(Stats.MAX_FUEL) == 0 ? 0 : currentFuel / getStat(Stats.MAX_FUEL);

		System.arraycopy(baseStats, 0, buffedStats, 0, buffedStats.length);
		statusProcTime(new ProcTime.StatCalc());

		//this is used for percentage based hp modifiers
		setStat(Stats.MAX_HP, getStat(Stats.MAX_HP) * (1.0f + getStat(Stats.MAX_HP_PERCENT)));

		currentHp = hpPercent * getStat(Stats.MAX_HP);
		currentFuel = fuelPercent * getStat(Stats.MAX_FUEL);
		
		if (currentTool instanceof RangedWeapon ranged) {
			ranged.setClipLeft();
			ranged.setAmmoLeft();
		}
	}
	
	/**
	 * This method is called when this schmuck receives damage.
	 * @param baseDamage : amount of damage received
	 * @param knockback : amount of knockback to apply.
	 * @param perp : the schmuck who inflicted damage
	 * @param procEffects : should this damage proc on-damage effects?
	 * @param hbox: hbox that inflicted the damage. Null if not inflicted by a hbox
	 * @param source : attack/weapon source of this damage
	 * @param tags : damage tags used for type-specific damage resistance/amplification
	 */
	@Override
	public float receiveDamage(float baseDamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
							   DamageSource source, DamageTag... tags) {
		if (!schmuck.isAlive()) { return 0.0f; }
		
		//calculate damage
		float damage = baseDamage;
		damage -= baseDamage * (getStat(Stats.DAMAGE_RES));
		damage += baseDamage * (perp.getStat(Stats.DAMAGE_AMP));
		damage += baseDamage * (-DAMAGE_VARIANCE + MathUtils.random() * 2 * DAMAGE_VARIANCE);
		
		//proc effects and inflict damage
		if (procEffects) {
			damage = ((InflictDamage) perp.statusProcTime(new ProcTime.InflictDamage(damage, this, hbox, source, tags))).damage;
			damage = ((ReceiveDamage) statusProcTime(new ProcTime.ReceiveDamage(damage, perp, hbox, source, tags))).damage;
		}
		currentHp -= damage;
		
		//apply knockback
		float kbScale = 1;
		kbScale -= Math.min(getStat(Stats.KNOCKBACK_RES), 1.0f);
		kbScale += perp.getStat(Stats.KNOCKBACK_AMP);
		schmuck.applyLinearImpulse(new Vector2(knockback).scl(kbScale));

		if (schmuck.getState().isServer()) {

			//Give credit for kills to last schmuck (besides self) who damaged this schmuck
			if (!perp.equals(this) && !perp.equals(schmuck.getState().getWorldDummy().getBodyData())) {
				lastDamagedBy = perp;
			}

			//Make schmuck flash upon receiving damage
			if (damage > 0 && schmuck.getShaderCount() < -Constants.FLASH) {
				schmuck.setShader(Shader.WHITE, Constants.FLASH, true);
				schmuck.impact.onForBurst(0.25f);
			}

			if (currentHp <= 0) {

				//this makes stat tracking not account for overkill damage
				damage += currentHp;

				currentHp = 0;
				die(lastDamagedBy, source, tags);
			}

			//charge on-damage active item
			if (perp instanceof PlayerBodyData perpData) {
				if (GameMode.CAMPAIGN.equals(schmuck.getState().getMode())) {
					
					//active item charges less against non-player enemies
					if (this instanceof PlayerBodyData) {
						perpData.getActiveItem().gainCharge(damage * ActiveItem.DAMAGE_CHARGE_MULTIPLIER);
					} else {
						perpData.getActiveItem().gainCharge(damage * ActiveItem.DAMAGE_CHARGE_MULTIPLIER * ActiveItem.ENEMY_DAMAGE_CHARGE_MULTIPLIER);
					}
				}

				if (perpData.getPlayer().getUser() != null) {
					SavedPlayerFieldsExtra field = perpData.getPlayer().getUser().getScoresExtra();
					//play on-hit sounds. pitched up automatically if fatal. No sounds for self or friendly fire.
					if (perp.getSchmuck().getHitboxfilter() != schmuck.getHitboxfilter()) {
						if (currentHp == 0) {
							perpData.getPlayer().playHitSound(999);
						} else {
							perpData.getPlayer().playHitSound(damage);
						}

						//track perp's damage dealt
						if (field != null && damage > 0.0f) {
							field.incrementDamageDealt(damage);
						}

					} else {
						if (field != null && damage > 0.0f) {
							if (perp.getSchmuck().equals(schmuck)) {
								field.incrementDamageDealtSelf(damage);
							} else {
								field.incrementDamageDealtAllies(damage);
							}
						}
					}
				}
			}
		}
		return damage;
	}
	
	/**
	 * This method is called when the schmuck is healed
	 * @param baseheal: amount of Hp to regenerate
	 * @param perp: the schmuck who healed
	 * @param procEffects: should this damage proc on-damage effects?
	 * @param tags: varargs of damage tags
	 */
	public void regainHp(float baseheal, BodyData perp, Boolean procEffects, DamageTag... tags) {
		if (!schmuck.getState().isServer()) { return; }

		float heal = baseheal;
		
		if (procEffects) {
			heal = ((ReceiveHeal) statusProcTime(new ProcTime.ReceiveHeal(heal, perp, tags))).heal;
		}
		
		//prevent overheal
		currentHp += heal;
		if (currentHp >= getStat(Stats.MAX_HP)) {
			currentHp = getStat(Stats.MAX_HP);
		}
	}
	
	/**
	 * This method is called when the schmuck dies. Queue up to be deleted next engine tick.
	 * @param tags: the tags that apply to the fatal damage instance
	 */
	public void die(BodyData perp, DamageSource source, DamageTag... tags) {
		if (schmuck.queueDeletion()) {
			perp.statusProcTime(new ProcTime.Kill(this, source));
			statusProcTime(new ProcTime.Death(perp, source));
		}		
	}
	
	public Schmuck getSchmuck() { return schmuck; }
		
	public float getCurrentHp() { return currentHp; }

	public void setCurrentHp(float currentHp) { this.currentHp = currentHp;	}

	public float getCurrentFuel() {	return currentFuel;	}

	public void setCurrentFuel(float currentFuel) { this.currentFuel = currentFuel;	}

	public float getStat(int index) { return buffedStats[index]; }
	
	/**
	 * Set a buffed stat for calcs. If hp or fuel, make sure the current amount does not exceed the max amount
	 * @param index: the number of the stat being modified
	 * @param amount: the amount to modify the stat by
	 */
	public void setStat(int index, float amount) {
		buffedStats[index] = amount;
		
		//prevent overheal and overfuel
		if (index == Stats.MAX_HP) {
			currentHp = currentHp / buffedStats[index] * amount;
		}
		if (index == Stats.MAX_FUEL) {
			currentFuel = currentFuel / buffedStats[index] * amount;
		}
	}
	
	public Equippable getCurrentTool() { return currentTool; }
	
	public void setCurrentTool(Equippable currentTool) { this.currentTool = currentTool; }
	
	public float getXGroundSpeed() { return MAX_GROUND_X_SPEED * (1 + getStat(Stats.GROUND_SPD)); }
	
	public float getXAirSpeed() { return MAX_AIR_X_SPEED * (1 + getStat(Stats.AIR_SPD)); }
	
	public float getXGroundAccel() { return GROUND_X_ACCEL * (1 + getStat(Stats.GROUND_ACCEL)); }
	
	public float getXAirAccel() { return AIR_X_ACCEL * (1 + getStat(Stats.AIR_ACCEL)); }
	
	public float getXGroundDeaccel() { return GROUND_X_DEACCEL * (1 + getStat(Stats.GROUND_DRAG)); }
	
	public float getXAirDeaccel() {	return AIR_X_DEACCEL * (1 + getStat(Stats.AIR_DRAG)); }
	
	public float getYGroundAccel() { return GROUND_Y_ACCEL * (1 + getStat(Stats.GROUND_ACCEL)); }
	
	public float getYAirAccel() { return AIR_Y_ACCEL * (1 + getStat(Stats.AIR_ACCEL)); }
	
	public float getYGroundDeaccel() { return GROUND_Y_DEACCEL * (1 + getStat(Stats.GROUND_DRAG)); }
	
	public float getYAirDeaccel() {	return AIR_Y_DEACCEL * (1 + getStat(Stats.AIR_DRAG)); }
}
