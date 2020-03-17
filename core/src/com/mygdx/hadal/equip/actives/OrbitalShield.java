package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

public class OrbitalShield extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 15.0f;
	
	private static final Vector2 projSize = new Vector2(50, 50);

	private static final float projDamage= 10.0f;
	private static final float projKnockback= 25.0f;
	private static final float projLifespan= 12.0f;
	
	private final static float returnAmp = 100.0f;

	private Vector2 projPos = new Vector2();
	
	public OrbitalShield(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		projPos.set(user.getPlayer().getPixelPosition()).add(50, 50);
		createOrbital(state, user, projPos);
		projPos.set(user.getPlayer().getPixelPosition()).add(-50, 50);
		createOrbital(state, user, projPos);
		projPos.set(user.getPlayer().getPixelPosition()).add(50, 0);
		createOrbital(state, user, projPos);
		projPos.set(user.getPlayer().getPixelPosition()).add(-50, 0);
		createOrbital(state, user, projPos);
	}
	
	public void createOrbital(PlayState state, PlayerBodyData user, Vector2 startPos) {
		Hitbox hbox = new RangedHitbox(state, startPos, projSize, projLifespan, new Vector2(), user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), Sprite.STAR);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user, projDamage, projKnockback));
		hbox.addStrategy(new OrbitUser(state, hbox, user, returnAmp));
	}
}