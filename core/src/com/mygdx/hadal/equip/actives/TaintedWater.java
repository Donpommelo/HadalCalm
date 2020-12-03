package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

public class TaintedWater extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 13.0f;
	
	private static final float duration = 4.0f;

	private static final float lifespan = 1.0f;
	private static final float projectileSpeed = 35.0f;

	private static final Vector2 projectileSize = new Vector2(40, 40);

	private static final Vector2 poisonSize = new Vector2(101, 50);
	private static final float poisonSizeSquared = 11000f;
	private static final float poisonSpread = 75f;
	private static final float poisonDamage = 0.6f;
	private static final float poisonDuration = 2.5f;
	
	public TaintedWater(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		SoundEffect.MAGIC27_EVIL.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		Vector2 startPosition = user.getPlayer().getPixelPosition();

		Hitbox hboxBase = new RangedHitbox(state, startPosition, projectileSize, lifespan,
			new Vector2(weaponVelo).nor().scl(projectileSpeed), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(),
			Sprite.NOTHING);

		hboxBase.addStrategy(new ControllerDefault(state, hboxBase, user));
		hboxBase.addStrategy(new ContactWallDie(state, hboxBase, user));
		hboxBase.addStrategy(new HitboxStrategy(state, hboxBase, user) {

			private final Vector2 lastPosition = new Vector2(startPosition);
			private float numPoison;
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > poisonSizeSquared) {
					numPoison++;
					lastPosition.set(hbox.getPixelPosition());
					new Poison(state, hbox.getPixelPosition(), new Vector2(poisonSize).add(0, numPoison * poisonSpread), poisonDamage, poisonDuration, user.getSchmuck(),
						true, user.getSchmuck().getHitboxfilter()) {

						@Override
						public void create() {
							super.create();
							setAngle(weaponVelo.angleRad());
						}
					};
				}
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
