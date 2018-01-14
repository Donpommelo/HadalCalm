package com.mygdx.hadal.schmucks.userdata;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.ai.Zone;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

/**
 * Body data contains the stats and methods of any unit; player or enemy.
 * @author Zachary Tu
 *
 */
public class BodyData extends HadalData {

	//The Schmuck that owns this data
	public Schmuck schmuck;
	
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
	 * 14: Bonus Airblast Power !
	 * 15: Airblast Cost
	 * 16: Bonus Airblast Recoil !
	 * 17: Bonus Airblast Size !
	 * 18: Momentum Freeze Amplification!
	 * 19: Momentum Freeze Size!
	 * 20: Momentum Freeze Cooldown Reduction
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
	 * 44: 


	 */
	
	public float[] baseStats;
	public float[] buffedStats;	
	
	//Speed on ground
	public float maxGroundXSpeed = 15.0f;
	public float maxAirXSpeed = 10.0f;
	
	//Speed on ground
	public float maxGroundYSpeed = 10.0f;
	public float maxAirYSpeed = 7.5f;
		
	//Accelerating on the ground/air
	public float groundXAccel = 0.10f;
	public float airXAccel = 0.05f;
	public float groundXDeaccel = 0.05f;
	public float airXDeaccel = 0.01f;
	
	public float groundYAccel = 0.10f;
	public float airYAccel = 0.50f;
	public float groundYDeaccel = 0.05f;
	public float airYDeaccel = 0.01f;
	
	//Hp and regen
	public int maxHp = 100;
	public float hpRegen = 0.0f;
	
	public int maxFuel = 100;
	public float fuelRegen = 5.0f;
	
	public float currentHp, currentFuel;

	public Zone currentZone;
	
	public ArrayList<Status> statuses;
	public ArrayList<Status> statusesChecked;
	
	/**
	 * This is created upon the create() method of any schmuck.
	 * Schmucks are the Body data type.
	 * @param world
	 * @param schmuck
	 */
	public BodyData(World world, Schmuck schmuck) {
		super(world, UserDataTypes.BODY, schmuck);
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

		currentHp = getMaxHp();
		currentFuel = getMaxHp();		
	}
	
	
	
