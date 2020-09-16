package com.mygdx.hadal.server;

public class SavedPlayerFieldsExtra {

	private float damageDealtEnemies, damageDealtAllies, damageDealtSelf, damageReceived;
	
	
	
	public SavedPlayerFieldsExtra() {
		
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
