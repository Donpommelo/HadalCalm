package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

public class MorningStar extends MeleeWeapon {

	private static final float swingCd = 0.4f;
	private static final float windup = 0.1f;
	
	private static final Vector2 projectileSize = new Vector2(75, 75);
	private static final Vector2 chainSize = new Vector2(20, 20);
	
	private static final Sprite weaponSprite = Sprite.MT_DEFAULT;
	private static final Sprite eventSprite = Sprite.P_DEFAULT;

	private static final Sprite chainSprite = Sprite.ORB_ORANGE;
	private static final Sprite projSprite = Sprite.FLAIL;
	
	private static final float baseDamage = 50.0f;
	private static final float knockback = 60.0f;

	private static final float swingForce = 7500.0f;
	private static final float range = 60.0f;
	private static final float chainLength = 1.2f;

	//this is the hitbox that this weapon extends
	private Hitbox base, star;
	private final Hitbox[] links = new Hitbox[4];
	
	//is the hitbox active?
	private boolean active = false;

	public MorningStar(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
	
	private final Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		
		//when clicked, we create the flail weapon and move it in the direction of the mouse click
		if (!active) {
			active = true;
			activate(state, shooter, mouseLocation);
		}
		if (star != null) {
			star.applyForceToCenter(projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(swingForce));
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void unequip(PlayState state) {
		active = false;
		deactivate(state);
	}
	
	/**
	 * This is called to create the flail weapon
	 */
	private void activate(PlayState state, BodyData shooter, Vector2 mouseLocation) {
		
		projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(range);
		
		//the base is connected to the player and links to the rest of the flail weapon
		base = new Hitbox(state, shooter.getSchmuck().getPixelPosition(), chainSize, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, false, user, chainSprite);
		base.setDensity(1.0f);
		base.makeUnreflectable();
		base.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

		base.addStrategy(new HitboxStrategy(state, base, user.getBodyData()) {
			
			private boolean linked = false;

			@Override
			public void controller(float delta) {
				
				//detach the flail when the user dies
				if (!user.isAlive()) {
					hbox.die();
					deactivate(state);
				}
				
				if (!linked) {
					if (user.getBody() != null && base.getBody() != null) {
						linked = true;
						RevoluteJointDef joint1 = new RevoluteJointDef();
						joint1.bodyA = user.getBody();
						joint1.bodyB = base.getBody();
						joint1.collideConnected = false;
						
						joint1.localAnchorA.set(0, 0);
						joint1.localAnchorB.set(chainLength, 0);
						
						state.getWorld().createJoint(joint1);
					}
				}
			}
			
			@Override
			public void die() {
				hbox.queueDeletion();
			}
		});
		
		//create several linked hboxes
		for (int i = 0; i < links.length; i++) {
			final int currentI = i;
			links[i] = new Hitbox(state, shooter.getSchmuck().getPixelPosition(), chainSize, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, false, user, chainSprite);
			links[i].setDensity(1.0f);
			links[i].makeUnreflectable();
			links[i].setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

			links[i].addStrategy(new HitboxStrategy(state, links[i], user.getBodyData()) {
				
				private boolean linked = false;
				
				@Override
				public void controller(float delta) {
					
					if (!linked) {
						if (currentI == 0) { 
							if (base.getBody() != null && hbox.getBody() != null) {
								linked = true;
								RevoluteJointDef joint1 = new RevoluteJointDef();
								joint1.bodyA = base.getBody();
								joint1.bodyB = hbox.getBody();
								joint1.collideConnected = false;
								
								joint1.localAnchorA.set(-chainLength, 0);
								joint1.localAnchorB.set(chainLength, 0);
								
								state.getWorld().createJoint(joint1);
							}
						} else {
							if (links[currentI - 1].getBody() != null && hbox.getBody() != null) {
								linked = true;
								
								RevoluteJointDef joint1 = new RevoluteJointDef();
								joint1.bodyA = links[currentI - 1].getBody();
								joint1.bodyB = hbox.getBody();
								joint1.collideConnected = false;
								joint1.localAnchorA.set(-chainLength, 0);
								joint1.localAnchorB.set(chainLength, 0);
								
								state.getWorld().createJoint(joint1);
							}
						}
					}
				}
				
				@Override
				public void die() {
					hbox.queueDeletion();
				}
			});
		}
		
		//the star hbox damages people and has weight
		star = new RangedHitbox(state, shooter.getSchmuck().getPixelPosition(), projectileSize, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), false, true, user, projSprite);
		
		star.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		star.setGravity(1.0f);
		star.setDensity(0.1f);
		star.makeUnreflectable();
		
		star.addStrategy(new DamageStandard(state, star, user.getBodyData(), baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.MELEE).setRepeatable(true));
		star.addStrategy(new ContactWallSound(state, star, user.getBodyData(), SoundEffect.WALL_HIT1, 0.25f));
		star.addStrategy(new ContactUnitSound(state, star, user.getBodyData(), SoundEffect.SLAP, 0.25f, true).setPitch(0.5f));

		star.addStrategy(new HitboxStrategy(state, star, user.getBodyData()) {
			private boolean linked = false;
			
			@Override
			public void controller(float delta) {
				
				if (!linked) {
					if (links[links.length - 1].getBody() != null && hbox.getBody() != null) {
						linked = true;
						
						RevoluteJointDef joint1 = new RevoluteJointDef();
						joint1.bodyA = links[links.length - 1].getBody();
						joint1.bodyB = hbox.getBody();
						joint1.collideConnected = false;
						joint1.localAnchorA.set(-chainLength, 0);
						joint1.localAnchorB.set(chainLength, 0);
						
						state.getWorld().createJoint(joint1);
					}
				}
			}
			
			@Override
			public void die() {
				hbox.queueDeletion();
			}
		});
	}
	
	/**
	 * upon deactivation, we delete the base hbox and make the others have a temporary lifespan
	 * this is so that the user can fling the flail by switching to another weapon
	 */
	private void deactivate(PlayState state) {
		active = false;
		
		if (base != null) {
			base.die();
		}
		
		if (star != null) {
			star.setLifeSpan(2.0f);
			star.addStrategy(new ControllerDefault(state, star, user.getBodyData()));
		}
		for (final Hitbox link : links) {
			if (link != null) {
				link.setLifeSpan(2.0f);
				link.addStrategy(new ControllerDefault(state, link, user.getBodyData()));
			}
		}
	}

	@Override
	public float getBotRangeMax() { return 5 * chainLength + 2; }
}
