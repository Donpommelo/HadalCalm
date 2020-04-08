package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a field of poison when the attached hbox dies
 * @author Zachary Tu
 *
 */
public class DiePoison extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private short filter;
	
	public DiePoison(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, 
			float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		new Poison(state, this.hbox.getPixelPosition(), new Vector2(poisonRadius, poisonRadius), poisonDamage, poisonDuration, creator.getSchmuck(), true, filter);
	}
}
