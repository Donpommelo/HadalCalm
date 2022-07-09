package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy gives a hbox "damaging knockback". If an enemy is knocked back by the hbox, it will damage itself upon contact with a wall or unit (the other unit to be contacted will also be damaged)
 * The damage scales to the velocity of the victim.
 * @author Xilkner Xalgernon
 */
public class ContactUnitKnockbackDamage extends HitboxStrategy {
	
	//this is the lifespan of the hbox that gets created when the hbox contacts a unit
	private static final float lifespan = 1.0f;
	
	//this is the minimum speed the target must be moving to inflict damage
	private static final float speedThreshold = 30.0f;

	//this is the window of time before the effect activates. prevents it from instakilling a unit already touching a wall.
	private static final float procCd = 0.03f;
	
	//this is the maximum amount of damage that this effect can inflict
	private static final float maxDamage = 150.0f;

	//this is the effect/item/weapon source of the knockback
	private final DamageSource source;

	public ContactUnitKnockbackDamage(PlayState state, Hitbox proj, BodyData user, DamageSource source) {
		super(state, proj, user);
		this.source = source;
	}

	@Override
	public void onHit(HadalData fixB) {
		if (!state.isServer()) { return; }
		if (fixB != null) {
			if (UserDataType.BODY.equals(fixB.getType())) {

				final BodyData vic = (BodyData) fixB;
				
				//hbox size is slightly larger than target so it can contact walls/other units
				Vector2 hitboxSize = new Vector2();
				hitboxSize.set(vic.getSchmuck().getSize()).add(5, 5);
				
				Hitbox hbox = new Hitbox(state, new Vector2(), hitboxSize, lifespan, new Vector2(), creator.getSchmuck().getHitboxfilter(),
					true, true, creator.getSchmuck(), Sprite.NOTHING);
				hbox.makeUnreflectable();
				
				hbox.addStrategy(new ControllerDefault(state, hbox, creator));
				hbox.addStrategy(new FixedToEntity(state, hbox, creator, vic.getSchmuck(), new Vector2(), new Vector2()).setRotate(true));
				hbox.addStrategy(new HitboxStrategy(state, hbox, creator) {
					
					private float procCdCount;
					private float lastVelo;
					@Override
					public void controller(float delta) {
						if (procCdCount < procCd) {
							procCdCount += delta;
						}
						lastVelo = Math.min(vic.getSchmuck().getLinearVelocity().len2(), maxDamage);
					}
					
					@Override
					public void onHit(HadalData fixB) {
						if (procCdCount > procCd) {
							if (fixB != null && lastVelo > speedThreshold) {

								//contact a wall, damage the victim
								if (UserDataType.WALL.equals(fixB.getType())) {
									vic.receiveDamage(lastVelo, new Vector2(), creator, true, hbox, source, DamageTag.WHACKING);
									new ParticleEntity(state, hbox.getPixelPosition(), Particle.EXPLOSION, 1.0f,
										true, SyncType.CREATESYNC);
									hbox.die();
								}
								
								//contact another unit, damage both
								if (UserDataType.BODY.equals(fixB.getType()) && !fixB.equals(vic)) {
									vic.receiveDamage(lastVelo, new Vector2(), creator, true, hbox, source, DamageTag.WHACKING);
									fixB.receiveDamage(lastVelo, new Vector2(), creator, true, hbox, source, DamageTag.WHACKING);
									new ParticleEntity(state, hbox.getPixelPosition(), Particle.EXPLOSION, 1.0f,
										true, SyncType.CREATESYNC);
									hbox.die();
								}
							}
						}
					}
				});
			}
		}
	}
}
