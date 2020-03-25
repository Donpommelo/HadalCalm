package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

public class AnchorSmash extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 20.0f;

	private final static Vector2 projectileSize = new Vector2(300, 200);
	private final static float lifespan = 5.0f;
	private final static float projectileSpeed = 90.0f;

	private final static float range = 1800.0f;
	
	private final static float baseDamage = 90.0f;
	private final static float knockback = 50.0f;
	
	public AnchorSmash(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	private float shortestFraction;
	private Vector2 originPt = new Vector2();
	private Vector2 endPt = new Vector2();
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		originPt.set(mouseLocation).scl( 1 / PPM);
		endPt.set(originPt).add(0, -range);
		shortestFraction = 1.0f;
		
		if (originPt.x != endPt.x || originPt.y != endPt.y) {

			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					if (fixture.getFilterData().categoryBits == (short)Constants.BIT_WALL && fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
				}
				return -1.0f;
				}
			}, originPt, endPt);
		}
		
		endPt.set(originPt).add(0, -range * shortestFraction).scl(PPM);
		originPt.set(endPt).add(0, range);
		
		Hitbox hbox = new Hitbox(state, originPt, projectileSize, lifespan, new Vector2(0, -projectileSpeed),
				user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), Sprite.ORB_BLUE);
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.MAGIC));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void controller(float delta) {
				if (hbox.getPixelPosition().y - hbox.getSize().y / 2 <= endPt.y) {
					hbox.setLinearVelocity(0, 0);
				}
			}
		});
		
	}
}
