package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
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
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

public class OrbitalShield extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 18.0f;
	
	private static final Vector2 projSize = new Vector2(25, 25);
	private static final Vector2 spriteSize = new Vector2(40, 40);

	private static final float projDamage= 27.0f;
	private static final float projKnockback= 25.0f;
	private static final float projLifespan= 5.0f;

	private static final float projSpeed= 180.0f;
	private static final float projRange= 5.0f;

	public OrbitalShield(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		new SoundEntity(state, createOrbital(state, user, 0), SoundEffect.MAGIC25_SPELL, 1.0f, 1.0f, true, true, soundSyncType.TICKSYNC);
		
		createOrbital(state, user, 90);
		createOrbital(state, user, 180);
		createOrbital(state, user, 270);
	}
	
	public Hitbox createOrbital(PlayState state, PlayerBodyData user, float startAngle) {
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projSize, projLifespan, new Vector2(), user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), Sprite.STAR_WHITE);
		hbox.setSpriteSize(spriteSize);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, projDamage, projKnockback, DamageTypes.MAGIC).setStaticKnockback(true).setRepeatable(true));
		hbox.addStrategy(new OrbitUser(state, hbox, user, startAngle, projRange, projSpeed));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.STAR_TRAIL, 0.0f, 1.0f));

		return hbox;
	}
}
