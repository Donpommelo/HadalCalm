package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Stats;

public class HydraulicUppercut extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 8.0f;
	
	private final static float recoil = 150.0f;

	private final static float baseDamage = 60.0f;
	private final static Vector2 hitboxSize = new Vector2(150, 150);
	private final static float lifespan = 0.5f;
	private final static float knockback = 75.0f;
	
	public HydraulicUppercut(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.WOOSH.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		user.addStatus(new Invulnerability(state, 0.75f, user, user));
		user.addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 10.0f, user, user));

		user.getPlayer().pushMomentumMitigation(0, recoil);
		
		Hitbox hbox = new Hitbox(state, mouseLocation, hitboxSize, lifespan, new Vector2(), user.getPlayer().getHitboxfilter(),  true, true, user.getPlayer(), Sprite.IMPACT);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user , Particle.SPARKS));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new FixedToEntity(state, hbox, user, new Vector2(), new Vector2(), false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user, SoundEffect.KICK1, 1.0f, true));
	}
}
