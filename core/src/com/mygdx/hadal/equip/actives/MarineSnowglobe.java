package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Static;

public class MarineSnowglobe extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 8.0f;
	
	private static final Vector2 projectileSize = new Vector2(400, 400);
	private static final float duration = 0.5f;

	private static final float projectileDamage = 24.0f;
	private static final float projectileKB = 15.0f;
	
	private static final float slowDuration = 5.0f;
	private static final float slowSlow = 0.75f;
	
	public MarineSnowglobe(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.ICE_IMPACT.playUniversal(state, user.getPlayer().getPixelPosition(), 0.9f, 0.5f, false);
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, duration, new Vector2(), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new Static(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, projectileDamage, projectileKB, DamageTypes.RANGED));
		hbox.addStrategy(new ContactUnitSlow(state, hbox, user, slowDuration, slowSlow, Particle.ICE_CLOUD));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.ICE_CLOUD, 0.0f, 1.0f).setParticleSize(25.0f));
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
