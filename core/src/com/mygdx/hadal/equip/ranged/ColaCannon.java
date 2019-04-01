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
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import static com.mygdx.hadal.utils.Constants.PPM;

public class ColaCannon extends RangedWeapon {

	private final static String name = "Cola Cannon";
	private final static int clipSize = 1;
	private final static int ammoSize = 14;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 7.0f;
	private final static float recoil = 18.0f;
	private final static float knockback = 4.5f;
	private final static float projectileSpeed = 45.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 4.0f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
	private final static float procCd = .05f;
	private final static float fireDuration = 1.6f;
	private final static float maxCharge = 200.0f;
	private final static float minVelo = 9.0f;
	private final static float minDuration = 0.5f;

	private final static Sprite projSprite = Sprite.ORB_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.MT_DEFAULT;
	
	private float chargeAmount;
	private Vector2 lastMouse = new Vector2(0, 0);
	
	public ColaCannon(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {
		if (chargeAmount < maxCharge && !reloading) {
			chargeAmount += lastMouse.dst(x, y);
			if (chargeAmount > maxCharge) {
				chargeAmount = maxCharge;
			}
		}
		
		lastMouse = new Vector2(x, y);
		super.mouseClicked(delta, state, shooter, faction, x, y);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {

	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		chargeAmount = 0;
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		if (!(user instanceof Player)) {
			return;
		}
		final Equipable tool = this;
		final Player p = (Player)user;
		
		final float duration = fireDuration * chargeAmount / maxCharge + minDuration;
		final float velocity = projectileSpeed * chargeAmount / maxCharge + minVelo;
		
		p.getBodyData().addStatus(new Status(state, duration, "", "", false, p.getBodyData(), p.getBodyData()) {
			
			private float procCdCount;
			private float currentVelocity = velocity;
			
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				
				if (p.getMouse() == null) {
					return;
				}
				
				procCdCount += delta;
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					if (currentVelocity > minVelo) {
						currentVelocity -= 1.0f;
					}
					
					Vector2 startVelocity = p.getMouse().getBody().getPosition()
							.sub(inflicted.getSchmuck().getBody().getPosition()).nor().scl(currentVelocity);
					Hitbox hbox = new HitboxSprite(state, 
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM, 
							projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
							filter, true, true, user, projSprite);
					hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
					hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
