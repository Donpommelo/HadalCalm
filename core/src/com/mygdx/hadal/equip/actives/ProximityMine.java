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
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.Static;

public class ProximityMine extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 10.0f;
	
	private final static Vector2 projectileSize = new Vector2(90, 20);
	private final static float lifespan = 3.0f;
	private final static float mineLifespan = 20.0f;

	private final static float projectileSpeed = 60.0f;
	
	private final static int explosionRadius = 200;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 50.0f;
	
	private final static Sprite projSprite = Sprite.STICKYBOMB;

	public ProximityMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan,  new Vector2(0, -projectileSpeed), user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), projSprite);
		
		hbox.setGravity(3.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new DieParticles(state, hbox, user, Particle.SMOKE_TOTLC));
		hbox.addStrategy(new DieSound(state, hbox, user, SoundEffect.SLAP, 0.6f));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void die() {
				Hitbox mine = new RangedHitbox(state, hbox.getPixelPosition(), projectileSize, mineLifespan,  new Vector2(), (short) 0, true, false, user.getPlayer(), Sprite.NOTHING);
				
				mine.addStrategy(new ControllerDefault(state, mine, user));
				mine.addStrategy(new Static(state, mine, user));
				mine.addStrategy(new ContactUnitDie(state, mine, user).setDelay(3.0f));
				mine.addStrategy(new DieExplode(state, mine, user, explosionRadius, explosionDamage, explosionKnockback, (short) 0));
				mine.addStrategy(new DieSound(state, mine, user, SoundEffect.EXPLOSION6, 0.6f));
			}
		});
	}
}
