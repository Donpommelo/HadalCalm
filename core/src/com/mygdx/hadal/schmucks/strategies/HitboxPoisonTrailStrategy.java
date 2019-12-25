package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hbox continually create poison tiles as it travels
 * @author Zachary Tu
 *
 */
public class HitboxPoisonTrailStrategy extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private short filter;
	
	//the time interval between creating poision
	private Vector2 lastPosition = new Vector2();
	
	public HitboxPoisonTrailStrategy(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
		
		lastPosition.set(proj.getStartPos());
	}
	
	@Override
	public void controller(float delta) {
		if (lastPosition.dst(hbox.getPixelPosition()) > poisonRadius) {
			lastPosition.set(hbox.getPixelPosition());
			new Poison(state, this.hbox.getPixelPosition(), new Vector2(poisonRadius, poisonRadius), poisonDamage, poisonDuration, creator.getSchmuck(), true, filter);
		}
	}
}
