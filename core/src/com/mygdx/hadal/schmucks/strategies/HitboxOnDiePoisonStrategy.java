package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnDiePoisonStrategy extends HitboxStrategy{
	
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	private short filter;
	
	public HitboxOnDiePoisonStrategy(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, 
			float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		new Poison(state,this.hbox.getPixelPosition(), new Vector2(poisonRadius, poisonRadius), poisonDamage, poisonDuration, creator.getSchmuck(), true, filter);
	}
}
