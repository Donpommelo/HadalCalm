package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Popper extends RangedWeapon {

	private final static String name = "Popper";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 16.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 60.0f;
	private final static int projectileWidth = 120;
	private final static int projectileHeight = 120;
	private final static float lifespan = 0.25f;
	private final static float gravity = 10.0f;
	
	private final static int projDura = 1;
	
	private final static int numProj = 15;
	private final static int spread = 30;
	private final static int fragWidth = 40;
	private final static int fragHeight = 40;
	private final static float fragLifespan = 0.25f;
	private final static float fragDamage = 3.0f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite fragSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.MT_DEFAULT;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Equipable tool, final Vector2 startVelocity, float x, float y, final short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSprite);
			
			if (user instanceof Player) {
				((Player)user).getMouse().getPosition();
			}
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount = 0;
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;

					if (controllerCount >= 1/60f) {
						Vector2 force = new Vector2(hbox.getLinearVelocity().nor().scl(-hbox.getBody().getMass() * projectileSpeed * 5).x,
								-Math.abs(hbox.getLinearVelocity().nor().scl(hbox.getBody().getMass() * projectileSpeed * 5).y));
						hbox.getBody().applyForceToCenter(force, true);
						controllerCount -= delta;
					}
				}
			});
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				@Override
				public void die() {
					for (int i = 0; i < numProj; i++) {
						float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
						Vector2 newVelocity = new Vector2(startVelocity);
						
						Hitbox frag = new HitboxSprite(state, 
								hbox.getBody().getPosition().x * PPM, hbox.getBody().getPosition().y * PPM,
								fragWidth, fragHeight, gravity, fragLifespan, projDura, 0, newVelocity.setAngle(newDegrees),
								filter, true, true, user, fragSprite);
						frag.addStrategy(new HitboxDefaultStrategy(state, frag, user.getBodyData()));
						frag.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, frag, user.getBodyData()));
						frag.addStrategy(new HitboxOnContactWallDieStrategy(state, frag, user.getBodyData()));
						frag.addStrategy(new HitboxDamageStandardStrategy(state, frag, user.getBodyData(), tool, fragDamage, knockback, DamageTypes.RANGED));
					}
				}
			});
		}
	};
	
	public Popper(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
