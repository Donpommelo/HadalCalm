package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.ContactWallParticles;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.strategies.ReturnToUser;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Boomerang extends RangedWeapon {

	private final static int clipSize = 3;
	private final static int ammoSize = 3;
	private final static float shootCd = 1.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 35.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespanx = 4.0f;
	
	private final static Sprite projSprite = Sprite.BOOMERANG;
	private final static Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private final static Sprite eventSprite = Sprite.P_BOOMERANG;
	
	public Boomerang(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespanx, startVelocity, (short) 0, false, true, user, projSprite);		
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), projectileSpeed));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				
				//Set boomerang to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(5);
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					
					//if boomerang hits user, they regain their clip. Otherwise do damage normally
					if (fixB instanceof PlayerBodyData) {
						if (((PlayerBodyData)fixB).getPlayer().getHitboxfilter() == user.getHitboxfilter()) {
							if (((PlayerBodyData)fixB).getCurrentTool() instanceof Boomerang && ((PlayerBodyData)fixB).equals(hbox.getCreator().getBodyData())) {
								((Boomerang)((PlayerBodyData)fixB).getCurrentTool()).gainClip(1);
							}
							this.hbox.queueDeletion();
						} else {
							fixB.receiveDamage(baseDamage, this.hbox.getLinearVelocity().nor().scl(knockback), user.getBodyData(), true, DamageTypes.RANGED);
						}
					} else {
						fixB.receiveDamage(baseDamage, this.hbox.getLinearVelocity().nor().scl(knockback), user.getBodyData(), true, DamageTypes.RANGED);
					}
				}
			}
		});	
	}
}
