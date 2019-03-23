package com.mygdx.hadal.equip.ranged;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

public class TeslaCoil extends RangedWeapon {

	private final static String name = "Tesla Coil";
	private final static int clipSize = 4;
	private final static int ammoSize = 40;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.1f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static float radius = 11.0f;
	private final static float activatedDuration = 3.0f;
	private final static float pulseInterval = 0.1f;
	private final static float pulseSpeed = 10.0f;
	private final static int pulseSize = 30;
	private final static float pulseDamage = 6.0f;
	
	private ArrayList<Hitbox> coilsLaid = new ArrayList<Hitbox>();

	public TeslaCoil(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
		final TeslaCoil tool = this;
		final Vector2 endLocation = new Vector2(this.x, this.y);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private Vector2 startLocation;
			private float distance;
			private boolean activated = false;
			
			@Override
			public void create() {
				this.startLocation = new Vector2(hbox.getBody().getPosition());
				this.distance = startLocation.dst(endLocation);
			}
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				
				if (activated) {
					return;
				}
				
				if (startLocation.dst(hbox.getBody().getPosition()) >= distance) {
					hbox.getBody().setLinearVelocity(0, 0);
					tool.checkCoils(state, hbox);
					activated = true;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB == null) {
					hbox.getBody().setLinearVelocity(0, 0);
					tool.checkCoils(state, hbox);
					activated = true;
				} else if (fixB.getType().equals(UserDataTypes.WALL)){
					hbox.getBody().setLinearVelocity(0, 0);
					tool.checkCoils(state, hbox);
					activated = true;
				}
			}
			
			@Override
			public void die() {
				coilsLaid.remove(hbox);
			}
		});
		
		coilsLaid.add(hbox);
	}
	
	public void checkCoils(final PlayState state, final Hitbox hbox) {
		hbox.getWorld().QueryAABB(new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof HitboxData) {
					if (coilsLaid.contains(((HitboxData) fixture.getUserData()).getHbox())) {
						if (!fixture.getUserData().equals(hbox.getHadalData())) {
							coilPairActivated(state, hbox, ((HitboxData) fixture.getUserData()).getHbox());
						}
					}
				}
				return true;
			}
		},
		hbox.getBody().getPosition().x - radius, hbox.getBody().getPosition().y - radius, 
		hbox.getBody().getPosition().x + radius, hbox.getBody().getPosition().y + radius);
	}
	
	public void coilPairActivated(final PlayState state, final Hitbox hbox1, final Hitbox hbox2) {
		
		final TeslaCoil tool = this;
		final Vector2 pos1 = new Vector2(hbox1.getBody().getPosition().scl(PPM));
		final Vector2 pos2 = new Vector2(hbox2.getBody().getPosition().scl(PPM));
		
		hbox1.setLifeSpan(activatedDuration);
		hbox2.setLifeSpan(activatedDuration);
		
		Hitbox new1 = new Hitbox(state, pos1.x, pos1.y, projectileWidth, projectileHeight, gravity, activatedDuration, projDura, 0, new Vector2(0, 0),
				hbox1.getFilter(), true, true, user);
		
		final Vector2 startVelocity = new Vector2(0, 0);
		
		float powerDiv = pos1.dst(pos2) / pulseSpeed;
		
		float xImpulse = -(pos1.x - pos2.x) / powerDiv;
		float yImpulse = -(pos1.y - pos2.y) / powerDiv;
		startVelocity.set(xImpulse, yImpulse);
		
		new1.addStrategy(new HitboxDefaultStrategy(state, new1, user.getBodyData(), false));
		new1.addStrategy(new HitboxStrategy(state, new1, user.getBodyData()) {
			
			private float controllerCount = 0;
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				
				controllerCount+=delta;

				if (controllerCount >= pulseInterval) {
					controllerCount -= pulseInterval;
					Hitbox pulse = new HitboxSprite(state, pos1.x, pos1.y,
							(int) (pos1.dst(pos2) * 2), pulseSize, gravity, 
							pulseInterval, projDura, 0, new Vector2(0, 0), hbox.getFilter(), true, true, user, projSprite) {
						
						@Override
						public void create() {
							super.create();
							//Rotate hitbox to match angle of fire.
							float newAngle = (float)(Math.atan2(startVelocity.y , startVelocity.x));
							Vector2 newPosition = this.body.getPosition().add(startVelocity.nor().scl(width / 2 / PPM));
							this.body.setTransform(newPosition.x, newPosition.y, newAngle);
						}
					};
					
					pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData(), false));
					pulse.addStrategy(new HitboxStaticStrategy(state, pulse, user.getBodyData()));
					pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, pulseDamage, 0, DamageTypes.RANGED));
				}
			}			
		});
	}
}