	public float statusProcTime(int procTime, BodyData schmuck, float amount, Status status) {
		float finalAmount = amount;
		ArrayList<Status> oldChecked = new ArrayList<Status>();
		for(Status s : this.statusesChecked){
			this.statuses.add(0,s);
			oldChecked.add(s);
		}
		this.statusesChecked.clear();
		
		while(!this.statuses.isEmpty()) {
			Status tempStatus = this.statuses.get(0);
			switch(procTime) {
			case 0:
				tempStatus.statChanges(this);
				break;
			case 1:
				finalAmount = tempStatus.onDealDamage(finalAmount, schmuck);
				break;
			case 2:
				finalAmount = tempStatus.onReceiveDamage(finalAmount, schmuck);
				break;
			case 3:
				tempStatus.timePassing(amount);
				break;
			}
			
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
	
	public void addStatus(Status s) {
		statuses.add(s);
		calcStats();
	}
	
	public void removeStatus(Status s) {
		statuses.remove(s);
		calcStats();
	}
	
	public void calcStats() {
		for (int i = 0; i < buffedStats.length; i++) {
			buffedStats[i] = baseStats[i];
		}
		statusProcTime(0, this, 0, null);
	}
	
	/**
	 * This method is called when this schmuck receives damage.
	 * @param basedamage: amount of damage received
	 * @param knockback: amount of knockback to apply.
	 *TODO: include the source of damage
	 */
	public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, DamageTypes... tags) {
		
		float damage = basedamage;
		
		damage -= basedamage * (getDamageReduc());
		damage += basedamage * (perp.getDamageAmp());
		
		if (Arrays.asList(tags).contains(DamageTypes.RANGED)) {
			damage *= (1 + perp.getBonusRangedDamage());
		}
		
		if (Arrays.asList(tags).contains(DamageTypes.MELEE)) {
			damage *= (1 + perp.getBonusMeleeDamage());
		}
		
		damage = perp.statusProcTime(1, perp, damage, null);
		damage = statusProcTime(2, this, damage, null);
		
		currentHp -= damage;
		
		float kbScale = 1;
		
		kbScale -= getKnockbackReduc();
		kbScale += perp.getKnockbackAmp();
		
		schmuck.getBody().applyLinearImpulse(knockback.scl(kbScale), schmuck.getBody().getLocalCenter(), true);
		if (currentHp <= 0) {
			currentHp = 0;
			die();
		}
	}
	
	/**
	 * This method is called when the schmuck is healed
	 * @param heal: amount of Hp to regenerate
	 */
	public void regainHp(float heal) {
		currentHp += heal;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
	}
	
	/**
	 * This method is called when the schmuck dies. Queue up to be deleted next engine tick.
	 */
	public void die() {
		schmuck.queueDeletion();
	}

	public Schmuck getSchmuck() {
		return schmuck;
	}
	
	public float getMaxHp() {
		return buffedStats[0];
	}
	
	public void setMaxHp(float buff) {
		float Hp = currentHp / buffedStats[0];
		buffedStats[0] = buff;
		currentHp = Hp * buff;
	}
	
	public float getMaxFuel() {
		return buffedStats[1];
	}
	
	public void setMaxFuel(float buff) {
		float fuel = currentFuel / buffedStats[1];
		buffedStats[1] = buff;
		currentHp = fuel * buff;
	}
	
	public float getHpRegen() {
		return buffedStats[2];
	}
	
	public void setHpRegen(float buff) {
		buffedStats[2] = buff;
	}
	
	public float getFuelRegen() {
		return buffedStats[3];
	}
	
	public void setFuelRegen(float buff) {
		buffedStats[3] = buff;
	}
	
	public float getBonusGroundSpeed() {
		return buffedStats[4];
	}
	
	public void setBonusGroundSpeed(float buff) {
		buffedStats[4] = buff;
	}
	
	public float getBonusAirSpeed() {
		return buffedStats[5];
	}
	
	public void setBonusAirSpeed(float buff) {
		buffedStats[5] = buff;
	}
	
	public float getBonusGroundAccel() {
		return buffedStats[6];
	}
	
	public void setBonusGroundAccel(float buff) {
		buffedStats[6] = buff;
	}
	
	public float getBonusAirAccel() {
		return buffedStats[7];
	}
	
	public void setBonusAirAccel(float buff) {
		buffedStats[7] = buff;
	}
	
	public float getBonusGroundDrag() {
		return buffedStats[8];
	}
	
	public void setBonusGroundDrag(float buff) {
		buffedStats[8] = buff;
	}
	
	public float getBonusAirDrag() {
		return buffedStats[9];
	}
	
	public void setBonusAirDrag(float buff) {
		buffedStats[9] = buff;
	}
	
	public float getBonusJumpPower() {
		return buffedStats[10];
	}
	
	public void setBonusJumpPower(float buff) {
		buffedStats[10] = buff;
	}
	
	public float getBonusJumpNum() {
		return buffedStats[11];
	}
	
	public void setBonusJumpNum(float buff) {
		buffedStats[11] = buff;
	}
	
	public float getBonusHoverPower() {
		return buffedStats[12];
	}
	
	public void setBonusHoverPower(float buff) {
		buffedStats[12] = buff;
	}
	
	public float getBonusHoverCost() {
		return buffedStats[13];
	}
	
	public void setBonusHoverCost(float buff) {
		buffedStats[13] = buff;
	}
	
	public float getBonusAirblastPower() {
		return buffedStats[14];
	}
	
	public void setBonusAirblastPower(float buff) {
		buffedStats[14] = buff;
	}
	
	public float getBonusAirblastCost() {
		return buffedStats[15];
	}
	
	public void setBonusAirblastCost(float buff) {
		buffedStats[15] = buff;
	}
	
	public float getBonusAirblastRecoil() {
		return buffedStats[16];
	}
	
	public void setBonusAirblastRecoil(float buff) {
		buffedStats[16] = buff;
	}
	
	public float getBonusAirblastSize() {
		return buffedStats[17];
	}
	
	public void setBonusAirblastSize(float buff) {
		buffedStats[17] = buff;
	}
	
	public float getBonusMomentumAmp() {
		return buffedStats[18];
	}
	
	public void setBonusMomentumAmp(float buff) {
		buffedStats[18] = buff;
	}
	
	public float getBonusMomentumSize() {
		return buffedStats[19];
	}
	
	public void setBonusMomentumSize(float buff) {
		buffedStats[19] = buff;
	}
	
	public float getBonusMomentumCd() {
		return buffedStats[20];
	}
	
	public void setBonusMomentumCd(float buff) {
		buffedStats[20] = buff;
	}
	
	public float getDamageAmp() {
		return buffedStats[21];
	}
	
	public void setDamageAmp(float buff) {
		buffedStats[21] = buff;
	}
	
	public float getDamageReduc() {
		return buffedStats[22];
	}
	
	public void setDamageReduc(float buff) {
		buffedStats[22] = buff;
	}
	
	public float getKnockbackAmp() {
		return buffedStats[23];
	}
	
	public void setKnockbackAmp(float buff) {
		buffedStats[23] = buff;
	}
	
	public float getKnockbackReduc() {
		return buffedStats[24];
	}
	
	public void setKnockbackReduc(float buff) {
		buffedStats[24] = buff;
	}
	
	public float getToolCdReduc() {
		return buffedStats[25];
	}
	
	public void setToolCdReduc(float buff) {
		buffedStats[25] = buff;
	}
	
	public float getBonusRangedDamage() {
		return buffedStats[26];
	}
	
	public void setBonusRangedDamage(float buff) {
		buffedStats[26] = buff;
	}
	
	public float getRangedFireRate() {
		return buffedStats[27];
	}
	
	public void setRangedFireRate(float buff) {
		buffedStats[27] = buff;
	}
	
	public float getReloadRate() {
		return buffedStats[28];
	}
	
	public void setReloadRate(float buff) {
		buffedStats[28] = buff;
	}
	
	public float getBonusClipSize() {
		return buffedStats[29];
	}
	
	public void setBonusClipSize(float buff) {
		buffedStats[29] = buff;
	}
	
	public float getProjectileSpeed() {
		return buffedStats[30];
	}
	
	public void setProjectileSpeed(float buff) {
		buffedStats[30] = buff;
	}
	
	public float getProjectileSize() {
		return buffedStats[31];
	}
	
	public void setProjectileSize(float buff) {
		buffedStats[31] = buff;
	}
	
	public float getProjectileGravity() {
		return buffedStats[32];
	}
	
	public void setProjectileGravity(float buff) {
		buffedStats[32] = buff;
	}
	
	public float getProjectileLifespan() {
		return buffedStats[33];
	}
	
	public void setProjectileLifespan(float buff) {
		buffedStats[33] = buff;
	}
	
	public float getProjectileDurability() {
		return buffedStats[34];
	}
	
	public void setProjectileDurability(float buff) {
		buffedStats[34] = buff;
	}
	
	public float getProjectileBounciness() {
		return buffedStats[35];
	}
	
	public void setProjectileBounciness(float buff) {
		buffedStats[35] = buff;
	}
	
	public float getBonusRecoil() {
		return buffedStats[36];
	}
	
	public void setBonusRecoil(float buff) {
		buffedStats[36] = buff;
	}
	
	public float getBonusMeleeDamage() {
		return buffedStats[37];
	}
	
	public void setBonusMeleeDamage(float buff) {
		buffedStats[37] = buff;
	}
	
	public float getMeleeSwingRate() {
		return buffedStats[38];
	}
	
	public void getMeleeSwingRate(float buff) {
		buffedStats[38] = buff;
	}
	
	public float getMeleeSwingDelay() {
		return buffedStats[39];
	}
	
	public void getMeleeSwingDelay(float buff) {
		buffedStats[39] = buff;
	}
	
	public float getMeleeSwingInterval() {
		return buffedStats[40];
	}
	
	public void getMeleeSwingInterval(float buff) {
		buffedStats[40] = buff;
	}
	
	public float getMeleeRange() {
		return buffedStats[41];
	}
	
	public void getMeleeRange(float buff) {
		buffedStats[41] = buff;
	}
	
	public float getMeleeArcSize() {
		return buffedStats[42];
	}
	
	public void getMeleeArcSize(float buff) {
		buffedStats[42] = buff;
	}
	
	public float getMeleeMomentum() {
		return buffedStats[43];
	}
	
	public void getMeleeMomentum(float buff) {
		buffedStats[43] = buff;
	}
}
