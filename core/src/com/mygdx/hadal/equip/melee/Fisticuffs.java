package com.mygdx.hadal.equip.melee;


import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Fisticuffs extends MeleeWeapon {

	private final static String name = "Fisticuffs";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.0f;
	private final static float baseDamage = 25.0f;

	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 0.1f;
	private final static float knockback = 25.0f;
	private final static float momentum = 1.5f;
	private final static Sprite projSprite = Sprite.IMPACT;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float range = 50.0f;
	
	public Fisticuffs(Schmuck user) {
		super(user, name, swingCd, windup, momentum, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		
		float xImpulse = -(user.getPosition().x - ((Player)user).getMouse().getPosition().x);
		float yImpulse = -(user.getPosition().y - ((Player)user).getMouse().getPosition().y);
				
		Vector2 projOffset = new Vector2(xImpulse, yImpulse).nor().scl(range);
		
		float offsetX = user.getPosition().x * PPM + projOffset.x;  
		float offsetY = user.getPosition().y * PPM + projOffset.y;  
		
		Hitbox hbox = new HitboxSprite(state, offsetX, offsetY, projectileWidth, projectileHeight, lifespan, new Vector2(0, 0),	filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.MELEE));
	}
}
