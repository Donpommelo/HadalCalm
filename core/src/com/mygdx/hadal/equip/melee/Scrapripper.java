package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.DamageStatic;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.FixedToUser;
import com.mygdx.hadal.schmucks.strategies.ContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Scrapripper extends MeleeWeapon {

	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.0f;
	private final static float baseDamage = 50.0f;
	private final static Vector2 hitboxSize = new Vector2(200, 120);
	private final static float knockback = 25.0f;
	private final static float lifespan = 0.25f;
	
	private final static Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private final static Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	public Scrapripper(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new Hitbox(state, startPosition, hitboxSize, lifespan, startVelocity, filter, true, true, user, Sprite.IMPACT);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new FixedToUser(state, hbox, user.getBodyData(), startVelocity, startVelocity.nor().scl(hitboxSize.x / 2 / PPM), false));
		new ParticleEntity(state, hbox, Particle.EXPLOSION, 0.2f, 0.0f, true, particleSyncType.CREATESYNC);
	}
}
