package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Boomerang extends RangedWeapon {

	private final static String name = "Boomerang";
	private final static int clipSize = 3;
	private final static float shootCd = 1.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 25.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 90;
	private final static int projectileHeight = 90;
	private final static float lifespanx = 5.0f;
	private final static float gravity = 0;
	private final static float returnAmp = 1.25f;

	private final static int projDura = 1;
	
	private final static Sprite projSprite = Sprite.BOOMERANG;
	private final static Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private final static Sprite eventSprite = Sprite.P_BOOMERANG;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespanx, projDura, 0, startVelocity,
					(short) 0, false, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount = 0;
				
				@Override
				public void create() {
					hbox.getBody().setAngularVelocity(5);
				}
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;

					if (controllerCount >= 1/60f) {
						Vector2 diff = new Vector2(user.getBody().getPosition().x * PPM - hbox.getPosition().x * PPM, 
								user.getBody().getPosition().y * PPM - hbox.getPosition().y * PPM);
						hbox.getBody().applyForceToCenter(diff.nor().scl(projectileSpeed * hbox.getBody().getMass() * returnAmp), true);

						controllerCount -= delta;
					}
				}
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB instanceof PlayerBodyData) {
							if (((PlayerBodyData)fixB).getPlayer().getHitboxfilter() == user.getHitboxfilter()) {
								if (hbox.getLifeSpan() < lifespanx - 0.25f) {
									if (((PlayerBodyData)fixB).getCurrentTool() instanceof Boomerang) {
										((Boomerang)((PlayerBodyData)fixB).getCurrentTool()).gainAmmo(1);
									}
									this.hbox.queueDeletion();
								}
							} else {
								fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
										user.getBodyData(), tool, true, DamageTypes.RANGED);
							}
						} else {
							fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
									user.getBodyData(), tool, true, DamageTypes.RANGED);
						}
					}
				}
			});	
		}
	};
	
	public Boomerang(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
