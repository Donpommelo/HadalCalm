package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class TrickGun extends RangedWeapon {

	private final static int clipSize = 5;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 60.0f;
	private final static float recoil = 16.0f;
	private final static float knockback = 20.0f;
	private final static float projectileSpeed = 35.0f;
	private final static Vector2 projectileSize = new Vector2(71, 61);
	private final static float lifespan = 1.5f;
	
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float projectileSpeedAfter = 60.0f;

	private boolean firstClicked = false;
	private Vector2 pos1 = new Vector2();
	private Vector2 pos2 = new Vector2();
	private Vector2 vel1 = new Vector2();
	private Vector2 vel2 = new Vector2();
	
	private final static Sprite projSprite = Sprite.TRICKBULLET;
	
	public TrickGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		//when clicked, keep track of mouse location
		if (!firstClicked) {
			pos1.set(mouseLocation);
			firstClicked = true;
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		
		//when released, fire weapon at location where mouse was pressed and keep track of location where mouse is released.
		if (firstClicked) {
			pos2.set(mouseLocation);
			
			float powerDiv = pos1.dst(pos2) / projectileSpeed;
			
			float xImpulse = -(pos1.x - pos2.x) / powerDiv;
			float yImpulse = -(pos1.y - pos2.y) / powerDiv;
			vel2.set(xImpulse, yImpulse);
			
			powerDiv = user.getPixelPosition().dst(pos1.x, pos1.y) / projectileSpeed;
			
			xImpulse = -(user.getPixelPosition().x - pos1.x) / powerDiv;
			yImpulse = -(user.getPixelPosition().y - pos1.y) / powerDiv;
			vel1.set(xImpulse, yImpulse);
			
			this.setWeaponVelo(vel1);
			
			super.execute(state, bodyData);			
			
			firstClicked = false;
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LASERHARPOON.playUniversal(state, startPosition, 0.6f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setSyncDefault(false);
		hbox.setSyncInstant(true);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.TRICK, 0.0f, 3.0f).setRotate(true));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.TRICK, DamageTypes.RANGED));
		
		//This extra check of firstClicked makes sure effects that autofire this gun work (like muddling cup)
		if (firstClicked) {

			//when hbox reaches location of mouse click, it moves towards location of mouse release
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private boolean firstReached = false;
				private Vector2 startLocation = new Vector2();
				private float distance;
				private Vector2 target = new Vector2();
				
				@Override
				public void create() {
					this.startLocation.set(hbox.getPixelPosition());
					this.distance = startLocation.dst(pos1);
				}
				
				@Override
				public void controller(float delta) {
					if (!firstReached) {
						if (startLocation.dst(hbox.getPixelPosition()) >= distance) {
							
							if (!pos2.equals(pos1)) {
								target.set(pos2).sub(hbox.getPixelPosition());
							} else {
								target.set(hbox.getLinearVelocity());
							}
							
							hbox.setLinearVelocity(target.nor().scl(projectileSpeedAfter));
							SoundEffect.LASERHARPOON.playUniversal(state, startPosition, 0.8f, false);
								
							firstReached = true;
						}
					}
				}
			});
		}
	}
}
