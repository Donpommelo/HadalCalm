package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

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
		new Poison(state, poisonRadius, poisonRadius,
				(int)(this.hbox.getBody().getPosition().x * PPM), 
				(int)(this.hbox.getPosition().y * PPM), poisonDamage, poisonDuration, creator.getSchmuck(), filter);
	}
}
