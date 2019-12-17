package com.mygdx.hadal.equip.ranged;

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
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.statuses.Status;

public class SlodgeGun extends RangedWeapon {

	private final static String name = "Slodge Gun";
	private final static int clipSize = 1;
	private final static int ammoSize = 21;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 3.0f;
	private final static float recoil = 16.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 3.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 4.0f;
	private final static float gravity = 3;
	
	private final static int projDura = 1;
	private final static float procCd = .05f;

	private final static float slowDura = 3.0f;
	private final static float slow = 0.75f;
	private final static float fireDuration = 0.75f;

	private final static Sprite projSprite = Sprite.SCRAP_C;
	private final static Sprite weaponSprite = Sprite.MT_SLODGEGUN;
	private final static Sprite eventSprite = Sprite.P_SLODGEGUN;
	
	public SlodgeGun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		if (!(user instanceof Player)) {
			return;
		}
		final Equipable tool = this;
		final Player p = (Player)user;
		
		p.getBodyData().addStatus(new Status(state, fireDuration, "", "", false, p.getBodyData(), p.getBodyData()) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				
				if (p.getMouse() == null) {
					return;
				}
				
				procCdCount += delta;
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					Vector2 startVelocity = p.getMouse().getPosition().sub(inflicted.getSchmuck().getPosition()).scl(projectileSpeed);
					Vector2 startPosition = inflicted.getSchmuck().getProjectileOrigin(startVelocity, projectileSize);
					Hitbox hbox = new HitboxSprite(state, startPosition.x, startPosition.y, 
							projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
							filter, true, true, user, projSprite);
					hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
						
						@Override
						public void onHit(HadalData fixB) {
							if (fixB != null) {
								if (fixB instanceof BodyData) {
									((BodyData)fixB).addStatus(new Slodged(state, slowDura, slow, user.getBodyData(), ((BodyData)fixB)));
								}
							}
						}
						
					});
				}
			}
		});
	}
}
