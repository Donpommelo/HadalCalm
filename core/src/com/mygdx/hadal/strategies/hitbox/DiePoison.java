package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a field of poison when the attached hbox dies
 * @author Klalemachus Klalexander
 */
public class DiePoison extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private final float poisonDamage, poisonDuration;
	private final int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private final short filter;

	//this is the effect/item/weapon source of the poison
	private final DamageSource source;

	public DiePoison(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, float poisonDuration,
					 short filter, DamageSource source) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
		this.source = source;
	}
	
	@Override
	public void die() {
		Poison poison = new Poison(state, this.hbox.getPixelPosition(), new Vector2(poisonRadius, poisonRadius), poisonDamage, poisonDuration,
			creator.getSchmuck(), true, filter, source);

		if (!state.isServer()) {
			((ClientState) state).addEntity(poison.getEntityID(), poison, false, ClientState.ObjectLayer.EFFECT);
		}
	}
}
