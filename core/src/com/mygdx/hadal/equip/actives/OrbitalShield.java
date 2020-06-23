package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

public class OrbitalShield extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 16.0f;
	
	private static final Vector2 projSize = new Vector2(50, 50);

	private static final float projDamage= 20.0f;
	private static final float projKnockback= 25.0f;
	private static final float projLifespan= 8.0f;

	private static final float projSpeed= 180.0f;
	private static final float projRange= 4.5f;

	public OrbitalShield(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		new SoundEntity(state, createOrbital(state, user, 0), SoundEffect.MAGIC25, 1.0f, true, true, soundSyncType.TICKSYNC);
		
		createOrbital(state, user, 90);
		createOrbital(state, user, 180);
		createOrbital(state, user, 270);
	}
	
	public Hitbox createOrbital(PlayState state, PlayerBodyData user, float startAngle) {
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projSize, projLifespan, new Vector2(), user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), Sprite.STAR);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user, projDamage, 0, DamageTypes.MAGIC));
		hbox.addStrategy(new DamageStatic(state, hbox, user, 0, projKnockback, DamageTypes.MAGIC));
		hbox.addStrategy(new OrbitUser(state, hbox, user, startAngle, projRange, projSpeed));
		
		return hbox;
	}
}
