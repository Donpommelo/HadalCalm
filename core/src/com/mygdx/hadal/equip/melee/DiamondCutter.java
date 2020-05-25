package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;

public class DiamondCutter extends MeleeWeapon {

	private final static float swingCd = 0.0f;
	private final static float windup = 0.0f;
	
	private final static Vector2 projectileSize = new Vector2(75, 75);
	
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static Sprite projSprite = Sprite.BUZZSAW;
	
	private final static float baseDamage = 4.0f;
	private final static float knockback = 0.0f;

	private final static float range = 75.0f;
	private final static float spinSpeed = 8.0f;
	private final static float spinInterval = 1 / 60f;
	
	//this is the hitbox that this weapon extends
	private Hitbox hbox;
	
	//is the player holding their mouse?
	private boolean held = false;
	
	private SoundEntity sawSound;

	public DiamondCutter(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
	
	private Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, final BodyData shooter, short faction, Vector2 mouseLocation) {
		
		if (sawSound == null) {
			sawSound = new SoundEntity(state, user, SoundEffect.DRILL, 0.8f, true, true, soundSyncType.TICKSYNC);
		} else {
			sawSound.turnOn();
		}
		
		if (!held) {
			held = true;
			projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(range);

			hbox = new Hitbox(state, projOffset, projectileSize, 0, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, true, user, projSprite);
			hbox.makeUnreflectable();
			hbox.setSyncDefault(false);
			hbox.setSyncInstant(true);
			
			hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL, 0.0f, 1.0f));
			hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.CUTTING, DamageTypes.MELEE));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount = 0;
				
				@Override
				public void create() {
					hbox.setAngularVelocity(spinSpeed);
				}
				
				@Override
				public void controller(float delta) {
					
					if (!user.isAlive()) {
						hbox.die();
						held = false;
					}
					
					projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(range);
					hbox.setTransform(
							shooter.getSchmuck().getPosition().x + projOffset.x / PPM,  
							shooter.getSchmuck().getPosition().y + projOffset.y / PPM,
							hbox.getAngle());
					
					controllerCount += delta;
					
					while (controllerCount >= spinInterval) {
						controllerCount -= spinInterval;
						
						Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), projectileSize, spinInterval, new Vector2(0, 0), shooter.getSchmuck().getHitboxfilter(), true, true, user, Sprite.NOTHING);
						pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
						pulse.addStrategy(new DamageStatic(state, pulse, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE));
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
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		held = false;
		if (hbox != null) {
			hbox.die();
		}
		if (sawSound != null) {
			sawSound.turnOff();
		}
	}
	
	@Override
	public void unequip() {
		held = false;
		if (hbox != null) {
			hbox.die();
		}
		if (sawSound != null) {
			sawSound.terminate();
			sawSound = null;
		}
	}
}
