package com.mygdx.hadal.users;

import com.mygdx.hadal.schmucks.entities.Schmuck;

/**
 * This class contains all the fields that are tracked during a match.
 * These are synced when the results state appears and let clients display information about each player's performance.
 * @author Shimpernickel Slatatron
 *
 */
public class StatsManager {

	//tracked stats for a player
	private float damageDealtEnemies, damageDealtAllies, damageDealtSelf, damageReceived;

	public StatsManager() {}

	public void receiveDamage(Schmuck perp, Schmuck vic, float damage) {
		if (perp.getHitboxFilter() != vic.getHitboxFilter()) {

			//track perp's damage dealt
			if (damage > 0.0f) {
				incrementDamageDealt(damage);
			}

		} else {
			if (damage > 0.0f) {
				if (perp.equals(vic)) {
					incrementDamageDealtSelf(damage);
				} else {
					incrementDamageDealtAllies(damage);
				}
			}
		}
	}

	public void newLevelReset() {
		damageDealtEnemies = 0.0f;
		damageDealtAllies = 0.0f;
		damageDealtSelf = 0.0f;
		damageReceived = 0.0f;
	}

	public float getDamageDealt() { return damageDealtEnemies; }

	public void incrementDamageDealt(float damageDealt) { this.damageDealtEnemies += damageDealt; }
	
	public float getDamageDealtSelf() { return damageDealtSelf; }

	public void incrementDamageDealtSelf(float damageDealt) { this.damageDealtSelf += damageDealt; }
	
	public float getDamageDealtAllies() { return damageDealtAllies; }

	public void incrementDamageDealtAllies(float damageDealt) { this.damageDealtAllies += damageDealt; }

	public float getDamageReceived() { return damageReceived; }

	public void incrementDamageReceived(float damageReceived) {	this.damageReceived += damageReceived; }
}
