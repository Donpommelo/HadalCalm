package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class DiamondCutter extends MeleeWeapon {

	private final static String name = "Diamond Cutter";
	private final static float swingCd = 0.0f;
	private final static float windup = 0.0f;
	private final static float momentum = 7.5f;
	
	private final static int projectileWidth = 150;
	private final static int projectileHeight = 150;
	
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static Sprite projSprite = Sprite.BUZZSAW;
	
	private final static float baseDamage = 2.5f;
	private final static float knockback = 0.0f;

	private final static float range = 75.0f;
	private final static float spinSpeed = 8.0f;
	private final static float spinInterval = 1/60f;
	
	
	private Hitbox hbox;
	private boolean held = false;
	
	public DiamondCutter(Schmuck user) {
		super(user, name, swingCd, windup, momentum, weaponSprite, eventSprite);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, final BodyData shooter, short faction, final int x, final int y) {
		
		if(!held) {
			held = true;
			final Equipable tool = this;
			hbox = new HitboxSprite(state, x, y, (int)projectileWidth, (int)projectileHeight, 0, 0, 0, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, true, user, projSprite);
			hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount = 0;
				
				@Override
				public void create() {
					hbox.setAngularVelocity(spinSpeed);
				}
				
				@Override
				public void controller(float delta) {
					float xImpulse = -(shooter.getSchmuck().getPosition().x - ((Player)shooter.getSchmuck()).getMouse().getPosition().x);
					float yImpulse = -(shooter.getSchmuck().getPosition().y - ((Player)shooter.getSchmuck()).getMouse().getPosition().y);
							
					Vector2 projOffset = new Vector2(xImpulse, yImpulse).nor().scl(range);
					hbox.setTransform(
							shooter.getSchmuck().getPosition().x + projOffset.x / PPM,  
							shooter.getSchmuck().getPosition().y + projOffset.y / PPM,
							hbox.getBody().getAngle());
					
					controllerCount+=delta;
					
					
					if (controllerCount >= spinInterval) {
						Hitbox pulse = new Hitbox(state, hbox.getPosition().x * PPM, hbox.getPosition().y * PPM, projectileWidth / 2, projectileHeight / 2, 
								0, spinInterval, 1, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, true, user);
						pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData()));
						pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
						
						controllerCount -= delta;
					}
				}
				
				@Override
				public void die() {
					hbox.queueDeletion();
				}
			});
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {

	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		held = false;
		hbox.die();
	}
}
