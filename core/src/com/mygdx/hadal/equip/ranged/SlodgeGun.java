package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class SlodgeGun extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 21;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 6.0f;
	private final static float recoil = 24.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 25.0f;
	private final static Vector2 projectileSize = new Vector2(40, 40);
	private final static float lifespan = 4.0f;
	
	private final static float procCd = .05f;

	private final static float slowDura = 3.0f;
	private final static float slow = 0.8f;
	private final static float fireDuration = 0.8f;

	private final static Sprite weaponSprite = Sprite.MT_SLODGEGUN;
	private final static Sprite eventSprite = Sprite.P_SLODGEGUN;
	
	public SlodgeGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hbox.setGravity(3.0f);
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DEBRIS_TRAIL, 0.0f, 3.0f));
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
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (processClip(state, shooter)) {
			shooter.addStatus(new FiringWeapon(state, fireDuration, shooter, shooter, projectileSpeed, 0, 0, projectileSize.x, procCd, this));
		}
	}
}
