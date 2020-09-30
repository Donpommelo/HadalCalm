package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox continually create poison tiles as it travels
 * @author Zachary Tu
 */
public class PoisonTrail extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private short filter;
	
	//the time interval between creating poison
	private Vector2 lastPosition = new Vector2();
	private Vector2 poisonSize = new Vector2();
	
	public PoisonTrail(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
		
		lastPosition.set(proj.getStartPos());
		poisonSize.set(poisonRadius, poisonRadius);
	}
	
	private Vector2 entityLocation = new Vector2();
	@Override
	public void controller(float delta) {
		entityLocation.set(hbox.getPixelPosition());
		if (lastPosition.dst(entityLocation) > poisonRadius) {
			lastPosition.set(entityLocation);
			new Poison(state, entityLocation, poisonSize, poisonDamage, poisonDuration, creator.getSchmuck(), true, filter);
		}
	}
}
