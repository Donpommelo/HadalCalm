package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;
import com.mygdx.hadal.utils.Constants;

public class ProximityMine extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 10.0f;
	
	private final static Vector2 projectileSize = new Vector2(75, 30);
	private final static float lifespan = 3.0f;
	private final static float mineLifespan = 24.0f;

	private final static float projectileSpeed = 60.0f;
	
	private final static int explosionRadius = 250;
	private final static float explosionDamage = 80.0f;
	private final static float explosionKnockback = 50.0f;
	
	private final static float primeDelay = 2.0f;

	private final static Sprite projSprite = Sprite.LAND_MINE;

	public ProximityMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan,  new Vector2(0, -projectileSpeed), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(), projSprite);
		hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL));
		hbox.makeUnreflectable();
		hbox.setGravity(3.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user));
		hbox.addStrategy(new DieParticles(state, hbox, user, Particle.SMOKE).setParticleSize(150));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private HadalEntity floor;
			private boolean planted, set;
			private float primeCount;
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					floor = fixB.getEntity();
					if (floor != null) {
						if (floor.getBody() != null) {
							planted = true;
						}
					}
				}
			}
			
			@Override
			public void controller(float delta) {
				if (planted) {
					planted = false;
					WeldJointDef joint = new WeldJointDef();
					joint.bodyA = floor.getBody();
					joint.bodyB = hbox.getBody();
					joint.localAnchorA.set(new Vector2(hbox.getBody().getPosition()).sub(floor.getBody().getPosition()));
					joint.localAnchorB.set(0, 0);
					state.getWorld().createJoint(joint);
					
					SoundEffect.SLAP.playUniversal(state, hbox.getPixelPosition(), 0.6f, false);
					set = true;
				}
				if (set) {
					primeCount += delta;
					if (primeCount >= primeDelay) {
						SoundEffect.MAGIC27_EVIL.playUniversal(state, hbox.getPixelPosition(), 1.0f, false);
						hbox.die();
					}
				}
			}
			
			@Override
			public void die() {
				Hitbox mine = new RangedHitbox(state, hbox.getPixelPosition(), projectileSize, mineLifespan,  new Vector2(), (short) 0, true, false, user.getPlayer(), Sprite.NOTHING);
				mine.makeUnreflectable();
				
				mine.addStrategy(new ControllerDefault(state, mine, user));
				mine.addStrategy(new ContactUnitDie(state, mine, user));
				mine.addStrategy(new DieExplode(state, mine, user, explosionRadius, explosionDamage, explosionKnockback, (short) 0));
				mine.addStrategy(new DieSound(state, mine, user, SoundEffect.EXPLOSION6, 0.6f));
				mine.addStrategy(new HitboxStrategy(state, mine, user) {
					
					@Override
					public void create() {
						if (floor != null) {
							if (floor.getBody() != null) {
								WeldJointDef joint = new WeldJointDef();
								joint.bodyA = floor.getBody();
								joint.bodyB = hbox.getBody();
								joint.localAnchorA.set(new Vector2(hbox.getBody().getPosition()).sub(floor.getBody().getPosition()));
								joint.localAnchorB.set(0, 0);
								state.getWorld().createJoint(joint);
							}
						}
					}
				});
			}
		});
	}
}
