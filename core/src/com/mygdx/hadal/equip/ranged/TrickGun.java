package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class TrickGun extends RangedWeapon {

	private final static String name = "Trick Gun";
	private final static int clipSize = 5;
	private final static int ammoSize = 24;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 55.0f;
	private final static float recoil = 16.0f;
	private final static float knockback = 20.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 80;
	private final static int projectileHeight = 80;
	private final static float lifespan = 2.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float projectileSpeedAfter = 45.0f;

	private boolean firstClicked = false;
	private Vector2 pos1 = new Vector2(0, 0);
	private Vector2 pos2 = new Vector2(0, 0);
	private Vector2 vel1 = new Vector2(0, 0);
	private Vector2 vel2 = new Vector2(0, 0);
	
	public TrickGun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (firstClicked) {
			pos2.set(x, y);
			firstClicked = false;
			
			float powerDiv = pos1.dst(pos2) / projectileSpeed;
			
			float xImpulse = -(pos1.x - pos2.x) / powerDiv;
			float yImpulse = -(pos1.y - pos2.y) / powerDiv;
			vel2.set(xImpulse, yImpulse);
			
			super.execute(state, bodyData);
		} else {
			pos1.set(x, y);
			firstClicked = true;
		}
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		
		float powerDiv = user.getPosition().dst(pos1.x, pos1.y) / projectileSpeed;
		
		float xImpulse = -(user.getPosition().x - pos1.x) / powerDiv;
		float yImpulse = -(user.getPosition().y - pos1.y) / powerDiv;
		vel1.set(xImpulse, yImpulse);
		
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, vel1,
				filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private boolean firstReached = false;
			private Vector2 startLocation;
			private float distance;
			
			@Override
			public void create() {
				this.startLocation = new Vector2(hbox.getPosition());
				this.distance = startLocation.dst(pos1);
			}
			
			@Override
			public void controller(float delta) {
				if (!firstReached) {
					if (startLocation.dst(hbox.getPosition()) >= distance) {
						if (!pos2.equals(pos1)) {
							hbox.setLinearVelocity(pos2.sub(pos1).nor().scl(projectileSpeedAfter));
						}
						firstReached = true;
					}
				}
			}
		});
	}
}
