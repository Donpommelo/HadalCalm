package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.PlayerClientOnHost;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.ProcTime.ReceiveHeal;
import com.mygdx.hadal.statuses.Status;

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

	public static final float BASE_CRIT_MULTIPLIER = 0.8f;
	public static final float BASE_MINI_CRIT_MULTIPLIER = 0.4f;

	public static final float ARMOR_1_MULTIPLIER = -0.2f;
	public static final float ARMOR_2_MULTIPLIER = -0.5f;
	public static final float ARMOR_3_MULTIPLIER = -0.8f;

	//The Schmuck that owns this data
	protected Schmuck schmuck;

	//schmuck stats
	private final float[] baseStats;
	private final float[] buffedStats;

	protected float currentHp, currentFuel;

	//statuses inflicted on the unit. statuses checked is used to recursively activate each status effect
	protected final Array<Status> statuses;
	protected final Array<Status> statusesChecked;
	
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
		
		this.baseStats = new float[55];
		this.buffedStats = new float[55];
		
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

			boolean proc = true;
			if (tempStatus.isServerOnly()) {
				proc = schmuck.getState().isServer();
			}

			if (tempStatus.isUserOnly()) {
				proc = schmuck.isOrigin();
			}

			//atm, clients only process stat-changing statuses or specifically designated statuses
			if (proc) {
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
			switch (s.getStackType()) {
			case ADD:
				added = true;
				break;
			case IGNORE:
				break;
			case REPLACE:
				old.setDuration(s.getDuration());
				statusProcTime(new ProcTime.BeforeStatusInfliction(old));
				break;
			case INCREMENT_DURATION:
				old.setDuration(old.getDuration() + s.getDuration());
				statusProcTime(new ProcTime.BeforeStatusInfliction(old));
				break;
			}
		} else {
			added = true;
		}
		
		if (added) {
			statusProcTime(new ProcTime.BeforeStatusInfliction(s));

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
		float hpPercent = Math.min(getStat(Stats.MAX_HP) == 0 ? 0 : currentHp / getStat(Stats.MAX_HP), 1.0f);
		float fuelPercent = Math.min(getStat(Stats.MAX_FUEL) == 0 ? 0 : currentFuel / getStat(Stats.MAX_FUEL), 1.0f);

		System.arraycopy(baseStats, 0, buffedStats, 0, buffedStats.length);
		statusProcTime(new ProcTime.StatCalc());

		//this is used for percentage based hp modifiers
		setStat(Stats.MAX_HP, getStat(Stats.MAX_HP) * (1.0f + getStat(Stats.MAX_HP_PERCENT)));

		currentHp = hpPercent * getStat(Stats.MAX_HP);
		currentFuel = fuelPercent * getStat(Stats.MAX_FUEL);
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

		//damage effects should be processed for all characters except clients on server who process them themselves
		boolean processDamageEffects = true;
		if (schmuck.getState().isServer()) {
			if (schmuck instanceof PlayerClientOnHost) {
				processDamageEffects = false;
			}
		} else {
			if (!(schmuck instanceof PlayerSelfOnClient)) {
				processDamageEffects = false;
			}
		}


		int crit = ((ProcTime.CalcInflictCrit) perp.statusProcTime(new ProcTime.CalcInflictCrit(0, this, hbox, source, tags))).crit;
		if (crit == 1) {
			damage *= (1 + BASE_MINI_CRIT_MULTIPLIER);
			schmuck.getDamageEffectHelper().addCritFlash();
		} else if (crit >= 2) {
			damage *= (1 + BASE_CRIT_MULTIPLIER);
			schmuck.getDamageEffectHelper().addCritFlash();
		} else {

			//armor is calculated
			int armor = ((ProcTime.CalcArmorReceive) statusProcTime(new ProcTime.CalcArmorReceive(0, damage, perp, hbox, source, tags))).armor;
			armor = ((ProcTime.CalcArmorInflict) statusProcTime(new ProcTime.CalcArmorInflict(armor, damage, perp, hbox, source, tags))).armor;

			if (armor == 1) {
				damage *= (1 + ARMOR_1_MULTIPLIER);
			} else if (armor == 2) {
				damage *= (1 + ARMOR_2_MULTIPLIER);
			} else if (armor >= 3) {
				damage *= (1 + ARMOR_3_MULTIPLIER);
			}
		}

		//proc effects and inflict damage
		if (procEffects) {
			damage = ((ProcTime.InflictDamage) perp.statusProcTime(new ProcTime.InflictDamage(damage, this, hbox, source, tags))).damage;
			damage = ((ProcTime.ReceiveDamage) statusProcTime(new ProcTime.ReceiveDamage(damage, perp, hbox, source, tags))).damage;
		}

		if (processDamageEffects) {

			damage = schmuck.getSpecialHpHelper().receiveShieldDamage(damage);

			currentHp -= damage;

			//apply knockback
			float kbScale = 1;
			kbScale -= Math.min(getStat(Stats.KNOCKBACK_RES), 1.0f);
			kbScale += perp.getStat(Stats.KNOCKBACK_AMP);
			schmuck.applyLinearImpulse(new Vector2(knockback).scl(kbScale));

			//Give credit for kills to last schmuck (besides self) who damaged this schmuck
			if (!perp.equals(this) && !perp.equals(schmuck.getState().getWorldDummy().getBodyData())) {
				lastDamagedBy = perp;
			}

			if (currentHp <= 0) {

				//this makes stat tracking not account for overkill damage
				damage += currentHp;

				currentHp = 0;

				//server processes death for all characters except clients who process it for themselves (and only themselves)
				if (schmuck.isOrigin()) {
					die(lastDamagedBy, source, tags);
				}
			}
		}

		//Make schmuck flash upon receiving damage
		if (damage > 0 && schmuck.getShaderHelper().getShaderStaticCount() < -Constants.FLASH) {
			schmuck.getShaderHelper().setStaticShader(Shader.WHITE, Constants.FLASH);
			if (null != schmuck.impact) {
				schmuck.impact.onForBurst(0.25f);
			}
		}

		//charge on-damage active item
		if (perp instanceof PlayerBodyData perpData) {
			if (GameMode.CAMPAIGN.equals(schmuck.getState().getMode())) {

				//active item charges less against non-player enemies
				if (this instanceof PlayerBodyData) {
					perpData.getPlayer().getMagicHelper().getMagic().gainCharge(damage * ActiveItem.DAMAGE_CHARGE_MULTIPLIER);
				} else {
					perpData.getPlayer().getMagicHelper().getMagic().gainCharge(damage * ActiveItem.DAMAGE_CHARGE_MULTIPLIER * ActiveItem.ENEMY_DAMAGE_CHARGE_MULTIPLIER);
				}
			}

			if (perpData.getPlayer().getUser() != null) {

				//play on-hit sounds. pitched up automatically if fatal. No sounds for self or friendly fire.
				perpData.getPlayer().getHitsoundHelper().playHitSound(this, damage);

				//increment tracked stats
				perpData.getPlayer().getUser().getStatsManager().receiveDamage(perp.getSchmuck(), schmuck, damage);
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
			//set death info to be sent to clients once death is processed
			schmuck.setDamageSource(source);
			schmuck.setDamageTags(tags);
			schmuck.setPerpID(perp.getSchmuck().getEntityID());

			perp.statusProcTime(new ProcTime.Kill(this, source, tags));
			statusProcTime(new ProcTime.Death(perp, source, tags));
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
