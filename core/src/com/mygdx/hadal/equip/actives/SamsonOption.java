package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

public class SamsonOption extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private final static Vector2 projectileSize = new Vector2(400, 400);
	private final static float duration = 1.4f;

	private final static float procCd = 0.2f;

	private final static int explosionRadius = 200;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 40.0f;
	
	public SamsonOption(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, duration, new Vector2(), (short) 0, false, false, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		hbox.setSyncDefault(false);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new Static(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private float procCdCount = procCd;
			private Vector2 explosionPosition = new Vector2();
			@Override
			public void controller(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					float randX = (float) ((Math.random() * hbox.getSize().x) - (hbox.getSize().x / 2) + hbox.getPixelPosition().x);
					float randY = (float) ((Math.random() * hbox.getSize().y) - (hbox.getSize().y / 2) + hbox.getPixelPosition().y);
					
					SoundEffect.EXPLOSION6.playUniversal(state, explosionPosition.set(randX, randY), 0.5f, false);
					WeaponUtils.createExplosion(state, explosionPosition, explosionRadius, user.getSchmuck(), explosionDamage, explosionKnockback, (short) 0);
				}
			}
		});
		
		user.receiveDamage(9999, new Vector2(), user, false);
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
