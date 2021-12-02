package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * @author Louhaha Losemary
 */
public class AnchorSmash extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 16.0f;

	private static final Vector2 projectileSize = new Vector2(300, 259);
	private static final float lifespan = 4.0f;
	private static final float projectileSpeed = 60.0f;

	private static final float range = 1800.0f;
	
	private static final float baseDamage = 80.0f;
	private static final float knockback = 50.0f;
	
	private static final Sprite projSprite = Sprite.ANCHOR;

	public AnchorSmash(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	private float shortestFraction;
	private final Vector2 originPt = new Vector2();
	private final Vector2 endPt = new Vector2();
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {

		originPt.set(mouseLocation).scl( 1 / PPM);
		endPt.set(originPt).add(0, -range);
		shortestFraction = 1.0f;
		
		if (originPt.x != endPt.x || originPt.y != endPt.y) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {
				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
					shortestFraction = fraction;
					return fraction;
			}
			return -1.0f;
			}, originPt, endPt);
		}
		
		endPt.set(originPt).add(0, -range * shortestFraction).scl(PPM);
		originPt.set(endPt).add(0, range);
		
		Hitbox hbox = new Hitbox(state, originPt, projectileSize, lifespan, new Vector2(0, -projectileSpeed), user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), projSprite);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.MAGIC));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user, SoundEffect.SLASH, 0.8f, true));
		hbox.addStrategy(new CreateSound(state, hbox, user, SoundEffect.FALLING, 0.5f, false).setPitch(0.75f));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private boolean landed;
			private final Vector2 hboxLocation = new Vector2();
			@Override
			public void controller(float delta) {
				hboxLocation.set(hbox.getPixelPosition());
				if (hboxLocation.y - hbox.getSize().y / 2 <= endPt.y) {
					hbox.setLinearVelocity(0, 0);
					
					if (!landed) {
						landed = true;
						
						SoundEffect.METAL_IMPACT_2.playUniversal(state, hboxLocation, 1.0f, false);
						new ParticleEntity(state, hboxLocation, Particle.BOULDER_BREAK, 0.5f, true, particleSyncType.CREATESYNC);
					}
				}
			}
		});
	}
}
